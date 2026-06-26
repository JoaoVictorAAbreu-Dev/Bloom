package com.bloom.app.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloom.app.BloomAppContainer
import com.bloom.app.data.local.nowDayStartMillis
import com.bloom.app.domain.model.PomodoroMode
import com.bloom.app.domain.model.PomodoroSession
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class FocusViewModel(
    private val container: BloomAppContainer,
) : ViewModel() {
    private val mutableState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = mutableState.asStateFlow()

    private var timerJob: Job? = null
    private var focusStartedAtMillis: Long? = null
    private var completedFocusRounds = 0

    init {
        viewModelScope.launch {
            container.observePreferencesUseCase().collectLatest { preferences ->
                mutableState.update { current ->
                    val shouldResetDuration = !current.running && !current.paused
                    val nextRemainingSeconds = if (shouldResetDuration) {
                        when (current.mode) {
                            PomodoroMode.FOCUS -> preferences.focusMinutes * 60
                            PomodoroMode.SHORT_BREAK -> preferences.shortBreakMinutes * 60
                            PomodoroMode.LONG_BREAK -> preferences.longBreakMinutes * 60
                        }
                    } else {
                        current.remainingSeconds
                    }
                    current.copy(
                        focusMinutes = preferences.focusMinutes,
                        shortBreakMinutes = preferences.shortBreakMinutes,
                        longBreakMinutes = preferences.longBreakMinutes,
                        autoStartNextSession = preferences.autoStartNextSession,
                        remainingSeconds = nextRemainingSeconds,
                        progress = if (shouldResetDuration) 0f else current.progress,
                    )
                }
            }
        }

        viewModelScope.launch {
            container.observeStatisticsUseCase().collectLatest { statistics ->
                mutableState.update {
                    it.copy(
                        focusMinutesToday = statistics.focusMinutesToday,
                    )
                }
            }
        }

        viewModelScope.launch {
            container.observePomodoroSessionsUseCase().collectLatest { sessions ->
                val todayStart = nowDayStartMillis()
                mutableState.update {
                    it.copy(
                        sessionsToday = sessions.count { session ->
                            session.completed && session.mode == PomodoroMode.FOCUS && session.finishedAtMillis >= todayStart
                        },
                    )
                }
            }
        }
    }

    fun start() {
        if (mutableState.value.running) return
        if (mutableState.value.paused) {
            resume()
            return
        }

        if (mutableState.value.mode == PomodoroMode.FOCUS && focusStartedAtMillis == null) {
            focusStartedAtMillis = System.currentTimeMillis()
        }

        mutableState.update { it.copy(running = true, paused = false) }
        startTicker()
    }

    fun pause() {
        if (!mutableState.value.running) return
        cancelTicker()
        mutableState.update { it.copy(running = false, paused = true) }
    }

    fun resume() {
        if (!mutableState.value.paused) return
        mutableState.update { it.copy(running = true, paused = false) }
        startTicker()
    }

    fun stop() {
        cancelTicker()
        persistInterruptedSession()
        resetToFocus()
    }

    fun toggleDeepFocus(enabled: Boolean) {
        mutableState.update { it.copy(deepFocusEnabled = enabled) }
    }

    private fun startTicker() {
        cancelTicker()
        timerJob = viewModelScope.launch {
            while (mutableState.value.running) {
                delay(1_000)
                if (!tick()) {
                    break
                }
            }
        }
    }

    private suspend fun tick(): Boolean {
        val state = mutableState.value
        val nextRemaining = state.remainingSeconds - 1
        return if (nextRemaining <= 0) {
            completeCurrentStage()
        } else {
            val fullDuration = durationFor(state.mode)
            mutableState.update {
                it.copy(
                    remainingSeconds = nextRemaining,
                    progress = 1f - (nextRemaining / fullDuration.toFloat()),
                )
            }
            true
        }
    }

    private suspend fun completeCurrentStage(): Boolean {
        val state = mutableState.value
        val now = System.currentTimeMillis()
        val currentDuration = durationFor(state.mode)
        if (state.mode == PomodoroMode.FOCUS) {
            container.savePomodoroSessionUseCase(
                PomodoroSession(
                    id = UUID.randomUUID().toString(),
                    mode = PomodoroMode.FOCUS,
                    durationMinutes = currentDuration / 60,
                    startedAtMillis = focusStartedAtMillis ?: (now - currentDuration * 1_000L),
                    finishedAtMillis = now,
                    completed = true,
                ),
            )
            completedFocusRounds = (completedFocusRounds + 1).coerceAtMost(4)
        }

        val nextMode = nextMode(state.mode)
        if (state.mode == PomodoroMode.LONG_BREAK) {
            completedFocusRounds = 0
        }

        mutableState.update {
            it.copy(
                mode = nextMode,
                remainingSeconds = durationFor(nextMode),
                running = state.autoStartNextSession,
                paused = !state.autoStartNextSession,
                progress = 0f,
                round = (completedFocusRounds + 1).coerceAtMost(4),
            )
        }

        focusStartedAtMillis = if (nextMode == PomodoroMode.FOCUS) now else null

        return state.autoStartNextSession
    }

    private fun persistInterruptedSession() {
        val state = mutableState.value
        if (state.mode != PomodoroMode.FOCUS || focusStartedAtMillis == null) return

        val elapsedSeconds = durationFor(PomodoroMode.FOCUS) - state.remainingSeconds
        if (elapsedSeconds <= 0) return

        viewModelScope.launch {
            container.savePomodoroSessionUseCase(
                PomodoroSession(
                    id = UUID.randomUUID().toString(),
                    mode = PomodoroMode.FOCUS,
                    durationMinutes = (elapsedSeconds / 60).coerceAtLeast(1),
                    startedAtMillis = focusStartedAtMillis ?: System.currentTimeMillis(),
                    finishedAtMillis = System.currentTimeMillis(),
                    completed = false,
                ),
            )
        }
    }

    private fun resetToFocus() {
        focusStartedAtMillis = null
        completedFocusRounds = 0
        mutableState.update {
            it.copy(
                mode = PomodoroMode.FOCUS,
                remainingSeconds = durationFor(PomodoroMode.FOCUS),
                round = 1,
                running = false,
                paused = false,
                progress = 0f,
            )
        }
    }

    private fun nextMode(mode: PomodoroMode): PomodoroMode {
        return when (mode) {
            PomodoroMode.FOCUS -> if (completedFocusRounds >= 4) PomodoroMode.LONG_BREAK else PomodoroMode.SHORT_BREAK
            PomodoroMode.SHORT_BREAK -> PomodoroMode.FOCUS
            PomodoroMode.LONG_BREAK -> PomodoroMode.FOCUS
        }
    }

    private fun durationFor(mode: PomodoroMode, currentState: FocusUiState = mutableState.value): Int {
        return when (mode) {
            PomodoroMode.FOCUS -> currentState.focusMinutes * 60
            PomodoroMode.SHORT_BREAK -> currentState.shortBreakMinutes * 60
            PomodoroMode.LONG_BREAK -> currentState.longBreakMinutes * 60
        }
    }

    private fun cancelTicker() {
        timerJob?.cancel()
        timerJob = null
    }
}

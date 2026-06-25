package com.bloom.app.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloom.app.BloomAppContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class StatisticsViewModel(
    private val container: BloomAppContainer,
) : ViewModel() {
    val uiState = combine(
        container.observeStatisticsUseCase(),
        container.observePomodoroSessionsUseCase(),
    ) { statistics, sessions ->
        StatisticsUiState(
            statistics = statistics,
            sessions = sessions.take(12),
            rewardSummary = when {
                statistics.longestStreak >= 7 -> "Your garden is blooming beautifully."
                statistics.focusMinutesToday >= 45 -> "A strong focus day is taking root."
                statistics.habitsDoneToday > 0 -> "Small wins are stacking up."
                else -> "Start a gentle streak today."
            },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatisticsUiState())
}

package com.bloom.app.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloom.app.BloomAppContainer
import com.bloom.app.domain.model.BloomStatistics
import com.bloom.app.domain.model.Habit
import com.bloom.app.domain.model.PomodoroSession
import com.bloom.app.domain.model.Reward
import com.bloom.app.domain.model.ThemeMode
import com.bloom.app.domain.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val container: BloomAppContainer,
) : ViewModel() {
    private val resetInProgress = MutableStateFlow(false)
    private val exportSnapshot = MutableStateFlow("")
    private val statistics = container.observeStatisticsUseCase()

    val uiState = combine(
        container.observePreferencesUseCase(),
        statistics,
        container.observeRewardsUseCase(statistics),
        resetInProgress,
        exportSnapshot,
    ) { preferences, statistics, rewards, resetting, snapshot ->
        SettingsUiState(
            preferences = preferences,
            statistics = statistics,
            rewards = rewards,
            resetInProgress = resetting,
            exportSnapshot = snapshot,
            aiIntegration = CoachIntegrationUiState(
                configured = container.aiCoachRepository.isConfigured,
                modelId = container.aiCoachRepository.modelId,
                baseUrl = container.aiCoachRepository.baseUrl,
            ),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun updateName(name: String) {
        viewModelScope.launch {
            container.updatePreferencesUseCase { current ->
                current.copy(userName = name.takeIf { it.isNotBlank() } ?: current.userName)
            }
        }
    }

    fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            container.updateThemeModeUseCase(mode)
        }
    }

    fun updateFocusMinutes(value: Int) {
        viewModelScope.launch {
            container.updatePreferencesUseCase { current -> current.copy(focusMinutes = value.coerceIn(15, 60)) }
        }
    }

    fun updateShortBreakMinutes(value: Int) {
        viewModelScope.launch {
            container.updatePreferencesUseCase { current -> current.copy(shortBreakMinutes = value.coerceIn(3, 15)) }
        }
    }

    fun updateLongBreakMinutes(value: Int) {
        viewModelScope.launch {
            container.updatePreferencesUseCase { current -> current.copy(longBreakMinutes = value.coerceIn(10, 30)) }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            container.updatePreferencesUseCase { current -> current.copy(notificationsEnabled = enabled) }
        }
    }

    fun toggleAutoStart(enabled: Boolean) {
        viewModelScope.launch {
            container.updatePreferencesUseCase { current -> current.copy(autoStartNextSession = enabled) }
        }
    }

    fun exportData() {
        viewModelScope.launch {
            val preferences = container.observePreferencesUseCase().first()
            val habits = container.observeHabitsUseCase().first()
            val sessions = container.observePomodoroSessionsUseCase().first()
            val currentStatistics = container.observeStatisticsUseCase().first()
            val rewards = container.observeRewardsUseCase(statistics).first()
            exportSnapshot.value = buildExportSnapshot(
                preferences = preferences,
                habits = habits,
                sessions = sessions,
                statistics = currentStatistics,
                rewards = rewards,
            )
        }
    }

    fun clearExport() {
        exportSnapshot.value = ""
    }

    fun resetData() {
        viewModelScope.launch {
            resetInProgress.update { true }
            container.resetAllDataUseCase()
            resetInProgress.update { false }
        }
    }

    private fun buildExportSnapshot(
        preferences: UserPreferences,
        habits: List<Habit>,
        sessions: List<PomodoroSession>,
        statistics: BloomStatistics,
        rewards: List<Reward>,
    ): String {
        return buildString {
            appendLine("{")
            appendLine("  \"user\": {")
            appendLine("    \"name\": \"${preferences.userName.jsonEscaped()}\",")
            appendLine("    \"primaryGoal\": \"${preferences.primaryGoal.jsonEscaped()}\",")
            appendLine("    \"themeMode\": \"${preferences.themeMode.name}\"")
            appendLine("  },")
            appendLine("  \"statistics\": {")
            appendLine("    \"focusMinutesToday\": ${statistics.focusMinutesToday},")
            appendLine("    \"habitsDoneToday\": ${statistics.habitsDoneToday},")
            appendLine("    \"totalHabits\": ${statistics.totalHabits},")
            appendLine("    \"longestStreak\": ${statistics.longestStreak},")
            appendLine("    \"weeklyConsistency\": ${statistics.weeklyConsistency},")
            appendLine("    \"gardenGrowth\": ${statistics.gardenGrowth}")
            appendLine("  },")
            appendLine("  \"habits\": [")
            habits.forEachIndexed { index, habit ->
                appendLine("    {")
                appendLine("      \"id\": \"${habit.id.jsonEscaped()}\",")
                appendLine("      \"name\": \"${habit.name.jsonEscaped()}\",")
                appendLine("      \"category\": \"${habit.category.label.jsonEscaped()}\",")
                appendLine("      \"frequency\": \"${habit.frequency.label.jsonEscaped()}\",")
                appendLine("      \"priority\": \"${habit.priority.jsonEscaped()}\",")
                appendLine("      \"streak\": ${habit.streak},")
                appendLine("      \"completedToday\": ${habit.completedToday}")
                append("    }")
                appendLine(if (index < habits.lastIndex) "," else "")
            }
            appendLine("  ],")
            appendLine("  \"focusSessions\": ${sessions.size},")
            appendLine("  \"unlockedRewards\": ${rewards.count { it.unlocked }}")
            appendLine("}")
        }
    }

    private fun String.jsonEscaped(): String {
        return replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
    }
}

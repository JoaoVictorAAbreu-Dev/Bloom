package com.bloom.app.ui.state

import com.bloom.app.domain.model.BloomStatistics
import com.bloom.app.domain.model.Habit
import com.bloom.app.domain.model.HabitCategory
import com.bloom.app.domain.model.HabitFrequency
import com.bloom.app.domain.model.PomodoroMode
import com.bloom.app.domain.model.PomodoroSession
import com.bloom.app.domain.model.Reward
import com.bloom.app.domain.model.RoutineBlock
import com.bloom.app.domain.model.ThemeMode
import com.bloom.app.domain.model.UserPreferences

fun defaultUserPreferences() = UserPreferences(
    userName = "Alex",
    userEmail = "",
    primaryGoal = "Build consistency",
    themeMode = ThemeMode.SYSTEM,
    focusMinutes = 25,
    shortBreakMinutes = 5,
    longBreakMinutes = 15,
    autoStartNextSession = true,
    notificationsEnabled = true,
    onboardingCompleted = false,
    authCompleted = false,
    seedDataCreated = false,
)

data class RootUiState(
    val preferences: UserPreferences = defaultUserPreferences(),
)

data class HomeUiState(
    val userName: String = "Alex",
    val progress: Float = 0f,
    val progressLabel: String = "0 of 0 habits done",
    val focusLabel: String = "25:00",
    val focusSubtitle: String = "Deep Work Block",
    val focusProgress: Float = 0f,
    val habits: List<Habit> = emptyList(),
    val routineBlocks: List<RoutineBlock> = emptyList(),
    val statistics: BloomStatistics = BloomStatistics(
        focusMinutesToday = 0,
        habitsDoneToday = 0,
        totalHabits = 0,
        longestStreak = 0,
        weeklyConsistency = 0,
        weeklyFocusMinutes = emptyList(),
        weeklyHabitCompletions = emptyList(),
        gardenGrowth = 0,
    ),
)

data class HabitsUiState(
    val habits: List<Habit> = emptyList(),
    val selectedCategory: HabitCategory? = null,
)

data class HabitEditorUiState(
    val habitId: String? = null,
    val name: String = "",
    val category: HabitCategory = HabitCategory.HEALTH,
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val reminderHour: Int = 8,
    val reminderMinute: Int = 0,
    val colorArgb: Int = 0xFF8DAA91.toInt(),
    val iconKey: String = "watering_can",
    val priority: String = "Medium",
    val emoji: String = "",
    val dailyGoal: Int = 1,
    val weeklyGoal: Int = 5,
    val customRepeat: String = "",
    val createdAtMillis: Long = System.currentTimeMillis(),
    val loading: Boolean = false,
    val saving: Boolean = false,
)

data class FocusUiState(
    val mode: PomodoroMode = PomodoroMode.FOCUS,
    val remainingSeconds: Int = 25 * 60,
    val round: Int = 1,
    val totalRounds: Int = 4,
    val running: Boolean = false,
    val paused: Boolean = false,
    val progress: Float = 0f,
    val focusMinutesToday: Int = 0,
    val sessionsToday: Int = 0,
    val focusMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val autoStartNextSession: Boolean = true,
)

data class StatisticsUiState(
    val statistics: BloomStatistics = HomeUiState().statistics,
    val sessions: List<PomodoroSession> = emptyList(),
    val rewardSummary: String = "Grow a little every day.",
)

data class GardenUiState(
    val rewards: List<Reward> = emptyList(),
    val statistics: BloomStatistics = HomeUiState().statistics,
)

data class RoutineUiState(
    val routineBlocks: List<RoutineBlock> = emptyList(),
)

data class SettingsUiState(
    val preferences: UserPreferences = defaultUserPreferences(),
    val statistics: BloomStatistics = HomeUiState().statistics,
    val rewards: List<Reward> = emptyList(),
    val resetInProgress: Boolean = false,
    val aiIntegration: CoachIntegrationUiState = CoachIntegrationUiState(),
)

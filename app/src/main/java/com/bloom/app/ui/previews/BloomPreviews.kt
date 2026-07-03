package com.bloom.app.ui.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
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
import com.bloom.app.ui.components.BloomButton
import com.bloom.app.ui.components.BloomHabitCard
import com.bloom.app.ui.components.BloomProgressRing
import com.bloom.app.ui.components.BloomBottomBar
import com.bloom.app.ui.components.bloomBottomBarItems
import com.bloom.app.ui.screens.coach.CoachScreen
import com.bloom.app.ui.screens.focus.FocusScreen
import com.bloom.app.ui.screens.home.HomeScreen
import com.bloom.app.ui.screens.settings.SettingsScreen
import com.bloom.app.ui.screens.statistics.StatisticsScreen
import com.bloom.app.ui.state.CoachIntegrationUiState
import com.bloom.app.ui.state.CoachMessageRole
import com.bloom.app.ui.state.CoachMessageUi
import com.bloom.app.ui.state.CoachUiState
import com.bloom.app.ui.state.FocusUiState
import com.bloom.app.ui.state.HomeUiState
import com.bloom.app.ui.state.HabitsUiState
import com.bloom.app.ui.state.SettingsUiState
import com.bloom.app.ui.state.StatisticsUiState
import com.bloom.app.ui.theme.BloomTheme

@Preview(showBackground = true)
@Composable
private fun BloomButtonPreview() {
    BloomTheme(darkTheme = false) {
        BloomButton(text = "Save Habit", onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun BloomHabitCardPreview() {
    BloomTheme(darkTheme = false) {
        BloomHabitCard(
            habit = Habit(
                id = "1",
                name = "Water Plants",
                category = HabitCategory.HEALTH,
                frequency = HabitFrequency.DAILY,
                reminderHour = 9,
                reminderMinute = 0,
                colorArgb = 0xFF8DAA91.toInt(),
                iconKey = "watering_can",
                createdAtMillis = System.currentTimeMillis(),
                streak = 3,
                completedToday = false,
                completionCount = 12,
            ),
            onToggle = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BloomProgressRingPreview() {
    BloomTheme(darkTheme = false) {
        BloomProgressRing(progress = 0.65f) {
            androidx.compose.material3.Text(text = "65%")
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun HomeScreenPreview() {
    BloomTheme(darkTheme = false) {
        HomeScreen(
            uiState = HomeUiState(
                userName = "Alex",
                progress = 0.65f,
                progressLabel = "5 of 8 habits done",
                focusLabel = "25:00",
                focusSubtitle = "Deep Work Block",
                focusProgress = 0.42f,
                habits = listOf(
                    Habit(
                        id = "1",
                        name = "Water Plants",
                        category = HabitCategory.HEALTH,
                        frequency = HabitFrequency.DAILY,
                        reminderHour = 9,
                        reminderMinute = 0,
                        colorArgb = 0xFF8DAA91.toInt(),
                        iconKey = "watering_can",
                        createdAtMillis = System.currentTimeMillis(),
                        streak = 3,
                        completedToday = false,
                        completionCount = 12,
                    ),
                ),
                routineBlocks = listOf(
                    RoutineBlock("morning", "Morning", "Water Plants + 1 more", "Morning", 30, true, 0xFF8DAA91.toInt(), "sun"),
                ),
                statistics = BloomStatistics(
                    focusMinutesToday = 24,
                    habitsDoneToday = 5,
                    totalHabits = 8,
                    longestStreak = 12,
                    weeklyConsistency = 71,
                    weeklyFocusMinutes = listOf(5, 15, 25, 0, 35, 20, 10),
                    weeklyHabitCompletions = listOf(1, 2, 4, 0, 5, 3, 2),
                    monthlyFocusMinutes = listOf(90, 120, 75, 160),
                    monthlyHabitCompletions = List(21) { it % 3 } + listOf(1, 2, 4, 0, 5, 3, 2),
                    averageFocusMinutes = 24,
                    mostProductiveHourLabel = "19:00",
                    topHabitName = "Water Plants",
                    gardenGrowth = 48,
                ),
            ),
            onHabitToggle = {},
            onOpenHabits = {},
            onOpenFocus = {},
            onOpenRoutine = {},
            onOpenGarden = {},
            onOpenCoach = {},
            onNotificationsClick = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun FocusScreenPreview() {
    BloomTheme(darkTheme = false) {
        FocusScreen(
            uiState = FocusUiState(
                mode = PomodoroMode.FOCUS,
                remainingSeconds = 25 * 60,
                round = 2,
                totalRounds = 4,
                running = true,
                progress = 0.42f,
                focusMinutesToday = 24,
                sessionsToday = 2,
                focusMinutes = 25,
                shortBreakMinutes = 5,
                longBreakMinutes = 15,
                autoStartNextSession = true,
                deepFocusEnabled = true,
            ),
            onStart = {},
            onPause = {},
            onResume = {},
            onStop = {},
            onDeepFocusToggle = {},
            onNotificationsClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StatisticsScreenPreview() {
    BloomTheme(darkTheme = false) {
        StatisticsScreen(
            uiState = StatisticsUiState(
                statistics = BloomStatistics(
                    focusMinutesToday = 24,
                    habitsDoneToday = 5,
                    totalHabits = 8,
                    longestStreak = 12,
                    weeklyConsistency = 71,
                    weeklyFocusMinutes = listOf(5, 15, 25, 0, 35, 20, 10),
                    weeklyHabitCompletions = listOf(1, 2, 4, 0, 5, 3, 2),
                    monthlyFocusMinutes = listOf(90, 120, 75, 160),
                    monthlyHabitCompletions = List(21) { it % 3 } + listOf(1, 2, 4, 0, 5, 3, 2),
                    averageFocusMinutes = 24,
                    mostProductiveHourLabel = "19:00",
                    topHabitName = "Water Plants",
                    gardenGrowth = 48,
                ),
                sessions = listOf(
                    PomodoroSession("1", PomodoroMode.FOCUS, 25, 0, 1, true),
                ),
            ),
            onNotificationsClick = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SettingsScreenPreview() {
    BloomTheme(darkTheme = false) {
        SettingsScreen(
            uiState = SettingsUiState(
                preferences = UserPreferences(
                    userName = "Alex",
                    userEmail = "alex@local",
                    primaryGoal = "Build consistency",
                    themeMode = ThemeMode.SYSTEM,
                    focusMinutes = 25,
                    shortBreakMinutes = 5,
                    longBreakMinutes = 15,
                    autoStartNextSession = true,
                    notificationsEnabled = true,
                    bloomCoachEnabled = true,
                    allowHabitContextForAi = false,
                    onboardingCompleted = true,
                    authCompleted = true,
                    seedDataCreated = true,
            ),
            resetInProgress = false,
            importInProgress = false,
        ),
            onNameChange = {},
            onThemeChange = { _ -> },
            onFocusMinutesChange = {},
            onShortBreakMinutesChange = {},
            onLongBreakMinutesChange = {},
            onNotificationsToggle = {},
            onAutoStartToggle = {},
            onBloomCoachToggle = {},
            onHabitContextForAiToggle = {},
            onExportData = {},
            onSaveExport = {},
            onImportExport = {},
            onShareExport = {},
            onClearExport = {},
            onResetData = {},
            onOpenCoach = {},
            onNotificationsClick = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun CoachScreenPreview() {
    BloomTheme(darkTheme = false) {
        CoachScreen(
            uiState = CoachUiState(
                integration = CoachIntegrationUiState(
                    configured = true,
                    modelId = "groq/compound-mini",
                    baseUrl = "https://api.groq.com/openai/v1",
                ),
                contextSummary = listOf(
                    "2/3 hábitos concluídos hoje",
                    "25 min de foco hoje",
                    "Maior streak: 7 dias",
                    "Consistência semanal: 80%",
                ),
                quickActions = listOf(
                    com.bloom.app.domain.model.AiCoachQuickAction("Planejar meu dia", "Plan my day"),
                    com.bloom.app.domain.model.AiCoachQuickAction("Revisar hábitos", "Review habits"),
                ),
                messages = listOf(
                    CoachMessageUi(
                        id = "1",
                        role = CoachMessageRole.ASSISTANT,
                        text = "Olá, Ana. Posso te ajudar a ajustar seu foco de hoje.",
                    ),
                    CoachMessageUi(
                        id = "2",
                        role = CoachMessageRole.USER,
                        text = "Me ajude a organizar a tarde.",
                    ),
                ),
                input = "",
                sending = false,
            ),
            onInputChange = { _ -> },
            onSend = { _ -> },
            onQuickAction = { _ -> },
            onNotificationsClick = {},
        )
    }
}

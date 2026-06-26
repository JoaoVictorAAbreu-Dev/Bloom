package com.bloom.app.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloom.app.BloomAppContainer
import com.bloom.app.domain.model.AiCoachContext
import com.bloom.app.domain.model.AiCoachReply
import com.bloom.app.domain.model.AiCoachSource
import com.bloom.app.domain.model.Habit
import com.bloom.app.domain.model.RoutineBlock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.UUID

class CoachViewModel(
    private val container: BloomAppContainer,
) : ViewModel() {
    private val messages = MutableStateFlow<List<CoachMessageUi>>(emptyList())
    private val input = MutableStateFlow("")
    private val sending = MutableStateFlow(false)
    private val lastError = MutableStateFlow<String?>(null)

    private val statisticsFlow = container.observeStatisticsUseCase()

    private val contextState = combine(
        container.observePreferencesUseCase(),
        container.observeHabitsUseCase(),
        statisticsFlow,
        container.observePomodoroSessionsUseCase(),
        container.observeRewardsUseCase(statisticsFlow),
    ) { preferences, habits, statistics, sessions, rewards ->
        AiCoachContext(
            userName = preferences.userName,
            preferences = preferences,
            habits = habits,
            routineBlocks = buildRoutineBlocks(habits),
            statistics = statistics,
            rewardsUnlocked = rewards.count { it.unlocked },
            recentSessions = sessions.take(5),
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AiCoachContext(
            userName = "Alex",
            preferences = defaultUserPreferences(),
            habits = emptyList(),
            routineBlocks = emptyList(),
            statistics = HomeUiState().statistics,
            rewardsUnlocked = 0,
            recentSessions = emptyList(),
        ),
    )

    val uiState = combine(contextState, messages, input, sending, lastError) { context, messageList, currentInput, isSending, error ->
        val visibleMessages = if (messageList.isEmpty()) {
            listOf(
                CoachMessageUi(
                    id = "welcome",
                    role = CoachMessageRole.ASSISTANT,
                    text = welcomeText(context),
                    source = AiCoachSource.LOCAL,
                ),
            )
        } else {
            messageList
        }

        CoachUiState(
            integration = CoachIntegrationUiState(
                configured = container.aiCoachRepository.isConfigured,
                modelId = container.aiCoachRepository.modelId,
                baseUrl = container.aiCoachRepository.baseUrl,
            ),
            contextSummary = buildContextSummary(context),
            weeklySummary = buildWeeklySummary(context),
            monthlySummary = buildMonthlySummary(context),
            nextBestAction = buildNextBestAction(context),
            recommendations = buildRecommendations(context),
            quickActions = container.aiCoachRepository.buildQuickActions(context),
            messages = visibleMessages,
            input = currentInput,
            sending = isSending,
            errorMessage = error,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        CoachUiState(),
    )

    fun updateInput(value: String) {
        input.value = value
    }

    fun send(prompt: String? = null) {
        viewModelScope.launch {
            val userPrompt = (prompt ?: input.value).trim()
            if (userPrompt.isBlank() || sending.value) return@launch

            input.value = ""
            sending.value = true
            lastError.value = null

            messages.update { current ->
                current + CoachMessageUi(
                    id = UUID.randomUUID().toString(),
                    role = CoachMessageRole.USER,
                    text = userPrompt,
                )
            }

            val context = contextState.value
            val result = try {
                container.generateAiCoachReplyUseCase(context, userPrompt)
            } catch (_: Throwable) {
                lastError.value = "Could not reach Groq now. Using local guidance."
                AiCoachReply(
                    text = localFallback(context, userPrompt),
                    source = AiCoachSource.LOCAL,
                    modelId = container.aiCoachRepository.modelId,
                )
            }

            messages.update { current ->
                current + CoachMessageUi(
                    id = UUID.randomUUID().toString(),
                    role = CoachMessageRole.ASSISTANT,
                    text = result.text,
                    source = result.source,
                )
            }

            sending.value = false
        }
    }

    fun clearError() {
        lastError.value = null
    }

    private fun welcomeText(context: AiCoachContext): String {
        val openHabits = context.habits.count { !it.completedToday }
        return buildString {
            append("Hi, ${context.userName}. ")
            append("I can plan your day, review habits, shape a Pomodoro block, or close the week. ")
            append("Today you have $openHabits pending habit(s) and ${context.statistics.focusMinutesToday} focus minutes completed.")
        }
    }

    private fun buildContextSummary(context: AiCoachContext): List<String> {
        return listOf(
            "${context.statistics.habitsDoneToday}/${context.statistics.totalHabits} habits completed today",
            "${context.statistics.focusMinutesToday} focus minutes today",
            "Longest streak: ${context.statistics.longestStreak} days",
            "Weekly consistency: ${context.statistics.weeklyConsistency}%",
            "Garden: ${context.rewardsUnlocked} rewards unlocked",
        )
    }

    private fun buildWeeklySummary(context: AiCoachContext): String {
        val activeDays = context.statistics.weeklyHabitCompletions.count { it > 0 }
        val focusMinutes = context.statistics.weeklyFocusMinutes.sum()
        return "$activeDays active days, $focusMinutes focus minutes, ${context.statistics.weeklyConsistency}% consistency."
    }

    private fun buildMonthlySummary(context: AiCoachContext): String {
        val activeDays = context.statistics.monthlyHabitCompletions.count { it > 0 }
        val focusMinutes = context.statistics.monthlyFocusMinutes.sum()
        return "$activeDays active days in 28 days, $focusMinutes focus minutes, top habit: ${context.statistics.topHabitName}."
    }

    private fun buildNextBestAction(context: AiCoachContext): String {
        val pendingHabit = context.habits.firstOrNull { !it.completedToday }
        return when {
            pendingHabit != null -> "Complete '${pendingHabit.name}' next to protect today's rhythm."
            context.statistics.focusMinutesToday < context.preferences.focusMinutes -> "Start one ${context.preferences.focusMinutes}-minute focus round."
            else -> "Review tomorrow and keep the streak light."
        }
    }

    private fun buildRecommendations(context: AiCoachContext): List<String> {
        val recommendations = mutableListOf<String>()
        if (context.statistics.weeklyConsistency < 50 && context.habits.isNotEmpty()) {
            recommendations += "Reduce one low-priority habit or move it to a better time window."
        }
        if (context.statistics.mostProductiveHourLabel != "No focus yet") {
            recommendations += "Schedule deep work near ${context.statistics.mostProductiveHourLabel}, your strongest focus window."
        }
        if (context.statistics.habitsDoneToday + 1 == context.statistics.totalHabits && context.statistics.totalHabits > 0) {
            recommendations += "Finish one more habit to complete today's garden progress."
        }
        if (context.statistics.averageFocusMinutes > 0 && context.statistics.averageFocusMinutes < context.preferences.focusMinutes) {
            recommendations += "Use a shorter warm-up round before the full Pomodoro."
        }
        if (recommendations.isEmpty()) {
            recommendations += "Keep today's plan simple: one habit, one focus block, one short review."
        }
        return recommendations.take(4)
    }

    private fun localFallback(context: AiCoachContext, userPrompt: String): String {
        val nextHabit = context.habits.firstOrNull { !it.completedToday }?.name
            ?: context.habits.firstOrNull()?.name
            ?: "a light habit"
        val activeRoutine = context.routineBlocks.firstOrNull { it.active } ?: context.routineBlocks.firstOrNull()

        return buildString {
            appendLine("Here is a simple next step:")
            appendLine("- Do ${context.preferences.focusMinutes} minutes of focus on $nextHabit.")
            if (context.statistics.habitsDoneToday < context.statistics.totalHabits) {
                appendLine("- Complete one more habit to keep the rhythm.")
            }
            if (context.statistics.focusMinutesToday == 0) {
                appendLine("- Start with 5 minutes of warm-up before the main focus block.")
            }
            if (activeRoutine != null) {
                appendLine("- The natural next routine is ${activeRoutine.slot}: ${activeRoutine.title}.")
            }
            buildRecommendations(context).forEach { recommendation ->
                appendLine("- $recommendation")
            }
            if (userPrompt.isNotBlank()) {
                appendLine("- Request: ${userPrompt.take(120)}")
            }
        }.trim()
    }

    private fun buildRoutineBlocks(habits: List<Habit>): List<RoutineBlock> {
        val now = LocalTime.now()
        return listOf(
            RoutineBlock(
                id = "morning",
                title = "Morning",
                subtitle = habits.firstOrNull()?.name ?: "Start light",
                slot = "Morning",
                durationMinutes = 30,
                active = now.hour in 5..11,
                colorArgb = 0xFFB4D2C0.toInt(),
                iconKey = "sun",
            ),
            RoutineBlock(
                id = "afternoon",
                title = "Afternoon",
                subtitle = habits.getOrNull(1)?.name ?: "Return to focus",
                slot = "Afternoon",
                durationMinutes = 30,
                active = now.hour in 12..16,
                colorArgb = 0xFFC6E9E9.toInt(),
                iconKey = "cloud",
            ),
            RoutineBlock(
                id = "evening",
                title = "Evening",
                subtitle = habits.getOrNull(2)?.name ?: "Slow down with intention",
                slot = "Evening",
                durationMinutes = 25,
                active = now.hour in 17..20,
                colorArgb = 0xFFAE98D6.toInt(),
                iconKey = "moon",
            ),
            RoutineBlock(
                id = "night",
                title = "Night",
                subtitle = "Prepare tomorrow",
                slot = "Night",
                durationMinutes = 20,
                active = now.hour !in 5..20,
                colorArgb = 0xFFD9A441.toInt(),
                iconKey = "star",
            ),
        )
    }
}

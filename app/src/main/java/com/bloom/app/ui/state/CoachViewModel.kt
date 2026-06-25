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
import java.util.UUID
import java.time.LocalTime

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
                lastError.value = "Não foi possível consultar o Groq agora. Usando orientação local."
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
            append("Olá, ${context.userName}. ")
            append("Posso planejar seu dia, revisar hábitos, montar um Pomodoro ou fechar a semana. ")
            append("Hoje você tem $openHabits hábito(s) pendente(s) e ${context.statistics.focusMinutesToday} min de foco concluídos.")
        }
    }

    private fun buildContextSummary(context: AiCoachContext): List<String> {
        return listOf(
            "${context.statistics.habitsDoneToday}/${context.statistics.totalHabits} hábitos concluídos hoje",
            "${context.statistics.focusMinutesToday} min de foco hoje",
            "Maior streak: ${context.statistics.longestStreak} dias",
            "Consistência semanal: ${context.statistics.weeklyConsistency}%",
            "Jardim: ${context.rewardsUnlocked} recompensas desbloqueadas",
        )
    }

    private fun localFallback(context: AiCoachContext, userPrompt: String): String {
        val nextHabit = context.habits.firstOrNull { !it.completedToday }?.name
            ?: context.habits.firstOrNull()?.name
            ?: "um hábito leve"
        val activeRoutine = context.routineBlocks.firstOrNull { it.active } ?: context.routineBlocks.firstOrNull()

        return buildString {
            appendLine("Posso te ajudar com um passo simples:")
            appendLine("• Faça ${context.preferences.focusMinutes} min de foco em $nextHabit.")
            if (context.statistics.habitsDoneToday < context.statistics.totalHabits) {
                appendLine("• Complete mais um hábito para manter o ritmo.")
            }
            if (context.statistics.focusMinutesToday == 0) {
                appendLine("• Comece com 5 min de aquecimento antes do foco principal.")
            }
            if (activeRoutine != null) {
                appendLine("• A próxima rotina natural é ${activeRoutine.slot}: ${activeRoutine.title}.")
            }
            if (userPrompt.isNotBlank()) {
                appendLine("• Pedido: ${userPrompt.take(120)}")
            }
        }.trim()
    }

    private fun buildRoutineBlocks(habits: List<Habit>): List<RoutineBlock> {
        val now = LocalTime.now()
        return listOf(
            RoutineBlock(
                id = "morning",
                title = "Morning",
                subtitle = habits.firstOrNull()?.name ?: "Comece leve",
                slot = "Morning",
                durationMinutes = 30,
                active = now.hour in 5..11,
                colorArgb = 0xFFB4D2C0.toInt(),
                iconKey = "sun",
            ),
            RoutineBlock(
                id = "afternoon",
                title = "Afternoon",
                subtitle = habits.getOrNull(1)?.name ?: "Retome o foco",
                slot = "Afternoon",
                durationMinutes = 30,
                active = now.hour in 12..16,
                colorArgb = 0xFFC6E9E9.toInt(),
                iconKey = "cloud",
            ),
            RoutineBlock(
                id = "evening",
                title = "Evening",
                subtitle = habits.getOrNull(2)?.name ?: "Desacelere com intenção",
                slot = "Evening",
                durationMinutes = 25,
                active = now.hour in 17..20,
                colorArgb = 0xFFAE98D6.toInt(),
                iconKey = "moon",
            ),
            RoutineBlock(
                id = "night",
                title = "Night",
                subtitle = "Prepare amanhã",
                slot = "Night",
                durationMinutes = 20,
                active = now.hour !in 5..20,
                colorArgb = 0xFFD9A441.toInt(),
                iconKey = "star",
            ),
        )
    }
}

package com.bloom.app.data.repository

import com.bloom.app.data.remote.GroqAiService
import com.bloom.app.domain.model.AiCoachContext
import com.bloom.app.domain.model.AiCoachQuickAction
import com.bloom.app.domain.model.AiCoachReply
import com.bloom.app.domain.model.AiCoachSource
import com.bloom.app.domain.repository.AiCoachRepository
import com.bloom.app.domain.usecase.BuildAiCoachPromptUseCase
import java.io.IOException

class AiCoachRepositoryImpl(
    private val groqAiService: GroqAiService,
    private val buildAiCoachPromptUseCase: BuildAiCoachPromptUseCase,
) : AiCoachRepository {
    override val modelId: String = groqAiService.modelId
    override val baseUrl: String = groqAiService.baseUrl
    override val isConfigured: Boolean = groqAiService.isConfigured

    override suspend fun generateReply(context: AiCoachContext, userPrompt: String): AiCoachReply {
        val prompt = buildAiCoachPromptUseCase(context, userPrompt)
        if (!isConfigured) {
            return offlineReply(context, userPrompt)
        }

        return runCatching {
            val text = groqAiService.generateReply(prompt)
            AiCoachReply(
                text = text,
                source = AiCoachSource.GROQ,
                modelId = modelId,
            )
        }.getOrElse { error ->
            if (error is IOException) {
                offlineReply(context, userPrompt)
            } else {
                offlineReply(context, userPrompt)
            }
        }
    }

    override fun buildQuickActions(context: AiCoachContext): List<AiCoachQuickAction> {
        val remainingHabits = context.habits.filterNot { it.completedToday }.take(2)
        val nextHabit = remainingHabits.firstOrNull()?.name ?: context.habits.firstOrNull()?.name ?: "um hábito leve"
        val focusMinutes = context.preferences.focusMinutes
        val totalHabits = context.statistics.totalHabits
        val doneHabits = context.statistics.habitsDoneToday

        return listOf(
            AiCoachQuickAction(
                label = "Planejar meu dia",
                prompt = "Me ajude a planejar meu dia com foco em ${nextHabit} e um Pomodoro de ${focusMinutes} minutos.",
            ),
            AiCoachQuickAction(
                label = "Revisar hábitos",
                prompt = "Analise meus hábitos de hoje. Tenho $doneHabits de $totalHabits concluídos e diga qual é o próximo passo mais inteligente.",
            ),
            AiCoachQuickAction(
                label = "Preparar foco",
                prompt = "Quero entrar em foco agora. Sugira como começar a sessão e manter um ritmo calmo por ${focusMinutes} minutos.",
            ),
            AiCoachQuickAction(
                label = "Fechar o dia",
                prompt = "Faça uma revisão curta do meu dia, com 3 pontos: progresso, ajuste e incentivo para amanhã.",
            ),
        )
    }

    private fun offlineReply(context: AiCoachContext, userPrompt: String): AiCoachReply {
        val nextHabit = context.habits.firstOrNull { !it.completedToday }?.name
            ?: context.habits.firstOrNull()?.name
            ?: "um hábito leve"
        val topRoutine = context.routineBlocks.firstOrNull { it.active } ?: context.routineBlocks.firstOrNull()

        val reply = buildString {
            appendLine("Posso te ajudar com um próximo passo simples:")
            appendLine("• Foque em $nextHabit por ${context.preferences.focusMinutes} min.")
            if (context.statistics.habitsDoneToday < context.statistics.totalHabits) {
                appendLine("• Conclua mais um hábito para manter o jardim crescendo.")
            }
            if (context.statistics.focusMinutesToday == 0) {
                appendLine("• Comece com 5 min de aquecimento para entrar no ritmo.")
            }
            if (topRoutine != null) {
                appendLine("• A próxima janela natural parece ser ${topRoutine.slot.lowercase()}: ${topRoutine.title}.")
            }
            if (userPrompt.isNotBlank()) {
                appendLine("• Pedido recebido: ${userPrompt.take(120)}")
            }
        }.trim()

        return AiCoachReply(
            text = reply,
            source = AiCoachSource.LOCAL,
            modelId = modelId,
        )
    }
}


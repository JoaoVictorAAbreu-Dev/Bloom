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
        val nextHabit = context.habits.firstOrNull { !it.completedToday }?.name
            ?: context.habits.firstOrNull()?.name
            ?: "a light habit"
        val focusMinutes = context.preferences.focusMinutes
        val totalHabits = context.statistics.totalHabits
        val doneHabits = context.statistics.habitsDoneToday

        return listOf(
            AiCoachQuickAction(
                label = "Plan my day",
                prompt = "Help me plan today around $nextHabit and one $focusMinutes-minute Pomodoro.",
            ),
            AiCoachQuickAction(
                label = "Review habits",
                prompt = "Analyze my habits today. I completed $doneHabits of $totalHabits. What is the smartest next step?",
            ),
            AiCoachQuickAction(
                label = "Prepare focus",
                prompt = "I want to focus now. Suggest how to start and keep a calm rhythm for $focusMinutes minutes.",
            ),
            AiCoachQuickAction(
                label = "Weekly summary",
                prompt = "Give me a short weekly summary with progress, adjustment, and one next goal.",
            ),
        )
    }

    private fun offlineReply(context: AiCoachContext, userPrompt: String): AiCoachReply {
        val nextHabit = context.habits.firstOrNull { !it.completedToday }?.name
            ?: context.habits.firstOrNull()?.name
            ?: "a light habit"
        val topRoutine = context.routineBlocks.firstOrNull { it.active } ?: context.routineBlocks.firstOrNull()

        val reply = buildString {
            appendLine("Here is a simple next step:")
            appendLine("- Focus on $nextHabit for ${context.preferences.focusMinutes} minutes.")
            if (context.statistics.habitsDoneToday < context.statistics.totalHabits) {
                appendLine("- Complete one more habit to keep the garden growing.")
            }
            if (context.statistics.focusMinutesToday == 0) {
                appendLine("- Start with a 5-minute warm-up to enter the rhythm.")
            }
            if (context.statistics.mostProductiveHourLabel != "No focus yet") {
                appendLine("- Your strongest focus window appears near ${context.statistics.mostProductiveHourLabel}.")
            }
            if (topRoutine != null) {
                appendLine("- The next natural routine window is ${topRoutine.slot.lowercase()}: ${topRoutine.title}.")
            }
            if (userPrompt.isNotBlank()) {
                appendLine("- Request received: ${userPrompt.take(120)}")
            }
        }.trim()

        return AiCoachReply(
            text = reply,
            source = AiCoachSource.LOCAL,
            modelId = modelId,
        )
    }
}

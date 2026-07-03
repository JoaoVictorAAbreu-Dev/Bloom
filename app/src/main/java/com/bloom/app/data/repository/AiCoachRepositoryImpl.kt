package com.bloom.app.data.repository

import com.bloom.app.data.remote.AiGateway
import com.bloom.app.domain.model.AiCoachContext
import com.bloom.app.domain.model.AiCoachQuickAction
import com.bloom.app.domain.model.AiCoachReply
import com.bloom.app.domain.model.AiCoachSource
import com.bloom.app.domain.repository.AiCoachRepository
import com.bloom.app.domain.usecase.BuildAiCoachPromptUseCase
import com.bloom.app.security.PrivacySanitizer
import java.io.IOException

class AiCoachRepositoryImpl(
    private val aiGateway: AiGateway,
    private val buildAiCoachPromptUseCase: BuildAiCoachPromptUseCase,
) : AiCoachRepository {
    private val privacySanitizer = PrivacySanitizer()

    override val modelId: String = aiGateway.modelId
    override val baseUrl: String = aiGateway.baseUrl
    override val isConfigured: Boolean = aiGateway.isConfigured

    override suspend fun generateReply(context: AiCoachContext, userPrompt: String): AiCoachReply {
        if (!context.preferences.bloomCoachEnabled) {
            return AiCoachReply(
                text = "Bloom Coach is disabled. Enable it in Settings > Privacy and AI to use AI suggestions.",
                source = AiCoachSource.LOCAL,
                modelId = modelId,
            )
        }

        val prompt = buildAiCoachPromptUseCase(context, userPrompt)
        if (!isConfigured) {
            return offlineReply(context, userPrompt)
        }

        return runCatching {
            val text = aiGateway.generateReply(prompt)
            AiCoachReply(
                text = text,
                source = if (aiGateway.modelId == BACKEND_MANAGED_MODEL) AiCoachSource.REMOTE else AiCoachSource.GROQ,
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
                prompt = "Help me plan today around ${privacySanitizer.sanitize(nextHabit)} and one $focusMinutes-minute Pomodoro.",
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
                appendLine("- The next natural routine window is ${topRoutine.slot.lowercase()}: ${privacySanitizer.sanitize(topRoutine.title)}.")
            }
            if (userPrompt.isNotBlank()) {
                appendLine("- Request received: ${privacySanitizer.sanitize(userPrompt).take(120)}")
            }
        }.trim()

        return AiCoachReply(
            text = reply,
            source = AiCoachSource.LOCAL,
            modelId = modelId,
        )
    }

    private companion object {
        const val BACKEND_MANAGED_MODEL = "backend-managed"
    }
}

package com.bloom.app.domain.usecase

import com.bloom.app.domain.model.AiCoachContext
import com.bloom.app.domain.model.AiCoachPrompt

class BuildAiCoachPromptUseCase {
    operator fun invoke(
        context: AiCoachContext,
        userPrompt: String,
    ): AiCoachPrompt {
        val systemPrompt = """
            You are Bloom Coach, a calm, premium productivity assistant for the Bloom app.
            Reply in the same language as the user. Default to Brazilian Portuguese when unclear.
            Keep the response concise, warm, and actionable.
            Use at most 3 short bullet points unless the user asks for more detail.
            Focus on habits, routine planning, Pomodoro timing, weekly review, and gentle accountability.
            Do not mention hidden chain-of-thought.
        """.trimIndent()

        val userContext = buildString {
            appendLine("User: ${context.userName}")
            appendLine("Focus plan: ${context.preferences.focusMinutes} min focus, ${context.preferences.shortBreakMinutes} min short break, ${context.preferences.longBreakMinutes} min long break")
            appendLine("Habits today: ${context.statistics.habitsDoneToday}/${context.statistics.totalHabits}")
            appendLine("Longest streak: ${context.statistics.longestStreak}")
            appendLine("Weekly consistency: ${context.statistics.weeklyConsistency}%")
            appendLine("Focus minutes today: ${context.statistics.focusMinutesToday}")
            appendLine("Average focus session: ${context.statistics.averageFocusMinutes} minutes")
            appendLine("Most productive hour: ${context.statistics.mostProductiveHourLabel}")
            appendLine("Top completed habit: ${context.statistics.topHabitName}")
            appendLine("Monthly focus minutes by week: ${context.statistics.monthlyFocusMinutes.joinToString()}")
            appendLine("Monthly habit completions by day: ${context.statistics.monthlyHabitCompletions.joinToString()}")
            appendLine("Garden growth: ${context.statistics.gardenGrowth}")
            appendLine("Unlocked rewards: ${context.rewardsUnlocked}")
            appendLine("Active habits:")
            context.habits.take(6).forEach { habit ->
                appendLine("- ${habit.name} (${habit.category.name.lowercase()}) streak ${habit.streak}${if (habit.completedToday) ", completed today" else ""}")
            }
            if (context.habits.isEmpty()) {
                appendLine("- No habits yet")
            }
            appendLine("Routine blocks:")
            context.routineBlocks.forEach { block ->
                appendLine("- ${block.slot}: ${block.title} - ${block.subtitle}${if (block.active) " [active]" else ""}")
            }
            appendLine("Recent sessions: ${context.recentSessions.size}")
            if (userPrompt.isNotBlank()) {
                appendLine("User request: $userPrompt")
            }
        }.trim()

        return AiCoachPrompt(
            systemPrompt = systemPrompt,
            userPrompt = userContext,
        )
    }
}

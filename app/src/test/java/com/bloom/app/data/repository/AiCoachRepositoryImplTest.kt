package com.bloom.app.data.repository

import com.bloom.app.data.remote.GroqAiGateway
import com.bloom.app.data.remote.GroqAiService
import com.bloom.app.data.remote.AiGateway
import com.bloom.app.domain.model.AiCoachPrompt
import com.bloom.app.domain.model.AiCoachContext
import com.bloom.app.domain.model.AiCoachSource
import com.bloom.app.domain.model.BloomStatistics
import com.bloom.app.domain.model.Habit
import com.bloom.app.domain.model.HabitCategory
import com.bloom.app.domain.model.HabitFrequency
import com.bloom.app.domain.model.PomodoroMode
import com.bloom.app.domain.model.PomodoroSession
import com.bloom.app.domain.model.RoutineBlock
import com.bloom.app.domain.model.ThemeMode
import com.bloom.app.domain.model.UserPreferences
import com.bloom.app.domain.usecase.BuildAiCoachPromptUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AiCoachRepositoryImplTest {
    private val repository = AiCoachRepositoryImpl(
        aiGateway = GroqAiGateway(GroqAiService(apiKeyProvider = { "" })),
        buildAiCoachPromptUseCase = BuildAiCoachPromptUseCase(),
    )

    @Test
    fun `returns local reply when api key is missing`() = runTest {
        val reply = repository.generateReply(sampleContext(), "Help me focus now")

        assertEquals(AiCoachSource.LOCAL, reply.source)
        assertTrue(reply.text.contains("25 minutes"))
        assertTrue(reply.text.contains("Focus"))
    }

    @Test
    fun `builds quick actions for the current context`() {
        val actions = repository.buildQuickActions(sampleContext())

        assertEquals(4, actions.size)
        assertTrue(actions.first().prompt.contains("25"))
    }

    @Test
    fun `marks backend proxy replies as remote`() = runTest {
        val remoteRepository = AiCoachRepositoryImpl(
            aiGateway = object : AiGateway {
                override val modelId: String = "backend-managed"
                override val baseUrl: String = "https://bloom-ai.example.com"
                override val isConfigured: Boolean = true

                override suspend fun generateReply(prompt: AiCoachPrompt): String {
                    return "Remote coaching reply"
                }
            },
            buildAiCoachPromptUseCase = BuildAiCoachPromptUseCase(),
        )

        val reply = remoteRepository.generateReply(sampleContext(), "Review my week")

        assertEquals(AiCoachSource.REMOTE, reply.source)
        assertEquals("Remote coaching reply", reply.text)
    }

    private fun sampleContext() = AiCoachContext(
        userName = "Ana",
        preferences = UserPreferences(
            userName = "Ana",
            userEmail = "",
            primaryGoal = "Build consistency",
            themeMode = ThemeMode.SYSTEM,
            focusMinutes = 25,
            shortBreakMinutes = 5,
            longBreakMinutes = 15,
            autoStartNextSession = true,
            notificationsEnabled = true,
            bloomCoachEnabled = true,
            allowHabitContextForAi = true,
            onboardingCompleted = true,
            authCompleted = true,
            seedDataCreated = true,
        ),
        habits = listOf(
            Habit(
                id = "1",
                name = "Water plants",
                category = HabitCategory.HEALTH,
                frequency = HabitFrequency.DAILY,
                reminderHour = 8,
                reminderMinute = 0,
                colorArgb = 0xFF8DAA91.toInt(),
                iconKey = "watering_can",
                createdAtMillis = 0L,
                streak = 4,
                completedToday = false,
                completionCount = 12,
            ),
        ),
        routineBlocks = listOf(
            RoutineBlock(
                id = "morning",
                title = "Morning",
                subtitle = "Water plants",
                slot = "Morning",
                durationMinutes = 30,
                active = true,
                colorArgb = 0xFFB4D2C0.toInt(),
                iconKey = "sun",
            ),
        ),
        statistics = BloomStatistics(
            focusMinutesToday = 0,
            habitsDoneToday = 1,
            totalHabits = 3,
            longestStreak = 7,
            weeklyConsistency = 80,
            weeklyFocusMinutes = listOf(5, 10, 25, 0, 30, 15, 20),
            weeklyHabitCompletions = listOf(1, 2, 1, 0, 2, 1, 3),
            monthlyFocusMinutes = listOf(60, 90, 120, 150),
            monthlyHabitCompletions = List(28) { if (it % 2 == 0) 1 else 0 },
            averageFocusMinutes = 25,
            mostProductiveHourLabel = "19:00",
            topHabitName = "Water plants",
            gardenGrowth = 42,
        ),
        rewardsUnlocked = 2,
        recentSessions = listOf(
            PomodoroSession(
                id = "session-1",
                mode = PomodoroMode.FOCUS,
                durationMinutes = 25,
                startedAtMillis = 0L,
                finishedAtMillis = 1L,
                completed = true,
            ),
        ),
    )
}

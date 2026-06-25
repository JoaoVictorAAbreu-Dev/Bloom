package com.bloom.app.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloom.app.BloomAppContainer
import com.bloom.app.domain.model.Habit
import com.bloom.app.domain.model.RoutineBlock
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime

class HomeViewModel(
    private val container: BloomAppContainer,
) : ViewModel() {
    private val habitsFlow = container.observeHabitsUseCase()
    private val statisticsFlow = container.observeStatisticsUseCase()
    private val preferencesFlow = container.observePreferencesUseCase()

    val uiState = combine(habitsFlow, statisticsFlow, preferencesFlow) { habits, statistics, preferences ->
        val completedHabits = habits.count { it.completedToday }
        val progress = if (habits.isEmpty()) 0f else completedHabits / habits.size.toFloat()
        val focusProgress = if (preferences.focusMinutes <= 0) 0f else (statistics.focusMinutesToday / preferences.focusMinutes.toFloat()).coerceIn(0f, 1f)

        HomeUiState(
            userName = preferences.userName,
            progress = progress,
            progressLabel = "$completedHabits of ${habits.size} habits done",
            focusLabel = String.format("%02d:00", preferences.focusMinutes),
            focusSubtitle = if (statistics.focusMinutesToday > 0) {
                "${statistics.focusMinutesToday} minutes done today"
            } else {
                "Deep Work Block"
            },
            focusProgress = focusProgress,
            habits = habits.sortedBy { it.reminderHour ?: 99 }.take(4),
            routineBlocks = buildRoutineBlocks(habits),
            statistics = statistics,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun toggleHabitCompletion(habitId: String) {
        viewModelScope.launch {
            container.toggleHabitCompletionUseCase(habitId)
        }
    }

    private fun buildRoutineBlocks(habits: List<Habit>): List<RoutineBlock> {
        val now = LocalTime.now()
        val grouped = habits.groupBy { habit ->
            val hour = habit.reminderHour ?: 8
            when (hour) {
                in 5..11 -> "Morning"
                in 12..16 -> "Afternoon"
                in 17..20 -> "Evening"
                else -> "Night"
            }
        }

        return listOf(
            RoutineBlock(
                id = "morning",
                title = "Morning",
                subtitle = routineSubtitle(grouped["Morning"]),
                slot = "Morning",
                durationMinutes = 30,
                active = now.hour in 5..11,
                colorArgb = 0xFFB4D2C0.toInt(),
                iconKey = "sun",
            ),
            RoutineBlock(
                id = "afternoon",
                title = "Afternoon",
                subtitle = routineSubtitle(grouped["Afternoon"]),
                slot = "Afternoon",
                durationMinutes = 30,
                active = now.hour in 12..16,
                colorArgb = 0xFFC6E9E9.toInt(),
                iconKey = "cloud",
            ),
            RoutineBlock(
                id = "evening",
                title = "Evening",
                subtitle = routineSubtitle(grouped["Evening"]),
                slot = "Evening",
                durationMinutes = 25,
                active = now.hour in 17..20,
                colorArgb = 0xFFAE98D6.toInt(),
                iconKey = "moon",
            ),
            RoutineBlock(
                id = "night",
                title = "Night",
                subtitle = routineSubtitle(grouped["Night"]),
                slot = "Night",
                durationMinutes = 20,
                active = now.hour !in 5..20,
                colorArgb = 0xFFD9A441.toInt(),
                iconKey = "star",
            ),
        )
    }

    private fun routineSubtitle(habits: List<Habit>?): String {
        val list = habits.orEmpty()
        return when {
            list.isEmpty() -> "Take a gentle start"
            list.size == 1 -> list.first().name
            else -> "${list.first().name} + ${list.size - 1} more"
        }
    }
}

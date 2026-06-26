package com.bloom.app.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloom.app.BloomAppContainer
import com.bloom.app.domain.model.Habit
import com.bloom.app.domain.model.RoutineBlock
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalTime

class RoutineViewModel(
    private val container: BloomAppContainer,
) : ViewModel() {
    val uiState = combine(
        container.observeHabitsUseCase(),
        container.observePreferencesUseCase(),
    ) { habits, _ ->
        RoutineUiState(
            routineBlocks = buildRoutineBlocks(habits),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RoutineUiState())

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
                activities = routineActivities(grouped["Morning"], "Add a morning habit"),
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
                activities = routineActivities(grouped["Afternoon"], "Add an afternoon habit"),
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
                activities = routineActivities(grouped["Evening"], "Add an evening habit"),
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
                activities = routineActivities(grouped["Night"], "Add a night habit"),
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

    private fun routineActivities(habits: List<Habit>?, emptyLabel: String): List<String> {
        val names = habits.orEmpty().map { it.name }
        return names.take(4).ifEmpty { listOf(emptyLabel) }
    }
}

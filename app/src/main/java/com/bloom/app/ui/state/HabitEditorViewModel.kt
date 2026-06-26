package com.bloom.app.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloom.app.BloomAppContainer
import com.bloom.app.domain.model.Habit
import com.bloom.app.domain.model.HabitCategory
import com.bloom.app.domain.model.HabitFrequency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class HabitEditorViewModel(
    private val container: BloomAppContainer,
    private val habitId: String?,
) : ViewModel() {
    private val mutableState = MutableStateFlow(
        HabitEditorUiState(
            habitId = habitId,
        ),
    )

    val uiState: StateFlow<HabitEditorUiState> = mutableState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), mutableState.value)

    init {
        habitId?.let { id ->
            viewModelScope.launch {
                container.observeHabitUseCase(id).collect { habit ->
                    habit?.let { applyHabit(it) }
                }
            }
        }
    }

    fun updateName(value: String) = mutableState.update { it.copy(name = value.sanitizedHabitName()) }
    fun updateCategory(value: HabitCategory) = mutableState.update { it.copy(category = value) }
    fun updateFrequency(value: HabitFrequency) = mutableState.update { it.copy(frequency = value) }
    fun updateReminder(hour: Int, minute: Int) = mutableState.update {
        it.copy(reminderHour = hour, reminderMinute = minute)
    }
    fun updateColor(value: Int) = mutableState.update { it.copy(colorArgb = value) }
    fun updateIcon(value: String) = mutableState.update { it.copy(iconKey = value) }
    fun updatePriority(value: String) = mutableState.update { it.copy(priority = value) }
    fun updateEmoji(value: String) = mutableState.update { it.copy(emoji = value.take(4)) }
    fun updateDailyGoal(value: Int) = mutableState.update { it.copy(dailyGoal = value.coerceIn(1, 12)) }
    fun updateWeeklyGoal(value: Int) = mutableState.update { it.copy(weeklyGoal = value.coerceIn(1, 21)) }
    fun updateCustomRepeat(value: String) = mutableState.update { it.copy(customRepeat = value.take(40)) }

    fun save(onSaved: () -> Unit) {
        val current = mutableState.value
        viewModelScope.launch {
            mutableState.update { it.copy(saving = true) }
            container.upsertHabitUseCase(
                Habit(
                    id = current.habitId ?: UUID.randomUUID().toString(),
                    name = current.name.trim().sanitizedHabitName(),
                    category = current.category,
                    frequency = current.frequency,
                    reminderHour = current.reminderHour,
                    reminderMinute = current.reminderMinute,
                    colorArgb = current.colorArgb,
                    iconKey = current.iconKey,
                    priority = current.priority,
                    emoji = current.emoji,
                    dailyGoal = current.dailyGoal,
                    weeklyGoal = current.weeklyGoal,
                    customRepeat = current.customRepeat,
                    createdAtMillis = current.createdAtMillis,
                    streak = 0,
                    completedToday = false,
                    completionCount = 0,
                ),
            )
            mutableState.update { it.copy(saving = false) }
            onSaved()
        }
    }

    fun delete(onDeleted: () -> Unit) {
        val currentId = mutableState.value.habitId ?: return
        viewModelScope.launch {
            container.deleteHabitUseCase(currentId)
            onDeleted()
        }
    }

    private fun applyHabit(habit: Habit) {
        mutableState.update {
            it.copy(
                habitId = habit.id,
                name = habit.name,
                category = habit.category,
                frequency = habit.frequency,
                reminderHour = habit.reminderHour ?: 8,
                reminderMinute = habit.reminderMinute ?: 0,
                colorArgb = habit.colorArgb,
                iconKey = habit.iconKey,
                priority = habit.priority,
                emoji = habit.emoji,
                dailyGoal = habit.dailyGoal,
                weeklyGoal = habit.weeklyGoal,
                customRepeat = habit.customRepeat,
                createdAtMillis = habit.createdAtMillis,
                loading = false,
            )
        }
    }

    private fun String.sanitizedHabitName(): String {
        return filterNot { it.isISOControl() }.take(80)
    }
}

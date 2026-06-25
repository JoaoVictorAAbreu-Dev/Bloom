package com.bloom.app.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloom.app.BloomAppContainer
import com.bloom.app.domain.model.HabitCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HabitsViewModel(
    private val container: BloomAppContainer,
) : ViewModel() {
    private val selectedCategory = MutableStateFlow<HabitCategory?>(null)
    private val habitsFlow = container.observeHabitsUseCase()

    val uiState = combine(habitsFlow, selectedCategory) { habits, category ->
        val filtered = category?.let { selected -> habits.filter { it.category == selected } } ?: habits
        HabitsUiState(
            habits = filtered,
            selectedCategory = category,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HabitsUiState())

    fun selectCategory(category: HabitCategory?) {
        selectedCategory.value = category
    }

    fun toggleCompletion(habitId: String) {
        viewModelScope.launch {
            container.toggleHabitCompletionUseCase(habitId)
        }
    }

    fun deleteHabit(habitId: String) {
        viewModelScope.launch {
            container.deleteHabitUseCase(habitId)
        }
    }
}

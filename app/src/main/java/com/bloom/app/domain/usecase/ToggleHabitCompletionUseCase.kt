package com.bloom.app.domain.usecase

import com.bloom.app.domain.repository.HabitRepository

class ToggleHabitCompletionUseCase(private val habitRepository: HabitRepository) {
    suspend operator fun invoke(habitId: String, completedAtMillis: Long = System.currentTimeMillis()) {
        habitRepository.toggleHabitCompletion(habitId, completedAtMillis)
    }
}

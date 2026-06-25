package com.bloom.app.domain.usecase

import com.bloom.app.domain.model.Habit
import com.bloom.app.domain.repository.HabitRepository

class UpsertHabitUseCase(private val habitRepository: HabitRepository) {
    suspend operator fun invoke(habit: Habit) = habitRepository.upsertHabit(habit)
}

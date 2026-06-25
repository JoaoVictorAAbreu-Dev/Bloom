package com.bloom.app.domain.usecase

import com.bloom.app.domain.repository.HabitRepository

class ObserveHabitUseCase(private val habitRepository: HabitRepository) {
    operator fun invoke(habitId: String) = habitRepository.observeHabit(habitId)
}

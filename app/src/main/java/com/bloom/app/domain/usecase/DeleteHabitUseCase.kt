package com.bloom.app.domain.usecase

import com.bloom.app.domain.repository.HabitRepository

class DeleteHabitUseCase(private val habitRepository: HabitRepository) {
    suspend operator fun invoke(habitId: String) = habitRepository.deleteHabit(habitId)
}

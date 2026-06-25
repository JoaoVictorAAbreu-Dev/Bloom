package com.bloom.app.domain.usecase

import com.bloom.app.domain.repository.HabitRepository

class ObserveHabitsUseCase(private val habitRepository: HabitRepository) {
    operator fun invoke() = habitRepository.observeHabits()
}

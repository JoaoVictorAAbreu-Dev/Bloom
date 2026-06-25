package com.bloom.app.domain.usecase

import com.bloom.app.domain.model.HabitCategory
import com.bloom.app.domain.repository.HabitRepository

class ObserveHabitsByCategoryUseCase(private val habitRepository: HabitRepository) {
    operator fun invoke(category: HabitCategory?) = habitRepository.observeHabitsByCategory(category)
}

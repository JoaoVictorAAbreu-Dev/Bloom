package com.bloom.app.domain.usecase

import com.bloom.app.domain.repository.HabitRepository
import com.bloom.app.domain.repository.PreferencesRepository

class SeedDemoContentUseCase(
    private val habitRepository: HabitRepository,
    private val preferencesRepository: PreferencesRepository,
) {
    suspend operator fun invoke() {
        habitRepository.seedDefaults()
        preferencesRepository.setSeeded()
    }
}

package com.bloom.app.domain.usecase

import com.bloom.app.domain.repository.HabitRepository
import com.bloom.app.domain.repository.PomodoroRepository
import com.bloom.app.domain.repository.PreferencesRepository

class ResetAllDataUseCase(
    private val habitRepository: HabitRepository,
    private val pomodoroRepository: PomodoroRepository,
    private val preferencesRepository: PreferencesRepository,
) {
    suspend operator fun invoke() {
        habitRepository.reset()
        pomodoroRepository.reset()
        preferencesRepository.reset()
    }
}

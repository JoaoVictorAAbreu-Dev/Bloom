package com.bloom.app.domain.usecase

import com.bloom.app.domain.repository.PreferencesRepository

class ObservePreferencesUseCase(private val preferencesRepository: PreferencesRepository) {
    operator fun invoke() = preferencesRepository.preferences
}

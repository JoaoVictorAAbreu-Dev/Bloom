package com.bloom.app.domain.usecase

import com.bloom.app.domain.model.UserPreferences
import com.bloom.app.domain.repository.PreferencesRepository

class UpdatePreferencesUseCase(private val preferencesRepository: PreferencesRepository) {
    suspend operator fun invoke(transform: (UserPreferences) -> UserPreferences) =
        preferencesRepository.updatePreferences(transform)
}

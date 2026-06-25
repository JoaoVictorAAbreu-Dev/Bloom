package com.bloom.app.domain.usecase

import com.bloom.app.domain.model.ThemeMode
import com.bloom.app.domain.repository.PreferencesRepository

class UpdateThemeModeUseCase(private val preferencesRepository: PreferencesRepository) {
    suspend operator fun invoke(themeMode: ThemeMode) = preferencesRepository.updateThemeMode(themeMode)
}

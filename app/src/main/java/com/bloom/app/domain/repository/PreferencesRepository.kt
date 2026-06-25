package com.bloom.app.domain.repository

import com.bloom.app.domain.model.ThemeMode
import com.bloom.app.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val preferences: Flow<UserPreferences>
    suspend fun updateThemeMode(themeMode: ThemeMode)
    suspend fun updatePreferences(transform: (UserPreferences) -> UserPreferences)
    suspend fun completeOnboarding()
    suspend fun setSeeded()
    suspend fun reset()
}

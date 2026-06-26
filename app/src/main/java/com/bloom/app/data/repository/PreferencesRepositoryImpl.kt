package com.bloom.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bloom.app.data.local.bloomPreferencesDataStore
import com.bloom.app.data.mapper.toThemeModeOrDefault
import com.bloom.app.domain.model.ThemeMode
import com.bloom.app.domain.model.UserPreferences
import com.bloom.app.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PreferencesRepositoryImpl(
    private val context: Context,
) : PreferencesRepository {
    private val dataStore = context.bloomPreferencesDataStore

    override val preferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        prefs.toDomain()
    }

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        dataStore.edit { prefs ->
            prefs[Keys.themeMode] = themeMode.name
        }
    }

    override suspend fun updatePreferences(transform: (UserPreferences) -> UserPreferences) {
        val current = preferences.first()
        val updated = transform(current)
        dataStore.edit { prefs -> prefs.merge(updated) }
    }

    override suspend fun setSeeded() {
        dataStore.edit { prefs -> prefs[Keys.seedDataCreated] = true }
    }

    override suspend fun reset() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    private fun Preferences.toDomain(): UserPreferences {
        return UserPreferences(
            userName = this[Keys.userName] ?: "Alex",
            userEmail = this[Keys.userEmail] ?: "",
            primaryGoal = this[Keys.primaryGoal] ?: "Build consistency",
            themeMode = (this[Keys.themeMode] ?: ThemeMode.SYSTEM.name).toThemeModeOrDefault(),
            focusMinutes = this[Keys.focusMinutes] ?: 25,
            shortBreakMinutes = this[Keys.shortBreakMinutes] ?: 5,
            longBreakMinutes = this[Keys.longBreakMinutes] ?: 15,
            autoStartNextSession = this[Keys.autoStartNextSession] ?: true,
            notificationsEnabled = this[Keys.notificationsEnabled] ?: true,
            onboardingCompleted = this[Keys.onboardingCompleted] ?: false,
            authCompleted = this[Keys.authCompleted] ?: false,
            seedDataCreated = this[Keys.seedDataCreated] ?: false,
        )
    }

    private fun androidx.datastore.preferences.core.MutablePreferences.merge(updated: UserPreferences) {
        this[Keys.userName] = updated.userName
        this[Keys.userEmail] = updated.userEmail
        this[Keys.primaryGoal] = updated.primaryGoal
        this[Keys.themeMode] = updated.themeMode.name
        this[Keys.focusMinutes] = updated.focusMinutes
        this[Keys.shortBreakMinutes] = updated.shortBreakMinutes
        this[Keys.longBreakMinutes] = updated.longBreakMinutes
        this[Keys.autoStartNextSession] = updated.autoStartNextSession
        this[Keys.notificationsEnabled] = updated.notificationsEnabled
        this[Keys.onboardingCompleted] = updated.onboardingCompleted
        this[Keys.authCompleted] = updated.authCompleted
        this[Keys.seedDataCreated] = updated.seedDataCreated
    }

    private object Keys {
        val userName = stringPreferencesKey("user_name")
        val userEmail = stringPreferencesKey("user_email")
        val primaryGoal = stringPreferencesKey("primary_goal")
        val themeMode = stringPreferencesKey("theme_mode")
        val focusMinutes = intPreferencesKey("focus_minutes")
        val shortBreakMinutes = intPreferencesKey("short_break_minutes")
        val longBreakMinutes = intPreferencesKey("long_break_minutes")
        val autoStartNextSession = booleanPreferencesKey("auto_start_next_session")
        val notificationsEnabled = booleanPreferencesKey("notifications_enabled")
        val onboardingCompleted = booleanPreferencesKey("onboarding_completed")
        val authCompleted = booleanPreferencesKey("auth_completed")
        val seedDataCreated = booleanPreferencesKey("seed_data_created")
    }
}

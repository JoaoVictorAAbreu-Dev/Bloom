package com.bloom.app.domain.model

data class UserPreferences(
    val userName: String,
    val userEmail: String,
    val primaryGoal: String,
    val themeMode: ThemeMode,
    val focusMinutes: Int,
    val shortBreakMinutes: Int,
    val longBreakMinutes: Int,
    val autoStartNextSession: Boolean,
    val notificationsEnabled: Boolean,
    val onboardingCompleted: Boolean,
    val authCompleted: Boolean,
    val seedDataCreated: Boolean,
)

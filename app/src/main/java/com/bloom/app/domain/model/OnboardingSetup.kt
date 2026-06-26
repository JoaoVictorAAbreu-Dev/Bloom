package com.bloom.app.domain.model

data class OnboardingSetup(
    val primaryGoal: String,
    val starterHabits: List<String>,
    val focusMinutes: Int,
    val shortBreakMinutes: Int,
    val notificationsEnabled: Boolean,
)

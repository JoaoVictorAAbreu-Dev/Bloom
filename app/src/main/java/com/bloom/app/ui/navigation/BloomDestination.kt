package com.bloom.app.ui.navigation

object BloomDestination {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val AUTH = "auth"
    const val HOME = "home"
    const val HABITS = "habits"
    const val FOCUS = "focus"
    const val STATS = "stats"
    const val PROFILE = "profile"
    const val ROUTINE = "routine"
    const val GARDEN = "garden"
    const val SETTINGS = "settings"
    const val COACH = "coach"
    const val HABIT_EDITOR = "habit_editor"

    fun habitEditorRoute(habitId: String? = null): String {
        return if (habitId == null) HABIT_EDITOR else "$HABIT_EDITOR/$habitId"
    }
}

package com.bloom.app.domain.model

data class BloomStatistics(
    val focusMinutesToday: Int,
    val habitsDoneToday: Int,
    val totalHabits: Int,
    val longestStreak: Int,
    val weeklyConsistency: Int,
    val weeklyFocusMinutes: List<Int>,
    val weeklyHabitCompletions: List<Int>,
    val gardenGrowth: Int,
)

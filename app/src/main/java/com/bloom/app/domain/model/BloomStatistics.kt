package com.bloom.app.domain.model

data class BloomStatistics(
    val focusMinutesToday: Int,
    val habitsDoneToday: Int,
    val totalHabits: Int,
    val longestStreak: Int,
    val weeklyConsistency: Int,
    val weeklyFocusMinutes: List<Int>,
    val weeklyHabitCompletions: List<Int>,
    val monthlyFocusMinutes: List<Int>,
    val monthlyHabitCompletions: List<Int>,
    val averageFocusMinutes: Int,
    val mostProductiveHourLabel: String,
    val topHabitName: String,
    val gardenGrowth: Int,
)

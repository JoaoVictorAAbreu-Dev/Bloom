package com.bloom.app.domain.model

data class Habit(
    val id: String,
    val name: String,
    val category: HabitCategory,
    val frequency: HabitFrequency,
    val reminderHour: Int?,
    val reminderMinute: Int?,
    val colorArgb: Int,
    val iconKey: String,
    val priority: String = "Medium",
    val emoji: String = "",
    val dailyGoal: Int = 1,
    val weeklyGoal: Int = 5,
    val customRepeat: String = "",
    val createdAtMillis: Long = System.currentTimeMillis(),
    val streak: Int = 0,
    val completedToday: Boolean = false,
    val completionCount: Int = 0,
)

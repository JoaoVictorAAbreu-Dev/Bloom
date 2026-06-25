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
    val createdAtMillis: Long,
    val streak: Int,
    val completedToday: Boolean,
    val completionCount: Int,
)

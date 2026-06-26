package com.bloom.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val frequency: String,
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
)

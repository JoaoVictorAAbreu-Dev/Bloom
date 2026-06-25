package com.bloom.app.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_completions",
    indices = [Index(value = ["habitId", "dayStartMillis"], unique = true)],
)
data class HabitCompletionEntity(
    @PrimaryKey val id: String,
    val habitId: String,
    val completedAtMillis: Long,
    val dayStartMillis: Long,
)

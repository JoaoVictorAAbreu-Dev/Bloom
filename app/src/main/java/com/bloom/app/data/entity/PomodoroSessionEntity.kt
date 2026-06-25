package com.bloom.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pomodoro_sessions")
data class PomodoroSessionEntity(
    @PrimaryKey val id: String,
    val mode: String,
    val durationMinutes: Int,
    val startedAtMillis: Long,
    val finishedAtMillis: Long,
    val completed: Boolean,
)

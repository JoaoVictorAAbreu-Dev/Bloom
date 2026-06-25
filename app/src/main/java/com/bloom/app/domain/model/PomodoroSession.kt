package com.bloom.app.domain.model

data class PomodoroSession(
    val id: String,
    val mode: PomodoroMode,
    val durationMinutes: Int,
    val startedAtMillis: Long,
    val finishedAtMillis: Long,
    val completed: Boolean,
)

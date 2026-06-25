package com.bloom.app.domain.model

data class HabitCompletion(
    val id: String,
    val habitId: String,
    val completedAtMillis: Long,
    val dayStartMillis: Long,
)

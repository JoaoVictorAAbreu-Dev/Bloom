package com.bloom.app.domain.model

data class RoutineBlock(
    val id: String,
    val title: String,
    val subtitle: String,
    val slot: String,
    val durationMinutes: Int,
    val active: Boolean,
    val colorArgb: Int,
    val iconKey: String,
    val activities: List<String> = emptyList(),
)

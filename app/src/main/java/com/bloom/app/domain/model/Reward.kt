package com.bloom.app.domain.model

data class Reward(
    val id: String,
    val title: String,
    val description: String,
    val threshold: Int,
    val category: String,
    val unlocked: Boolean,
    val accentColorArgb: Int,
    val iconKey: String,
)

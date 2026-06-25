package com.bloom.app.domain.model

data class AiCoachReply(
    val text: String,
    val source: AiCoachSource,
    val modelId: String,
)


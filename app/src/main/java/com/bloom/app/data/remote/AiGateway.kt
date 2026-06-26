package com.bloom.app.data.remote

import com.bloom.app.domain.model.AiCoachPrompt

interface AiGateway {
    val modelId: String
    val baseUrl: String
    val isConfigured: Boolean

    suspend fun generateReply(prompt: AiCoachPrompt): String
}

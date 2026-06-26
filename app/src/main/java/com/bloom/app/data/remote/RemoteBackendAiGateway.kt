package com.bloom.app.data.remote

import com.bloom.app.domain.model.AiCoachPrompt
import java.io.IOException

class RemoteBackendAiGateway(
    override val baseUrl: String,
) : AiGateway {
    override val modelId: String = "backend-managed"
    override val isConfigured: Boolean = baseUrl.startsWith("https://")

    override suspend fun generateReply(prompt: AiCoachPrompt): String {
        throw IOException("Backend AI gateway is not implemented in the local-first MVP")
    }
}

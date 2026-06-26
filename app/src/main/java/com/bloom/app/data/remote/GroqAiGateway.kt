package com.bloom.app.data.remote

import com.bloom.app.domain.model.AiCoachPrompt

class GroqAiGateway(
    private val service: GroqAiService,
) : AiGateway {
    override val modelId: String get() = service.modelId
    override val baseUrl: String get() = service.baseUrl
    override val isConfigured: Boolean get() = service.isConfigured

    override suspend fun generateReply(prompt: AiCoachPrompt): String {
        return service.generateReply(prompt)
    }
}

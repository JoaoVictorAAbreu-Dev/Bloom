package com.bloom.aiproxy

interface AiProviderClient {
    fun generate(systemPrompt: String, userPrompt: String): AiCoachProxyResponse
}

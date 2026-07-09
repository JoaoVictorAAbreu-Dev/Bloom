package com.bloom.aiproxy

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "bloom.ai")
data class AiProxyProperties(
    val groqApiKey: String = System.getenv("GROQ_API_KEY").orEmpty(),
    val groqModel: String = System.getenv("GROQ_MODEL") ?: "groq/compound-mini",
    val groqBaseUrl: String = System.getenv("GROQ_BASE_URL") ?: "https://api.groq.com/openai/v1",
    val maxPromptCharacters: Int = 2_400,
    val clientToken: String = System.getenv("BLOOM_AI_CLIENT_TOKEN").orEmpty(),
    val rateLimitWindowSeconds: Long = 60,
    val rateLimitRequests: Int = 20,
)

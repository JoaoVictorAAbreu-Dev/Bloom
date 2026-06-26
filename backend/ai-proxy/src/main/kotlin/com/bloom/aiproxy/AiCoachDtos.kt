package com.bloom.aiproxy

data class AiCoachProxyRequest(
    val systemPrompt: String = "",
    val userPrompt: String = "",
)

data class AiCoachProxyResponse(
    val text: String,
    val model: String,
)

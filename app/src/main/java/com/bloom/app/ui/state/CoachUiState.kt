package com.bloom.app.ui.state

import com.bloom.app.domain.model.AiCoachQuickAction
import com.bloom.app.domain.model.AiCoachSource

enum class CoachMessageRole {
    USER,
    ASSISTANT,
}

data class CoachMessageUi(
    val id: String,
    val role: CoachMessageRole,
    val text: String,
    val source: AiCoachSource? = null,
)

data class CoachIntegrationUiState(
    val configured: Boolean = false,
    val modelId: String = "groq/compound-mini",
    val baseUrl: String = "https://api.groq.com/openai/v1",
)

data class CoachUiState(
    val integration: CoachIntegrationUiState = CoachIntegrationUiState(),
    val contextSummary: List<String> = emptyList(),
    val quickActions: List<AiCoachQuickAction> = emptyList(),
    val messages: List<CoachMessageUi> = emptyList(),
    val input: String = "",
    val sending: Boolean = false,
    val errorMessage: String? = null,
)


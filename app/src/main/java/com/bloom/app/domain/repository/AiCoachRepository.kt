package com.bloom.app.domain.repository

import com.bloom.app.domain.model.AiCoachContext
import com.bloom.app.domain.model.AiCoachQuickAction
import com.bloom.app.domain.model.AiCoachReply

interface AiCoachRepository {
    val modelId: String
    val baseUrl: String
    val isConfigured: Boolean

    suspend fun generateReply(context: AiCoachContext, userPrompt: String): AiCoachReply
    fun buildQuickActions(context: AiCoachContext): List<AiCoachQuickAction>
}


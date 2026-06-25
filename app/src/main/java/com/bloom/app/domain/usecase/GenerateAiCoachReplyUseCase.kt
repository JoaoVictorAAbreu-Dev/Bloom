package com.bloom.app.domain.usecase

import com.bloom.app.domain.model.AiCoachContext
import com.bloom.app.domain.model.AiCoachReply
import com.bloom.app.domain.repository.AiCoachRepository

class GenerateAiCoachReplyUseCase(
    private val repository: AiCoachRepository,
) {
    suspend operator fun invoke(
        context: AiCoachContext,
        userPrompt: String,
    ): AiCoachReply = repository.generateReply(context, userPrompt)
}


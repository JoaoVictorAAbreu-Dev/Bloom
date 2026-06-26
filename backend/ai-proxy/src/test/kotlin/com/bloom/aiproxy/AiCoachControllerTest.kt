package com.bloom.aiproxy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class AiCoachControllerTest {
    private val fakeClient = object : AiProviderClient {
        var lastSystemPrompt: String = ""
        var lastUserPrompt: String = ""

        override fun generate(systemPrompt: String, userPrompt: String): AiCoachProxyResponse {
            lastSystemPrompt = systemPrompt
            lastUserPrompt = userPrompt
            return AiCoachProxyResponse(text = "Keep the next action small.", model = "fake")
        }
    }

    private val controller = AiCoachController(
        aiProviderClient = fakeClient,
        privacySanitizer = PrivacySanitizer(),
        properties = AiProxyProperties(maxPromptCharacters = 80),
    )

    @Test
    fun `sanitizes prompt before sending to provider`() {
        val response = controller.coach(
            AiCoachProxyRequest(
                systemPrompt = "You are Bloom.",
                userPrompt = "My email is alex@example.com and my habit is study.",
            ),
        )

        assertEquals("Keep the next action small.", response.text)
        assertEquals("You are Bloom.", fakeClient.lastSystemPrompt)
        assertEquals("My email is [email] and my habit is study.", fakeClient.lastUserPrompt)
    }

    @Test
    fun `rejects blank prompts`() {
        assertThrows(IllegalArgumentException::class.java) {
            controller.coach(AiCoachProxyRequest(systemPrompt = "", userPrompt = "hello"))
        }
    }
}

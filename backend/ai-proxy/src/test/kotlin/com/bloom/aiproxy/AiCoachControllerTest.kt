package com.bloom.aiproxy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest

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
        rateLimiter = RateLimiter(AiProxyProperties(maxPromptCharacters = 80)),
    )

    @Test
    fun `sanitizes prompt before sending to provider`() {
        val response = controller.coach(
            AiCoachProxyRequest(
                systemPrompt = "You are Bloom.",
                userPrompt = "My email is alex@example.com and my habit is study.",
            ),
            MockHttpServletRequest(),
        )

        assertEquals("Keep the next action small.", response.text)
        assertEquals("You are Bloom.", fakeClient.lastSystemPrompt)
        assertEquals("My email is [email] and my habit is study.", fakeClient.lastUserPrompt)
    }

    @Test
    fun `rejects blank prompts`() {
        assertThrows(IllegalArgumentException::class.java) {
            controller.coach(AiCoachProxyRequest(systemPrompt = "", userPrompt = "hello"), MockHttpServletRequest())
        }
    }

    @Test
    fun `requires configured client token`() {
        val securedController = AiCoachController(
            aiProviderClient = fakeClient,
            privacySanitizer = PrivacySanitizer(),
            properties = AiProxyProperties(clientToken = "client-token"),
            rateLimiter = RateLimiter(AiProxyProperties(clientToken = "client-token")),
        )

        assertThrows(InvalidClientTokenException::class.java) {
            securedController.coach(AiCoachProxyRequest("system", "user"), MockHttpServletRequest())
        }

        val request = MockHttpServletRequest().apply {
            addHeader("X-Bloom-Client-Token", "client-token")
        }
        val response = securedController.coach(AiCoachProxyRequest("system", "user"), request)

        assertEquals("Keep the next action small.", response.text)
    }

    @Test
    fun `enforces per-client rate limit`() {
        val limitedProperties = AiProxyProperties(rateLimitRequests = 1, rateLimitWindowSeconds = 60)
        val limitedController = AiCoachController(
            aiProviderClient = fakeClient,
            privacySanitizer = PrivacySanitizer(),
            properties = limitedProperties,
            rateLimiter = RateLimiter(limitedProperties),
        )
        val request = MockHttpServletRequest().apply {
            remoteAddr = "203.0.113.10"
        }

        limitedController.coach(AiCoachProxyRequest("system", "user"), request)

        assertThrows(RateLimitExceededException::class.java) {
            limitedController.coach(AiCoachProxyRequest("system", "user"), request)
        }
    }
}

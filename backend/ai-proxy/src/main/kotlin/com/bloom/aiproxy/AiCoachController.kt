package com.bloom.aiproxy

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestController
@RequestMapping("/api/ai")
class AiCoachController(
    private val aiProviderClient: AiProviderClient,
    private val privacySanitizer: PrivacySanitizer,
    private val properties: AiProxyProperties,
    private val rateLimiter: RateLimiter,
) {
    @PostMapping("/coach")
    fun coach(
        @RequestBody request: AiCoachProxyRequest,
        servletRequest: HttpServletRequest,
    ): AiCoachProxyResponse {
        requireClientToken(servletRequest)
        if (!rateLimiter.allow(servletRequest.clientKey())) {
            throw RateLimitExceededException()
        }

        val systemPrompt = privacySanitizer.sanitize(request.systemPrompt, properties.maxPromptCharacters).trim()
        val userPrompt = privacySanitizer.sanitize(request.userPrompt, properties.maxPromptCharacters).trim()
        require(systemPrompt.isNotBlank()) { "systemPrompt is required" }
        require(userPrompt.isNotBlank()) { "userPrompt is required" }
        return aiProviderClient.generate(systemPrompt, userPrompt)
    }

    private fun requireClientToken(request: HttpServletRequest) {
        val expectedToken = properties.clientToken.trim()
        if (expectedToken.isBlank()) return

        val providedToken = request.getHeader(CLIENT_TOKEN_HEADER).orEmpty()
        if (providedToken != expectedToken) {
            throw InvalidClientTokenException()
        }
    }

    private fun HttpServletRequest.clientKey(): String {
        return getHeader("X-Forwarded-For")
            ?.substringBefore(',')
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: remoteAddr.orEmpty().ifBlank { "unknown" }
    }

    private companion object {
        const val CLIENT_TOKEN_HEADER = "X-Bloom-Client-Token"
    }
}

@RestControllerAdvice
class ApiErrorHandler {
    @ExceptionHandler(InvalidClientTokenException::class)
    fun unauthorized(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Unauthorized"))
    }

    @ExceptionHandler(RateLimitExceededException::class)
    fun rateLimited(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(mapOf("error" to "Too many requests"))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun badRequest(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to "Invalid request"))
    }

    @ExceptionHandler(Exception::class)
    fun serverError(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(mapOf("error" to "AI provider unavailable"))
    }
}

class InvalidClientTokenException : RuntimeException()

class RateLimitExceededException : RuntimeException()

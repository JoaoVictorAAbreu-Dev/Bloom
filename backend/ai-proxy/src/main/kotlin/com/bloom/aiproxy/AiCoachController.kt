package com.bloom.aiproxy

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
) {
    @PostMapping("/coach")
    fun coach(@RequestBody request: AiCoachProxyRequest): AiCoachProxyResponse {
        val systemPrompt = privacySanitizer.sanitize(request.systemPrompt, properties.maxPromptCharacters)
        val userPrompt = privacySanitizer.sanitize(request.userPrompt, properties.maxPromptCharacters)
        require(systemPrompt.isNotBlank()) { "systemPrompt is required" }
        require(userPrompt.isNotBlank()) { "userPrompt is required" }
        return aiProviderClient.generate(systemPrompt, userPrompt)
    }
}

@RestControllerAdvice
class ApiErrorHandler {
    @ExceptionHandler(IllegalArgumentException::class)
    fun badRequest(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to "Invalid request"))
    }

    @ExceptionHandler(Exception::class)
    fun serverError(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(mapOf("error" to "AI provider unavailable"))
    }
}

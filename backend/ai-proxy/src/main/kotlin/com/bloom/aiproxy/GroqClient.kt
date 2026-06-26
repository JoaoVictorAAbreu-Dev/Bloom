package com.bloom.aiproxy

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.stereotype.Component
import java.io.IOException
import java.time.Duration

@Component
class GroqClient(
    private val properties: AiProxyProperties,
) : AiProviderClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(Duration.ofSeconds(10))
        .readTimeout(Duration.ofSeconds(25))
        .writeTimeout(Duration.ofSeconds(10))
        .build()

    override fun generate(systemPrompt: String, userPrompt: String): AiCoachProxyResponse {
        val apiKey = properties.groqApiKey.trim()
        check(apiKey.isNotBlank()) { "GROQ_API_KEY is not configured" }
        check(properties.groqBaseUrl.startsWith("https://")) { "GROQ_BASE_URL must use HTTPS" }

        val payload = JSONObject()
            .put("model", properties.groqModel)
            .put("temperature", 0.4)
            .put("max_tokens", 320)
            .put(
                "messages",
                JSONArray()
                    .put(JSONObject().put("role", "system").put("content", systemPrompt))
                    .put(JSONObject().put("role", "user").put("content", userPrompt)),
            )

        val request = Request.Builder()
            .url("${properties.groqBaseUrl.trimEnd('/')}/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw IOException("Groq request failed (${response.code})")
            }
            val text = JSONObject(body)
                .optJSONArray("choices")
                ?.optJSONObject(0)
                ?.optJSONObject("message")
                ?.optString("content")
                .orEmpty()
            if (text.isBlank()) {
                throw IOException("Groq response missing content")
            }
            return AiCoachProxyResponse(text = text, model = properties.groqModel)
        }
    }

    private companion object {
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}

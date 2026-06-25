package com.bloom.app.data.remote

import com.bloom.app.BuildConfig
import com.bloom.app.domain.model.AiCoachPrompt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class GroqAiService(
    private val apiKeyProvider: () -> String = { BuildConfig.GROQ_API_KEY },
    private val modelProvider: () -> String = { BuildConfig.GROQ_MODEL },
    private val baseUrlProvider: () -> String = { BuildConfig.GROQ_BASE_URL },
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build(),
) {
    val modelId: String get() = modelProvider()
    val baseUrl: String get() = baseUrlProvider().trimEnd('/')
    val isConfigured: Boolean get() = apiKeyProvider().isNotBlank()

    suspend fun generateReply(prompt: AiCoachPrompt): String = withContext(Dispatchers.IO) {
        val apiKey = apiKeyProvider().trim()
        if (apiKey.isBlank()) {
            throw IOException("Groq API key is not configured")
        }

        val payload = JSONObject()
            .put("model", modelId)
            .put("temperature", 0.4)
            .put("max_tokens", 320)
            .put(
                "messages",
                JSONArray()
                    .put(JSONObject().put("role", "system").put("content", prompt.systemPrompt))
                    .put(JSONObject().put("role", "user").put("content", prompt.userPrompt)),
            )

        val request = Request.Builder()
            .url("$baseUrl/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw IOException("Groq request failed (${response.code}): $responseBody")
            }

            val json = JSONObject(responseBody)
            val choices = json.optJSONArray("choices") ?: throw IOException("Groq response missing choices")
            val firstChoice = choices.optJSONObject(0) ?: throw IOException("Groq response missing first choice")
            val message = firstChoice.optJSONObject("message") ?: throw IOException("Groq response missing message")
            message.optString("content").ifBlank {
                throw IOException("Groq response missing content")
            }
        }
    }

    private companion object {
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}


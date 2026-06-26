package com.bloom.app.data.remote

import com.bloom.app.domain.model.AiCoachPrompt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class RemoteBackendAiGateway(
    baseUrl: String,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build(),
) : AiGateway {
    override val baseUrl: String = baseUrl.trim().trimEnd('/')
    override val modelId: String = "backend-managed"
    override val isConfigured: Boolean = this.baseUrl.startsWith("https://")

    override suspend fun generateReply(prompt: AiCoachPrompt): String = withContext(Dispatchers.IO) {
        if (!isConfigured) {
            throw IOException("AI backend URL must use HTTPS")
        }

        val payload = JSONObject()
            .put("systemPrompt", prompt.systemPrompt)
            .put("userPrompt", prompt.userPrompt)

        val request = Request.Builder()
            .url("${this.baseUrl}/api/ai/coach")
            .addHeader("Content-Type", "application/json")
            .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw IOException("AI backend request failed (${response.code})")
            }
            JSONObject(body).optString("text").ifBlank {
                throw IOException("AI backend response missing text")
            }
        }
    }

    private companion object {
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}

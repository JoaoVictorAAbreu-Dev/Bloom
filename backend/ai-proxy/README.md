# Bloom AI Proxy

Small Spring Boot service that keeps the Groq API key out of the Android APK.

## Why this exists

Mobile apps cannot safely keep provider API keys. Release builds of Bloom should call this proxy over HTTPS. The proxy stores `GROQ_API_KEY` server-side, sanitizes prompts, calls Groq, and returns only the assistant text.

## Stack

- Kotlin
- Java 21
- Spring Boot 3
- OkHttp
- JUnit 5

## Configuration

Copy `.env.example` into your deployment environment and set real values there. Do not commit real secrets.

```env
GROQ_API_KEY=replace_with_server_side_key
GROQ_MODEL=groq/compound-mini
GROQ_BASE_URL=https://api.groq.com/openai/v1
```

## Run locally

This folder is an independent Gradle project. It still requires a Gradle installation or a generated wrapper.

```bash
cd backend/ai-proxy
./gradlew bootRun
```

On Windows:

```powershell
cd backend\ai-proxy
.\gradlew.bat bootRun
```

## Endpoint

`POST /api/ai/coach`

Request:

```json
{
  "systemPrompt": "You are Bloom Coach.",
  "userPrompt": "Give me a weekly habit summary."
}
```

Response:

```json
{
  "text": "Keep tomorrow focused on one small habit and one focus round.",
  "model": "groq/compound-mini"
}
```

## Android configuration

Set the backend URL in the Android project root `local.properties` or CI environment:

```properties
aiBackendBaseUrl=https://your-bloom-ai-proxy.example.com
```

```bash
AI_BACKEND_BASE_URL=https://your-bloom-ai-proxy.example.com
```

If this value starts with `https://`, Bloom uses the backend proxy. If it is empty, debug builds can still use the direct Groq fallback configured with `groqApiKey`, but release builds do not embed a Groq key.

## Security behavior

- Requires HTTPS for provider and app-facing backend URLs.
- Masks emails, phone numbers, URLs, bearer tokens, and common API key formats.
- Caps prompt size through `bloom.ai.max-prompt-characters`.
- Returns generic errors instead of provider details.
- Does not log prompts or authorization headers.

## Tests

```bash
cd backend/ai-proxy
./gradlew test
```

Tests cover prompt sanitization and controller behavior without network calls.

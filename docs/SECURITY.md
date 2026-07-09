# Bloom Security Guide

## Threat Model

Bloom stores local productivity data that can reveal behavior patterns:

- habit names and categories
- reminder times
- Pomodoro sessions
- routine windows
- progress, streaks, and rewards
- optional profile name and email
- optional AI prompts

Main risks:

- leaked API keys
- local data extraction from device backups or compromised devices
- sensitive context sent to AI providers without consent
- accidental logging of prompts, habit names, or authorization headers
- insecure HTTP traffic
- reverse engineering of release APKs

## Secrets Policy

- Never commit real API keys, `.env` files, keystores, APKs, AABs, or signing material.
- `local.properties` is ignored by Git.
- Debug builds can read `groqApiKey` from `local.properties`.
- Release builds intentionally set `GROQ_API_KEY` to an empty string.
- A Groq key inside an APK must be considered extractable.
- Production should use `backend/ai-proxy`, which stores the Groq key server-side.
- Release builds can point to the proxy through non-secret `aiBackendBaseUrl`.
- The proxy can require `X-Bloom-Client-Token`; treat the matching Android value as abuse friction, not as a provider secret.

## AI Privacy Policy

Bloom Coach is optional.

Settings includes:

- `Enable Bloom Coach`
- `Allow habit context`

Defaults:

- Bloom Coach remote calls are disabled.
- Habit context sharing is disabled.

Before data is sent to an AI provider:

- prompts pass through `PrivacySanitizer`
- emails, phone numbers, URLs, bearer tokens, and common API-key patterns are masked
- prompt size is capped
- full database contents are never sent
- habit names and routine labels are included only when the user enables habit context sharing

## Network Policy

- `android:usesCleartextTraffic="false"` is set.
- `network_security_config.xml` blocks cleartext traffic.
- The default trust anchors are system CAs only.
- Groq calls must use HTTPS.
- OkHttp timeouts are configured.
- No logging interceptor is configured.
- The AI proxy supports a per-client in-memory rate limit and generic provider errors.
- Certificate pinning is not enabled yet because pin rotation and backend ownership must be planned first.

## Local Storage Policy

Current state:

- Room stores habits, completions, and Pomodoro sessions.
- DataStore stores lightweight app preferences.
- Android auto-backup is disabled.
- `CryptoManager` uses Android Keystore with AES/GCM/NoPadding.
- Habit free-text fields are encrypted before being persisted.
- Sensitive preference strings are encrypted before being persisted.
- Legacy plaintext habit and preference values are re-encrypted during app startup.
- JSON restore validates schema version, file size, item counts, and field bounds before resetting local data.

MVP limitation:

- Pomodoro session timestamps, completion timestamps, categories, goals, and aggregate metrics remain queryable plaintext because the app needs local analytics without server access.

## Logging Policy

- `SecureLogger` masks sensitive values.
- Release builds should not log API keys, authorization headers, prompts, AI responses, habit content, or personal data.
- Direct `println` and direct sensitive `Log.d` calls should not be introduced.
- Exceptions surfaced to users must use friendly messages and avoid stack traces.

## Build Hardening

Release build settings:

- `minifyEnabled = true`
- `shrinkResources = true`
- ProGuard/R8 rules are configured
- Groq key is empty in release
- cleartext traffic is disabled
- backup is disabled

## Permissions

Current permissions:

- `INTERNET` for optional Bloom Coach
- `POST_NOTIFICATIONS` for reminders on supported Android versions

No extra permissions should be added without a product reason.

## Before Play Store Release

- Add or restore Gradle Wrapper and run a clean release build.
- Verify no secrets are present in Git history or APK artifacts.
- Deploy `backend/ai-proxy` and point release builds to it with `aiBackendBaseUrl`.
- Configure `BLOOM_AI_CLIENT_TOKEN` server-side and matching `AI_BACKEND_CLIENT_TOKEN` for release builds.
- Add privacy policy copy for AI and local storage.
- Run mobile security testing aligned with OWASP MASVS.
- Test install, upgrade, Room migrations, and reset on a real device.

# Bloom

![Bloom banner](docs/assets/bloom-banner.png)

[![Download Bloom](https://img.shields.io/badge/Download-Bloom_Install_Page-1B1C1A?style=for-the-badge&logo=github)](https://joaovictorabreu-dev.github.io/Bloom/)
[![Install with QR](https://img.shields.io/badge/Install-QR_Code-8DAA91?style=for-the-badge)](https://joaovictorabreu-dev.github.io/Bloom/)

Bloom is a native Android app for habits, routine planning, Pomodoro focus, personal growth, and local AI coaching.

## Download

- Install page: [Bloom install page](https://joaovictorabreu-dev.github.io/Bloom/)
- Releases: [GitHub Releases](https://github.com/JoaoVictorAAbreu-Dev/Bloom/releases)
- QR install guide: [docs/USER_GUIDE.md](docs/USER_GUIDE.md#installing-with-the-qr-code)

It follows an Organic Productivity design language: calm, premium, minimal, and warm, with pixel-art details reserved for illustrations, rewards, and empty states.

## What Bloom does

- Helps users build daily habits
- Creates starter habits from onboarding choices
- Organizes routines by time of day
- Runs a functional Pomodoro timer
- Stores data locally with Room and DataStore
- Tracks streaks, sessions, and progress
- Shows 28-day consistency, monthly focus, best focus hour, and top habit
- Grows a rewards garden from local progress
- Offers Bloom Coach, an AI assistant powered by a production backend proxy or debug Groq fallback
- Exports a local JSON data snapshot from Settings

## Stack

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- ViewModel
- Coroutines and Flow
- Room
- DataStore
- MVVM with `ui`, `domain`, and `data`

## Main screens

- Splash
- Onboarding with goal, starter habits, notifications, and Pomodoro setup
- Offline-first auth entry
- Home dashboard
- Habits
- Create and edit habit
- Routine timeline
- Pomodoro focus
- Statistics
- Garden and rewards
- Profile and settings
- Bloom Coach AI

## V2 status

Implemented in the local MVP:

- Full onboarding flow with persisted setup choices
- Offline-first auth screens without backend dependency
- Advanced habit fields: priority, emoji, goals, custom repetition, color, icon
- Pomodoro Deep Focus toggle and local session persistence
- Advanced statistics and heatmap
- Full garden/rewards screen
- Profile metrics and categorized Settings
- Bloom Coach with Groq integration plus local analytic fallback
- Local JSON export snapshot

Deferred because they require external platform integrations:

- Google Drive backup/sync
- Android widgets
- Wear OS companion
- Spotify/YouTube Music integration
- Real notification blocking through Android DND policies

## Local AI setup

For production, Bloom Coach should use the backend proxy in `backend/ai-proxy` so the Groq key never ships inside the APK.

```properties
aiBackendBaseUrl=https://your-bloom-ai-proxy.example.com
```

CI builds can use `AI_BACKEND_BASE_URL` instead of `local.properties`.

Debug builds can still call Groq directly when the API key is configured in root `local.properties`.

```properties
groqApiKey=your_groq_api_key_here
groqModel=groq/compound-mini
groqBaseUrl=https://api.groq.com/openai/v1
```

If the key is missing, the app keeps working with local fallback guidance.

## Documentation

- Technical guide: [docs/TECHNICAL.md](docs/TECHNICAL.md)
- Production readiness: [docs/PRODUCTION.md](docs/PRODUCTION.md)
- User guide: [docs/USER_GUIDE.md](docs/USER_GUIDE.md)
- QA report: [docs/QA_V2.md](docs/QA_V2.md)
- Security guide: [docs/SECURITY.md](docs/SECURITY.md)
- AI proxy: [backend/ai-proxy/README.md](backend/ai-proxy/README.md)

## Project structure

```text
app/src/main/java/com/bloom/app
  data
  domain
  ui

backend/ai-proxy
  src/main/kotlin/com/bloom/aiproxy
  src/test/kotlin/com/bloom/aiproxy
```

## Installation and test

The repository currently needs a working Android SDK plus Gradle/Gradle Wrapper to generate a fresh APK.

After the build environment is available:

1. Build a new debug APK.
2. Generate a QR install page pointing to the fresh APK.
3. Scan the QR code from the Android device.
4. Allow installs from unknown sources if Android asks.
5. Open Bloom and complete onboarding.

## Notes

- The project is structured as a local-first MVP.
- No backend login is implemented yet.
- Login and cadastro screens are local-only placeholders for future sync.
- Backup/restore import is intentionally deferred until file-picker and cloud sync are validated.
- Any ignored APK under `app/build/outputs` may be stale and should not be treated as the final V2 build.

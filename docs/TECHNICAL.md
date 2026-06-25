# Bloom Technical Overview

## Summary

Bloom is a local-first Android app for habits, routine planning, Pomodoro focus, statistics, garden rewards, and an AI coach powered by Groq when configured.

## Architecture

- Language: Kotlin
- UI: Jetpack Compose + Material 3
- Navigation: Navigation Compose
- State: ViewModel + Flow + Coroutines
- Persistence: Room + DataStore
- Pattern: MVVM with `ui`, `domain`, and `data`

## Data layer

- Room stores habits, habit completions, and Pomodoro sessions.
- DataStore stores user preferences, theme mode, onboarding state, and Pomodoro configuration.
- The AI coach uses a repository boundary so the app can fall back to local guidance when Groq is not configured.

## AI integration

Bloom Coach builds a prompt from the current app context:

- current habits
- routine blocks
- streaks
- focus minutes today
- unlocked rewards
- recent sessions

If `groqApiKey` is present in `app/local.properties`, Bloom sends the request to Groq chat completions.
If not, the app returns a local fallback response and keeps working.

Default model:

- `groq/compound-mini`

Groq API base URL:

- `https://api.groq.com/openai/v1`

## Install package

The install package is meant to be served over HTTP on a local network while testing on a phone.
The QR code points to the install page, not directly to a source file path.

## Known constraints

- The project was prepared without Android Studio on this machine.
- Final validation depends on the local Android SDK, emulator, and available Gradle wrapper.
- If the QR install page is served from another machine, update the QR target URL before sharing it.


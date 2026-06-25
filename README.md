# Bloom

Bloom is a native Android productivity app built with Kotlin, Jetpack Compose, Material 3, Navigation Compose, Room, DataStore, Coroutines, and Flow.

It is designed around an "Organic Productivity" visual language: calm, minimal, premium, and soft, with pixel-art accents reserved for illustrations, rewards, and empty states.

## Features

- Splash screen with animated Bloom logo
- 3-step onboarding
- Home dashboard with progress, focus, garden, and routine sections
- Habit CRUD with categories, reminders, streaks, and daily completion
- Vertical routine timeline
- Pomodoro timer with start, pause, resume, stop, and cycle switching
- Local persistence with Room and DataStore
- Statistics and growth overview
- Garden and rewards screen
- Profile and settings screen with theme controls and reset flow
- Bloom Coach AI screen powered by Groq with local fallback suggestions

## How to use

1. Install the APK.
2. Open Bloom and complete onboarding.
3. Add or complete habits on the Home and Habits screens.
4. Use Focus for Pomodoro sessions.
5. Open Bloom Coach for planning help or quick guidance.
6. Review progress in Statistics and Garden.

## Installation

- Open the install page from the QR code.
- Download the APK.
- Allow installs from unknown sources when Android asks.
- Launch the app after installation.

## Documentation

- Technical overview: `docs/TECHNICAL.md`
- User guide: `docs/USER_GUIDE.md`

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

## Groq AI setup

Bloom Coach reads its Groq configuration from `app/local.properties` through Gradle build config fields:

```properties
groqApiKey=your_groq_api_key_here
groqModel=groq/compound-mini
groqBaseUrl=https://api.groq.com/openai/v1
```

`groq/compound-mini` is the default model because Groq's model list currently shows no per-token price for it. If the key is missing, Bloom Coach stays usable through the local fallback coach logic.

## Project Structure

- `app/src/main/java/com/bloom/app/data`
- `app/src/main/java/com/bloom/app/domain`
- `app/src/main/java/com/bloom/app/ui`

## Notes

- The project is structured to compile in Android Studio with the Android SDK installed.
- If your local SDK path differs, Android Studio should resolve it automatically from your environment.
- Demo seed data is included so the MVP is visually useful on first launch.

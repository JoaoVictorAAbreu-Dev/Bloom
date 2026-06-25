# Bloom Technical Overview

## Purpose

Bloom is a local-first Android MVP for habits, routines, Pomodoro focus, growth tracking, and optional AI assistance.

The current implementation is intentionally lightweight but structured so it can grow without rewriting the whole app.

## Architecture

| Layer | Responsibility |
| --- | --- |
| `ui` | Compose screens, reusable components, theme, navigation, and view state |
| `domain` | Business models, repository contracts, and use cases |
| `data` | Room, DataStore, repositories, mappers, and local persistence |

Main patterns:

- MVVM
- Unidirectional state flow
- Repository abstraction
- Coroutines and Flow for async state
- Navigation Compose for screen routing

## Persistence

### Room

Room stores the app data that must survive restarts:

- habits
- habit completions
- Pomodoro sessions

### DataStore

DataStore stores lightweight preferences:

- theme mode
- onboarding completion
- Pomodoro defaults
- user settings

## AI integration

Bloom Coach uses Groq through a repository boundary.

Behavior:

1. The app gathers context from habits, streaks, focus time, and recent sessions.
2. If `groqApiKey` exists in `app/local.properties`, Bloom sends the prompt to Groq.
3. If the key is absent, the app returns a local fallback response.

Default configuration:

```properties
groqApiKey=your_groq_api_key_here
groqModel=groq/compound-mini
groqBaseUrl=https://api.groq.com/openai/v1
```

`groq/compound-mini` is used as the default open model target.

## UI system

The UI is built with:

- Material 3
- custom Bloom theme
- reusable cards, buttons, icons, and progress rings
- calm spacing based on an 8 dp grid
- rounded corners between 16 dp and 24 dp

Pixel art is restricted to:

- mascot
- rewards
- plants
- empty states

The functional UI remains clean and modern.

## Main flows

- Splash -> Onboarding -> Home
- Home -> Habits / Focus / Routine / Garden / Settings
- Habits -> Create and edit habit
- Focus -> Pomodoro session history
- Statistics -> growth and consistency overview

## Repository layout

```text
app/src/main/java/com/bloom/app
  data
  domain
  ui
```

## Build and install

The delivery package in `outputs/bloom-install` contains:

- `Bloom-debug.apk`
- `index.html`
- `install-qr.png`
- user guide
- technical guide

The install page is designed to be opened from a phone on the same local network.

## Current constraints

- This workspace was prepared without Android Studio on the machine.
- Final build validation still depends on the Android SDK and a working Gradle setup.
- The current delivery is an MVP, not a complete production release.

## Future improvements

- Gradle wrapper should be added if the repo is meant to build from CLI everywhere.
- Add UI tests for the main navigation flows.
- Add more real screenshots to the gallery after device validation.
- Expand the AI coach with better context summarization and offline caching.

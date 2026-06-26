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
- offline auth completion
- primary goal
- Pomodoro defaults
- user settings

## AI integration

Bloom Coach uses Groq through a repository boundary.
Security details are documented in [SECURITY.md](SECURITY.md).

Behavior:

1. The app gathers context from habits, streaks, focus time, statistics, rewards, and recent sessions.
2. If `aiBackendBaseUrl` starts with `https://`, Bloom sends the prompt to the production backend proxy.
3. If no backend URL is configured, debug builds can send the prompt directly to Groq using `groqApiKey` from root `local.properties`.
4. If the key is absent or the request fails, the app returns a local analytic fallback response.

Production configuration:

```properties
aiBackendBaseUrl=https://your-bloom-ai-proxy.example.com
```

Debug-only direct provider configuration:

```properties
groqApiKey=your_groq_api_key_here
groqModel=groq/compound-mini
groqBaseUrl=https://api.groq.com/openai/v1
```

`groq/compound-mini` is used as the default open model target.

The backend proxy lives in `backend/ai-proxy` and keeps `GROQ_API_KEY` server-side.

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
- Splash -> Onboarding -> Local auth -> Home on first run
- Home -> Habits / Focus / Routine / Garden / Settings
- Habits -> Create and edit habit
- Focus -> Pomodoro session history
- Statistics -> weekly, monthly, heatmap, and productivity-pattern overview
- Settings -> local JSON export snapshot

## V2 implementation notes

- Onboarding persists the user's primary goal, notification preference, Pomodoro setup, and selected starter habits.
- Habit persistence uses Room version 2 with a migration for priority, emoji, daily goal, weekly goal, and custom repetition.
- Habit free-text fields and sensitive preference strings are encrypted with Android Keystore before persistence.
- Existing plaintext habit/preference values are re-encrypted lazily during app startup.
- Bloom Coach receives richer prompt context: average focus, best focus hour, top habit, monthly focus, and 28-day habit activity.
- Deep Focus is currently an in-app mode. It changes the focus experience and copy, but it does not request Android DND access yet.
- Local export is rendered in Settings as a JSON snapshot for inspection/copying. File save/share can be added after Android file-picker validation.

## Repository layout

```text
app/src/main/java/com/bloom/app
  data
  domain
  ui
```

## Build and install

The repository currently does not include a versioned install package.

To generate a fresh install artifact:

1. Add or restore the Gradle Wrapper, or install Gradle locally.
2. Build a debug APK with the Android SDK.
3. Generate a QR install page that points to the new APK.
4. Verify the APK on a real Android device.

Ignored files under `app/build/outputs` can exist locally, but they may be stale and are not treated as validated V2 artifacts.

## Current constraints

- This workspace was prepared without Android Studio on the machine.
- Final build validation still depends on the Android SDK and a working Gradle setup.
- This repo currently does not include `gradlew.bat`, and `gradle` is not available on this machine.
- The current delivery is an MVP, not a complete production release.

## Future improvements

- Gradle wrapper should be added if the repo is meant to build from CLI everywhere.
- Add UI tests for the main navigation flows.
- Add Android widgets for Pomodoro, next habit, progress, and garden.
- Add Wear OS companion support.
- Add Google Drive backup and restore.
- Add real DND integration for Deep Focus after permission flow design.

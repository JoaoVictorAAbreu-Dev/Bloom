# Bloom V2 QA Report

## Scope Reviewed

This QA pass covers the local-first Bloom V2 implementation:

- onboarding and local auth
- habits CRUD and advanced habit fields
- routine timeline
- Pomodoro and Deep Focus
- statistics and heatmap
- garden and rewards
- profile and settings
- Bloom Coach with Groq fallback
- production AI proxy
- encrypted local free-text fields and sensitive preferences
- local JSON export snapshot
- documentation accuracy

## Implementation Status

| Area | Status | Notes |
| --- | --- | --- |
| Onboarding | Implemented | Persists primary goal, notification preference, starter habits, and Pomodoro setup. |
| Offline auth | Implemented | Local-only login/cadastro/guest flow, no backend sync. |
| Profile | Implemented | Shows avatar, user name, active days, habits, focus, streak, rewards, and goal. |
| Settings | Implemented | Categorized settings, Pomodoro preferences, AI status, local export, reset. |
| Habits | Implemented | CRUD, complete today, streaks, priority, emoji, color, icon, goals, custom repeat. |
| Routine | Implemented | Timeline by Morning, Afternoon, Evening, Night with current activity highlight. |
| Pomodoro | Implemented | Start, pause, resume, stop, cycle switching, Room session persistence. |
| Deep Focus | Partial | In-app mode exists. Android DND notification blocking is deferred. |
| Statistics | Implemented | Weekly chart, 28-day heatmap, monthly focus, average focus, best hour, top habit. |
| Garden | Implemented | Full garden screen with visual unlock progression and rewards. |
| Bloom Coach | Implemented | Dedicated chat, quick actions, summaries, local recommendations, Groq integration. |
| AI proxy | Implemented | Spring Boot proxy keeps Groq key server-side for production. |
| Local encryption | Implemented | Habit free text and sensitive preferences use Android Keystore with lazy plaintext re-encryption. |
| Export | Implemented | In-app JSON snapshot, native share, file save, and JSON import are implemented. |
| Widgets/Wear/music | Deferred | Requires platform or third-party integrations outside this MVP pass. |

## Static Validation Performed

- `git diff --check` passed after each implementation block.
- Verified `:app:assembleDebug` and `testDebugUnitTest` with Java 21 and the Gradle Wrapper.
- Searched for broken call sites after API changes:
  - `HabitEditorScreen`
  - `FocusScreen`
  - `SettingsScreen`
  - `UserPreferences`
  - `BloomStatistics`
- Searched runtime source folders for common mojibake patterns after rewriting Bloom Coach and Home copy.
- Verified working tree was clean between commits.
- Verified recent commit history contains separate implementation commits.
- Added backend unit tests for sanitizer and controller behavior.

## Build Validation

Validated locally:

- `.\gradlew.bat :app:assembleDebug`
- `.\gradlew.bat testDebugUnitTest`
- `.\gradlew.bat :app:assembleRelease`
- `.\gradlew.bat -p backend/ai-proxy test`

Release artifact:

- `app/build/outputs/apk/release/app-release-unsigned.apk`
- Local binary search found no obvious embedded Groq/OpenAI key patterns in the release APK.

Environment notes:

- The workspace uses JDK 21 for Android builds.
- The repository now includes Gradle Wrapper scripts, so no global Gradle installation is required.

There is an ignored `app/build/outputs/apk/debug/app-debug.apk`, but it should only be treated as current if rebuilt after the latest V2 commits.

## UX Review

Strengths:

- The first-run flow now explains Bloom, asks for intent, and creates useful starter data.
- The app is more coherent as a daily system: habits, routine, focus, stats, garden, profile, and AI now reinforce each other.
- The Coach is useful offline and not dependent on Groq for core value.
- Habit completion now has immediate haptic/animated feedback.
- Settings are clearer and separated by user mental model.

Risks:

- Some advanced features are currently UI/data-level only and need deeper Android platform integration later.
- Deep Focus does not block system notifications yet.
- Export now supports snapshot generation, native share, file save, and JSON import.
- QR/APK installation must be regenerated after a real Android build.
- No automated UI tests were added in this pass.

## Recommended Next Validation

1. Install the fresh APK on a real Android device.
2. Test first run: splash -> onboarding -> local auth -> home.
3. Test habit CRUD, completion animation, and Room migration from version 1 to 2.
4. Test Pomodoro completion persistence and interrupted session persistence.
5. Test Bloom Coach with no Groq key and with a valid Groq key.
6. Test Bloom Coach through `backend/ai-proxy` over HTTPS.
7. Add cloud sync backup/restore if the release scope still requires it.

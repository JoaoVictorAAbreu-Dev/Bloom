# Bloom User Guide

## What Bloom is for

Bloom helps you keep a daily rhythm with:

- habits
- routines
- Pomodoro focus sessions
- growth statistics
- Bloom Coach for planning support

## First use

1. Install the APK from the Bloom install page.
2. Open the app.
3. Wait for the splash screen.
4. Finish onboarding by choosing your goal, starter habits, notification preference, and Pomodoro setup.
5. Continue with local auth or without a real online account.
6. Review your Home dashboard.

## Daily workflow

### Habits

Use the Habits screen to:

- create a new habit
- edit an existing habit
- delete a habit
- mark a habit as complete for the day
- review streaks
- set priority, emoji, color, icon, goals, and custom repetition

### Routine

Use the Routine screen to see your day by period:

- Morning
- Afternoon
- Evening
- Night

### Focus

Use Focus to run Pomodoro sessions:

- Start
- Pause
- Resume
- Stop
- Deep Focus toggle

### Statistics

Use Statistics to review:

- focus time
- habits completed
- longest streak
- garden growth
- consistency heatmap
- monthly focus
- best focus hour
- most completed habit

### Garden

Use Garden to unlock plants and rewards based on consistency.

### Settings

Use Settings to:

- change display name
- change theme
- tune Pomodoro durations
- configure notification and auto-start preferences
- open Bloom Coach
- enable or disable Bloom Coach
- decide whether habit context can be used for AI suggestions
- generate a local JSON export snapshot
- save the export as a JSON file
- import a saved JSON backup
- reset local data

## Bloom Coach

Bloom Coach is the AI assistant inside the app.

You can ask for:

- a plan for the day
- help starting a focus session
- a habit reminder
- a short review at the end of the day
- weekly and monthly analysis
- next-best-action recommendations

If Groq is not configured, the app still replies with local fallback guidance.

## Current limitations

- Account screens are offline-first and do not sync to a backend yet.
- Deep Focus is an in-app mode and does not block Android notifications yet.
- JSON export can be shown, saved, shared, or imported from Settings using Android file pickers.
- Google Drive backup, widgets, Wear OS, and music integrations are not part of the current MVP.

## Installing with the QR code

The QR install page must be generated from a fresh APK after the Android build environment is available.

Recommended flow:

1. Build a new debug APK.
2. Generate the QR install page from that APK.
3. Open the install page on your phone.
4. Scan the QR code.
5. Download the APK.
6. Allow installs from unknown sources if Android asks.
7. Open Bloom after installation.

## Troubleshooting

- If the app does not install, verify the phone is on the same network as the PC.
- If Bloom Coach says the API is not configured, use `aiBackendBaseUrl` for production or `groqApiKey` only for a debug build.
- If the visual preview looks outdated, regenerate the screenshots after the next UI update.

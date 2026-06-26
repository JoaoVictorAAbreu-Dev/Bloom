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
- generate a local JSON export snapshot
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
- JSON export is shown inside Settings; file save/share is a later Android integration.
- Google Drive backup, widgets, Wear OS, and music integrations are not part of the current MVP.

## Installing with the QR code

1. Open the install page on your phone.
2. Scan the QR code.
3. Download `Bloom-debug.apk`.
4. Allow installs from unknown sources if Android asks.
5. Open Bloom after installation.

## Troubleshooting

- If the app does not install, verify the phone is on the same network as the PC.
- If Bloom Coach says the API is not configured, add `groqApiKey` to `app/local.properties`.
- If the visual preview looks outdated, regenerate the screenshots after the next UI update.

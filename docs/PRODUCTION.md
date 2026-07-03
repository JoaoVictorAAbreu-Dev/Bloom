# Bloom Production Readiness

## Production AI

Release builds should use `backend/ai-proxy` instead of calling Groq directly from Android.

Required steps:

1. Deploy `backend/ai-proxy` behind HTTPS.
2. Store `GROQ_API_KEY` only in the backend environment.
3. Configure Android with root `local.properties` or CI environment variable:

```properties
aiBackendBaseUrl=https://your-bloom-ai-proxy.example.com
```

```bash
AI_BACKEND_BASE_URL=https://your-bloom-ai-proxy.example.com
```

4. Build the Android release artifact.
5. Verify Bloom Coach works with AI enabled and habit context disabled/enabled.

## Android Release Build

Required local tooling:

- JDK 17 or 21
- Android SDK
- Gradle or Gradle Wrapper

The repository now includes Gradle Wrapper scripts, so a clean local build can be run without installing Gradle globally.

Expected command:

```powershell
.\gradlew.bat :app:assembleRelease
```

## Local Data Protection

Implemented:

- Android backups disabled.
- Cleartext traffic disabled.
- Release build removes the Groq key from `BuildConfig`.
- Habit free-text fields are encrypted with Android Keystore before persistence.
- Existing plaintext habit fields are re-encrypted on app startup.
- Sensitive preference strings are encrypted with Android Keystore.
- Existing plaintext preference values are re-encrypted on app startup.

Important limitation:

- Encryption protects app-managed local storage. It does not protect data while the device is unlocked and the app is running.

## Pre-Publish Checklist

- Run Android unit tests.
- Run a clean release build.
- Install the release APK or AAB on a real Android device.
- Test first-run flow, habit CRUD, Pomodoro persistence, Bloom Coach, export, reset, and dark/light theme.
- Inspect release artifact for secrets.
- Run mobile security review aligned with OWASP MASVS.
- Publish a user-facing privacy policy covering local data and optional AI processing.

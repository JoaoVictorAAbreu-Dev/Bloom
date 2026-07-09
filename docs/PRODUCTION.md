# Bloom Production Readiness

## Production AI

Release builds should use `backend/ai-proxy` instead of calling Groq directly from Android.

Required steps:

1. Deploy `backend/ai-proxy` behind HTTPS.
2. Store `GROQ_API_KEY` only in the backend environment.
3. Configure `BLOOM_AI_CLIENT_TOKEN` on the proxy and a matching Android client token.
4. Configure Android with root `local.properties` or CI environment variables:

```properties
aiBackendBaseUrl=https://your-bloom-ai-proxy.example.com
aiBackendClientToken=replace_with_random_client_token
```

```bash
AI_BACKEND_BASE_URL=https://your-bloom-ai-proxy.example.com
AI_BACKEND_CLIENT_TOKEN=replace_with_random_client_token
```

5. Build the Android release artifact.
6. Verify Bloom Coach works with AI enabled and habit context disabled/enabled.

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

CI also runs Android unit tests, debug build, release APK build, release AAB build, proxy tests, and artifact upload on pushes and pull requests to `master`.

Pull requests also run Dependency Review when dependency manifests change. Dependabot opens weekly update PRs for Gradle dependencies and GitHub Actions.

## Release Signing

Unsigned release artifacts are useful only for validation. A real install/distribution build must be signed.

Generate a local upload key:

```powershell
keytool -genkeypair `
  -v `
  -keystore bloom-release.jks `
  -storetype JKS `
  -keyalg RSA `
  -keysize 4096 `
  -validity 10000 `
  -alias bloom-release
```

Base64 encode it for CI:

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("bloom-release.jks")) | Set-Content bloom-release.base64.txt
```

On macOS/Linux:

```bash
base64 -w 0 bloom-release.jks > bloom-release.base64.txt
```

Configure these GitHub Secrets:

```text
BLOOM_RELEASE_KEYSTORE_BASE64
BLOOM_RELEASE_KEYSTORE_PASSWORD
BLOOM_RELEASE_KEY_ALIAS
BLOOM_RELEASE_KEY_PASSWORD
```

Local `local.properties` equivalents:

```properties
releaseKeystoreBase64=base64_encoded_jks_or_keystore
releaseKeystorePassword=replace_with_store_password
releaseKeyAlias=bloom-release
releaseKeyPassword=replace_with_key_password
```

Artifact naming:

- With signing Secrets: `bloom-release-signed-apk` and `bloom-release-signed-aab`
- Without signing Secrets: `bloom-release-unsigned-apk` and `bloom-release-unsigned-aab`

The workflow verifies APK signing when signing Secrets are present.

## Local Data Protection

Implemented:

- Android backups disabled.
- Cleartext traffic disabled.
- Release build removes the Groq key from `BuildConfig`.
- Release signing can be driven by CI Secrets without committing keystores.
- Habit free-text fields are encrypted with Android Keystore before persistence.
- Existing plaintext habit fields are re-encrypted on app startup.
- Sensitive preference strings are encrypted with Android Keystore.
- Existing plaintext preference values are re-encrypted on app startup.
- JSON import is bounded and validated before local data is reset.

Important limitation:

- Encryption protects app-managed local storage. It does not protect data while the device is unlocked and the app is running.

## Pre-Publish Checklist

- Run Android unit tests.
- Run a clean release build.
- Install the release APK or AAB on a real Android device.
- Test first-run flow, habit CRUD, Pomodoro persistence, Bloom Coach, export, reset, and dark/light theme.
- Inspect release artifact for secrets.
- Exercise proxy token rejection and rate limiting in the deployed environment.
- Run mobile security review aligned with OWASP MASVS.
- Publish a user-facing privacy policy covering local data and optional AI processing.

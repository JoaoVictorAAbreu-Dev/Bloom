import java.util.Properties
import java.util.Base64

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

fun String.toBuildConfigValue(): String = replace("\\", "\\\\").replace("\"", "\\\"")

fun secretProperty(name: String, envName: String, defaultValue: String = ""): String {
    return localProperties.getProperty(name) ?: System.getenv(envName) ?: defaultValue
}

val releaseKeystoreBase64 = secretProperty("releaseKeystoreBase64", "BLOOM_RELEASE_KEYSTORE_BASE64")
val releaseKeystorePassword = secretProperty("releaseKeystorePassword", "BLOOM_RELEASE_KEYSTORE_PASSWORD")
val releaseKeyAlias = secretProperty("releaseKeyAlias", "BLOOM_RELEASE_KEY_ALIAS")
val releaseKeyPassword = secretProperty("releaseKeyPassword", "BLOOM_RELEASE_KEY_PASSWORD")
val hasReleaseSigningConfig = listOf(
    releaseKeystoreBase64,
    releaseKeystorePassword,
    releaseKeyAlias,
    releaseKeyPassword,
).all { it.isNotBlank() }

android {
    namespace = "com.bloom.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.bloom.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (hasReleaseSigningConfig) {
            create("release") {
                val keystoreFile = layout.buildDirectory.file("generated/signing/bloom-release.jks").get().asFile
                keystoreFile.parentFile.mkdirs()
                keystoreFile.writeBytes(Base64.getDecoder().decode(releaseKeystoreBase64))

                storeFile = keystoreFile
                storePassword = releaseKeystorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
                enableV1Signing = false
                enableV2Signing = true
                enableV3Signing = true
                enableV4Signing = true
            }
        }
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "GROQ_API_KEY",
                "\"${secretProperty("groqApiKey", "GROQ_API_KEY").toBuildConfigValue()}\"",
            )
            buildConfigField(
                "String",
                "GROQ_MODEL",
                "\"${secretProperty("groqModel", "GROQ_MODEL", "groq/compound-mini").toBuildConfigValue()}\"",
            )
            buildConfigField(
                "String",
                "GROQ_BASE_URL",
                "\"${secretProperty("groqBaseUrl", "GROQ_BASE_URL", "https://api.groq.com/openai/v1").toBuildConfigValue()}\"",
            )
            buildConfigField(
                "String",
                "AI_BACKEND_BASE_URL",
                "\"${secretProperty("aiBackendBaseUrl", "AI_BACKEND_BASE_URL").toBuildConfigValue()}\"",
            )
            buildConfigField(
                "String",
                "AI_BACKEND_CLIENT_TOKEN",
                "\"${secretProperty("aiBackendClientToken", "AI_BACKEND_CLIENT_TOKEN").toBuildConfigValue()}\"",
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            // Production builds must not embed provider API keys. Use a backend proxy for Groq.
            buildConfigField("String", "GROQ_API_KEY", "\"\"")
            buildConfigField("String", "GROQ_MODEL", "\"groq/compound-mini\"")
            buildConfigField("String", "GROQ_BASE_URL", "\"https://api.groq.com/openai/v1\"")
            buildConfigField(
                "String",
                "AI_BACKEND_BASE_URL",
                "\"${secretProperty("aiBackendBaseUrl", "AI_BACKEND_BASE_URL").toBuildConfigValue()}\"",
            )
            buildConfigField(
                "String",
                "AI_BACKEND_CLIENT_TOKEN",
                "\"${secretProperty("aiBackendClientToken", "AI_BACKEND_CLIENT_TOKEN").toBuildConfigValue()}\"",
            )
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            if (hasReleaseSigningConfig) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")

    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.core:core-ktx:1.19.0")
    implementation("androidx.datastore:datastore-preferences:1.2.1")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-text")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")
    implementation("androidx.navigation:navigation-compose:2.9.8")
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    kapt("androidx.room:room-compiler:2.8.4")
    implementation("com.google.accompanist:accompanist-placeholder-material:0.34.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")

    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

kapt {
    correctErrorTypes = true
}

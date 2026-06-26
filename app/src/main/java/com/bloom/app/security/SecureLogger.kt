package com.bloom.app.security

import android.util.Log
import com.bloom.app.BuildConfig

object SecureLogger {
    fun debug(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message.maskSensitiveData())
        }
    }

    fun warn(tag: String, message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, message.maskSensitiveData(), throwable?.sanitized())
        }
    }

    fun String.maskSensitiveData(): String {
        return PrivacySanitizer().sanitize(this)
            .replace(Regex("(?i)(authorization|api[-_ ]?key|token)\\s*[:=]\\s*\\S+"), "$1=[secret]")
    }

    private fun Throwable.sanitized(): Throwable {
        return IllegalStateException(message?.maskSensitiveData() ?: "Sanitized error")
    }
}

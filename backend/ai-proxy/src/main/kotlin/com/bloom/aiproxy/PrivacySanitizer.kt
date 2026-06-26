package com.bloom.aiproxy

import org.springframework.stereotype.Component

@Component
class PrivacySanitizer {
    fun sanitize(input: String, maxLength: Int): String {
        return input
            .replace(EMAIL_PATTERN, "[email]")
            .replace(PHONE_PATTERN, "[phone]")
            .replace(URL_PATTERN, "[url]")
            .replace(TOKEN_PATTERN, "[secret]")
            .take(maxLength)
    }

    private companion object {
        val EMAIL_PATTERN = Regex("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", RegexOption.IGNORE_CASE)
        val PHONE_PATTERN = Regex("\\+?\\d[\\d\\s().-]{7,}\\d")
        val URL_PATTERN = Regex("https?://\\S+", RegexOption.IGNORE_CASE)
        val TOKEN_PATTERN = Regex("(sk-[A-Za-z0-9_-]{12,}|gsk_[A-Za-z0-9_-]{12,}|Bearer\\s+[A-Za-z0-9._-]+)", RegexOption.IGNORE_CASE)
    }
}

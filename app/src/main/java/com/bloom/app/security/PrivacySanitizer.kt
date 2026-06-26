package com.bloom.app.security

class PrivacySanitizer(
    private val maxPromptLength: Int = 2_400,
) {
    fun sanitize(text: String): String {
        return text
            .replace(EMAIL_PATTERN, "[email]")
            .replace(PHONE_PATTERN, "[phone]")
            .replace(URL_PATTERN, "[url]")
            .replace(TOKEN_PATTERN, "[secret]")
            .replace(NAME_HINT_PATTERN, "$1 [name]")
            .take(maxPromptLength)
    }

    private companion object {
        val EMAIL_PATTERN = Regex("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", RegexOption.IGNORE_CASE)
        val PHONE_PATTERN = Regex("\\+?\\d[\\d\\s().-]{7,}\\d")
        val URL_PATTERN = Regex("https?://\\S+", RegexOption.IGNORE_CASE)
        val TOKEN_PATTERN = Regex("(sk-[A-Za-z0-9_-]{12,}|gsk_[A-Za-z0-9_-]{12,}|Bearer\\s+[A-Za-z0-9._-]+)", RegexOption.IGNORE_CASE)
        val NAME_HINT_PATTERN = Regex("\\b(name|nome|user|usuario)\\s*[:=]\\s*[^,;\\n]+", RegexOption.IGNORE_CASE)
    }
}

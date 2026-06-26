package com.bloom.aiproxy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PrivacySanitizerTest {
    private val sanitizer = PrivacySanitizer()

    @Test
    fun `masks common sensitive values`() {
        val output = sanitizer.sanitize(
            "Email me at alex@example.com, call +55 11 99999-9999, open https://example.com and use Bearer abc.def",
            maxLength = 500,
        )

        assertTrue(output.contains("[email]"))
        assertTrue(output.contains("[phone]"))
        assertTrue(output.contains("[url]"))
        assertTrue(output.contains("[secret]"))
        assertFalse(output.contains("alex@example.com"))
        assertFalse(output.contains("https://example.com"))
    }

    @Test
    fun `limits prompt length`() {
        val output = sanitizer.sanitize("abcdef", maxLength = 3)

        assertEquals("abc", output)
    }
}

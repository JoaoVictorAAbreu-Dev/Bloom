package com.bloom.app.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SecureLoggerTest {
    @Test
    fun `masks secrets before logging`() {
        val sanitized = SecureLogger.run {
            "Authorization: Bearer abc.def.ghi apiKey=secret123 token: xyz".maskSensitiveData()
        }

        assertTrue(sanitized.contains("[secret]"))
        assertFalse(sanitized.contains("abc.def.ghi"))
        assertFalse(sanitized.contains("secret123"))
        assertFalse(sanitized.contains("xyz"))
    }
}

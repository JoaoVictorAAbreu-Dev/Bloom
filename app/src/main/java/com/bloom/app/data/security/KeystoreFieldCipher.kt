package com.bloom.app.data.security

import com.bloom.app.security.CryptoManager
import com.bloom.app.security.EncryptedPayload

class KeystoreFieldCipher(
    private val cryptoManager: CryptoManager = CryptoManager(),
) : FieldCipher {
    override fun encrypt(plainText: String): String {
        if (plainText.isBlank()) return plainText
        val encrypted = cryptoManager.encrypt(plainText)
        return "$PREFIX${encrypted.version}:${encrypted.iv}:${encrypted.cipherText}"
    }

    override fun decrypt(storedText: String): String {
        if (!storedText.startsWith(PREFIX)) return storedText
        val payload = storedText.removePrefix(PREFIX).toEncryptedPayload()
        return runCatching { cryptoManager.decrypt(payload) }.getOrElse { PROTECTED_FALLBACK }
    }

    override fun isEncrypted(storedText: String): Boolean {
        return storedText.startsWith(PREFIX)
    }

    private fun String.toEncryptedPayload(): EncryptedPayload {
        val parts = split(":", limit = 3)
        require(parts.size == 3) { "Invalid encrypted field payload" }
        return EncryptedPayload(
            version = parts[0].toInt(),
            iv = parts[1],
            cipherText = parts[2],
        )
    }

    private companion object {
        const val PREFIX = "enc:v"
        const val PROTECTED_FALLBACK = "Protected data unavailable"
    }
}

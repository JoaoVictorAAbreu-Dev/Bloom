package com.bloom.app.data.security

interface FieldCipher {
    fun encrypt(plainText: String): String
    fun decrypt(storedText: String): String
    fun isEncrypted(storedText: String): Boolean
}

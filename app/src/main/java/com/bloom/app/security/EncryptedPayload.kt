package com.bloom.app.security

data class EncryptedPayload(
    val cipherText: String,
    val iv: String,
    val version: Int = 1,
)

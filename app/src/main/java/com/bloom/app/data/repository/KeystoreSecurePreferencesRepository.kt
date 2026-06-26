package com.bloom.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bloom.app.data.local.bloomPreferencesDataStore
import com.bloom.app.domain.repository.SecurePreferencesRepository
import com.bloom.app.security.CryptoManager
import com.bloom.app.security.EncryptedPayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class KeystoreSecurePreferencesRepository(
    context: Context,
    private val cryptoManager: CryptoManager = CryptoManager(),
) : SecurePreferencesRepository {
    private val dataStore = context.bloomPreferencesDataStore

    override fun observeSensitiveString(key: String): Flow<String?> {
        val prefKey = stringPreferencesKey(key.secureKey())
        return dataStore.data.map { preferences ->
            preferences[prefKey]?.let { serialized ->
                runCatching { cryptoManager.decrypt(serialized.toEncryptedPayload()) }.getOrNull()
            }
        }
    }

    override suspend fun setSensitiveString(key: String, value: String) {
        val prefKey = stringPreferencesKey(key.secureKey())
        val encrypted = cryptoManager.encrypt(value)
        dataStore.edit { preferences ->
            preferences[prefKey] = encrypted.serialize()
        }
    }

    override suspend fun clearSensitiveString(key: String) {
        val prefKey = stringPreferencesKey(key.secureKey())
        dataStore.edit { preferences -> preferences.remove(prefKey) }
    }

    private fun String.secureKey(): String = "secure_${filter { it.isLetterOrDigit() || it == '_' }.take(48)}"

    private fun EncryptedPayload.serialize(): String = "$version:$iv:$cipherText"

    private fun String.toEncryptedPayload(): EncryptedPayload {
        val parts = split(":", limit = 3)
        require(parts.size == 3) { "Invalid encrypted payload" }
        return EncryptedPayload(
            version = parts[0].toInt(),
            iv = parts[1],
            cipherText = parts[2],
        )
    }
}

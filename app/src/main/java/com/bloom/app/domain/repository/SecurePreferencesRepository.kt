package com.bloom.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface SecurePreferencesRepository {
    fun observeSensitiveString(key: String): Flow<String?>
    suspend fun setSensitiveString(key: String, value: String)
    suspend fun clearSensitiveString(key: String)
}

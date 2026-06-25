package com.bloom.app.domain.repository

import com.bloom.app.domain.model.PomodoroSession
import kotlinx.coroutines.flow.Flow

interface PomodoroRepository {
    fun observeSessions(): Flow<List<PomodoroSession>>
    suspend fun saveSession(session: PomodoroSession)
    suspend fun reset()
}

package com.bloom.app.data.repository

import com.bloom.app.data.dao.PomodoroSessionDao
import com.bloom.app.data.mapper.toDomain
import com.bloom.app.data.mapper.toEntity
import com.bloom.app.domain.model.PomodoroSession
import com.bloom.app.domain.repository.PomodoroRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PomodoroRepositoryImpl(
    private val pomodoroSessionDao: PomodoroSessionDao,
) : PomodoroRepository {
    override fun observeSessions(): Flow<List<PomodoroSession>> {
        return pomodoroSessionDao.observeSessions().map { sessions -> sessions.map { it.toDomain() } }
    }

    override suspend fun saveSession(session: PomodoroSession) {
        pomodoroSessionDao.insert(session.toEntity())
    }

    override suspend fun reset() {
        pomodoroSessionDao.deleteAll()
    }
}

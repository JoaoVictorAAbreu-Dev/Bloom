package com.bloom.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bloom.app.data.entity.PomodoroSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PomodoroSessionDao {
    @Query("SELECT * FROM pomodoro_sessions ORDER BY startedAtMillis DESC")
    fun observeSessions(): Flow<List<PomodoroSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: PomodoroSessionEntity)

    @Query("DELETE FROM pomodoro_sessions")
    suspend fun deleteAll()
}

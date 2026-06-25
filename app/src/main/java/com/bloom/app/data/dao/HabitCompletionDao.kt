package com.bloom.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bloom.app.data.entity.HabitCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCompletionDao {
    @Query("SELECT * FROM habit_completions ORDER BY completedAtMillis DESC")
    fun observeCompletions(): Flow<List<HabitCompletionEntity>>

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY completedAtMillis DESC")
    fun observeCompletionsForHabit(habitId: String): Flow<List<HabitCompletionEntity>>

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId AND dayStartMillis = :dayStartMillis LIMIT 1")
    suspend fun getCompletionForDay(habitId: String, dayStartMillis: Long): HabitCompletionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(completion: HabitCompletionEntity)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND dayStartMillis = :dayStartMillis")
    suspend fun deleteForDay(habitId: String, dayStartMillis: Long)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId")
    suspend fun deleteByHabitId(habitId: String)

    @Query("DELETE FROM habit_completions")
    suspend fun deleteAll()
}

package com.bloom.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.bloom.app.data.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY createdAtMillis DESC")
    fun observeHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :habitId LIMIT 1")
    fun observeHabit(habitId: String): Flow<HabitEntity?>

    @Query("SELECT * FROM habits ORDER BY createdAtMillis DESC")
    suspend fun getHabits(): List<HabitEntity>

    @Upsert
    suspend fun upsert(habit: HabitEntity)

    @Delete
    suspend fun delete(habit: HabitEntity)

    @Query("DELETE FROM habits")
    suspend fun deleteAll()
}

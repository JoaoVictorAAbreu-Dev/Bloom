package com.bloom.app.domain.repository

import com.bloom.app.domain.model.Habit
import com.bloom.app.domain.model.HabitCategory
import com.bloom.app.domain.model.HabitFrequency
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun observeHabits(): Flow<List<Habit>>
    fun observeHabit(habitId: String): Flow<Habit?>
    fun observeHabitsByCategory(category: HabitCategory?): Flow<List<Habit>>
    suspend fun upsertHabit(habit: Habit)
    suspend fun deleteHabit(habitId: String)
    suspend fun toggleHabitCompletion(habitId: String, completedAtMillis: Long = System.currentTimeMillis())
    suspend fun reset()
    suspend fun seedDefaults()
}

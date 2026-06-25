package com.bloom.app.data.repository

import com.bloom.app.data.dao.HabitCompletionDao
import com.bloom.app.data.dao.HabitDao
import com.bloom.app.data.local.nowDayStartMillis
import com.bloom.app.data.local.startOfDayMillis
import com.bloom.app.data.entity.HabitEntity
import com.bloom.app.data.mapper.toDomain
import com.bloom.app.data.mapper.toEntity
import com.bloom.app.domain.model.BloomStatistics
import com.bloom.app.domain.model.Habit
import com.bloom.app.domain.model.HabitCategory
import com.bloom.app.domain.model.HabitCompletion
import com.bloom.app.domain.model.HabitFrequency
import com.bloom.app.domain.repository.HabitRepository
import com.bloom.app.domain.usecase.CalculateHabitStreakUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.UUID

class HabitRepositoryImpl(
    private val habitDao: HabitDao,
    private val habitCompletionDao: HabitCompletionDao,
    private val calculateHabitStreakUseCase: CalculateHabitStreakUseCase,
) : HabitRepository {

    override fun observeHabits(): Flow<List<Habit>> {
        return combine(habitDao.observeHabits(), habitCompletionDao.observeCompletions()) { habits, completions ->
            habits.map { habit -> habit.withStats(completions) }
        }
    }

    override fun observeHabit(habitId: String): Flow<Habit?> {
        return combine(habitDao.observeHabit(habitId), habitCompletionDao.observeCompletionsForHabit(habitId)) { habit, completions ->
            habit?.withStats(completions)
        }
    }

    override fun observeHabitsByCategory(category: HabitCategory?): Flow<List<Habit>> {
        val allHabits = observeHabits()
        return if (category == null) {
            allHabits
        } else {
            allHabits.map { habits -> habits.filter { it.category == category } }
        }
    }

    override suspend fun upsertHabit(habit: Habit) {
        habitDao.upsert(habit.toEntity())
    }

    override suspend fun deleteHabit(habitId: String) {
        val habit = habitDao.getHabits().firstOrNull { it.id == habitId } ?: return
        habitCompletionDao.deleteByHabitId(habitId)
        habitDao.delete(habit)
    }

    override suspend fun toggleHabitCompletion(habitId: String, completedAtMillis: Long) {
        val dayStart = completedAtMillis.startOfDayMillis()
        val current = habitCompletionDao.getCompletionForDay(habitId, dayStart)
        if (current == null) {
            habitCompletionDao.insert(
                HabitCompletion(
                    id = UUID.randomUUID().toString(),
                    habitId = habitId,
                    completedAtMillis = completedAtMillis,
                    dayStartMillis = dayStart,
                ).toEntity(),
            )
        } else {
            habitCompletionDao.deleteForDay(habitId, dayStart)
        }
    }

    override suspend fun reset() {
        habitCompletionDao.deleteAll()
        habitDao.deleteAll()
    }

    override suspend fun seedDefaults() {
        if (habitDao.getHabits().isNotEmpty()) return

        val now = System.currentTimeMillis()
        val todayStart = nowDayStartMillis()
        val yesterday = todayStart - 24 * 60 * 60 * 1000L
        val twoDaysAgo = yesterday - 24 * 60 * 60 * 1000L

        val habits = listOf(
            Habit(
                id = UUID.randomUUID().toString(),
                name = "Water Plants",
                category = HabitCategory.HEALTH,
                frequency = HabitFrequency.DAILY,
                reminderHour = 9,
                reminderMinute = 0,
                colorArgb = 0xFFB4D2C0.toInt(),
                iconKey = "watering_can",
                createdAtMillis = now,
                streak = 0,
                completedToday = false,
                completionCount = 0,
            ),
            Habit(
                id = UUID.randomUUID().toString(),
                name = "Journal for 5 minutes",
                category = HabitCategory.MIND,
                frequency = HabitFrequency.DAILY,
                reminderHour = 20,
                reminderMinute = 30,
                colorArgb = 0xFFD6C8F5.toInt(),
                iconKey = "journal",
                createdAtMillis = now,
                streak = 0,
                completedToday = false,
                completionCount = 0,
            ),
            Habit(
                id = UUID.randomUUID().toString(),
                name = "Deep Work Block",
                category = HabitCategory.WORK,
                frequency = HabitFrequency.DAILY,
                reminderHour = 10,
                reminderMinute = 0,
                colorArgb = 0xFFC6E9E9.toInt(),
                iconKey = "focus",
                createdAtMillis = now,
                streak = 0,
                completedToday = false,
                completionCount = 0,
            ),
            Habit(
                id = UUID.randomUUID().toString(),
                name = "Morning Run",
                category = HabitCategory.HEALTH,
                frequency = HabitFrequency.WEEKLY,
                reminderHour = 7,
                reminderMinute = 0,
                colorArgb = 0xFFCFE0B1.toInt(),
                iconKey = "shoe",
                createdAtMillis = now,
                streak = 0,
                completedToday = false,
                completionCount = 0,
            ),
        )

        habits.forEach { habitDao.upsert(it.toEntity()) }

        val seededCompletions = listOf(
            HabitCompletion(
                id = UUID.randomUUID().toString(),
                habitId = habits[0].id,
                completedAtMillis = twoDaysAgo + 9 * 60 * 60 * 1000L,
                dayStartMillis = twoDaysAgo,
            ),
            HabitCompletion(
                id = UUID.randomUUID().toString(),
                habitId = habits[0].id,
                completedAtMillis = yesterday + 9 * 60 * 60 * 1000L,
                dayStartMillis = yesterday,
            ),
            HabitCompletion(
                id = UUID.randomUUID().toString(),
                habitId = habits[0].id,
                completedAtMillis = now,
                dayStartMillis = todayStart,
            ),
            HabitCompletion(
                id = UUID.randomUUID().toString(),
                habitId = habits[1].id,
                completedAtMillis = yesterday + 20 * 60 * 60 * 1000L,
                dayStartMillis = yesterday,
            ),
            HabitCompletion(
                id = UUID.randomUUID().toString(),
                habitId = habits[2].id,
                completedAtMillis = todayStart + 10 * 60 * 60 * 1000L,
                dayStartMillis = todayStart,
            ),
        )

        seededCompletions.forEach { habitCompletionDao.insert(it.toEntity()) }
    }

    private fun HabitEntity.withStats(completions: List<com.bloom.app.data.entity.HabitCompletionEntity>): Habit {
        val habitCompletions = completions.filter { it.habitId == id }
        val streak = calculateHabitStreakUseCase(habitCompletions.map { it.completedAtMillis })
        val completedToday = habitCompletions.any { it.dayStartMillis == nowDayStartMillis() }
        return toDomain(
            streak = streak,
            completedToday = completedToday,
            completionCount = habitCompletions.size,
        )
    }
}

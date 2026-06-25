package com.bloom.app.data.repository

import com.bloom.app.data.local.nowDayStartMillis
import com.bloom.app.data.local.startOfDayMillis
import com.bloom.app.data.dao.HabitCompletionDao
import com.bloom.app.domain.model.BloomStatistics
import com.bloom.app.domain.model.PomodoroMode
import com.bloom.app.domain.repository.HabitRepository
import com.bloom.app.domain.repository.PomodoroRepository
import com.bloom.app.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId

class StatisticsRepositoryImpl(
    private val habitRepository: HabitRepository,
    private val pomodoroRepository: PomodoroRepository,
    private val habitCompletionDao: HabitCompletionDao,
) : StatisticsRepository {
    override fun observeStatistics(): Flow<BloomStatistics> {
        return combine(
            habitRepository.observeHabits(),
            pomodoroRepository.observeSessions(),
            habitCompletionDao.observeCompletions(),
        ) { habits, sessions, completions ->
            val zoneId = ZoneId.systemDefault()
            val todayStart = nowDayStartMillis()
            val todayHabitCompletions = completions.count { it.dayStartMillis == todayStart }
            val focusMinutesToday = sessions
                .filter { it.completed && it.mode == PomodoroMode.FOCUS && it.finishedAtMillis.startOfDayMillis(zoneId) == todayStart }
                .sumOf { it.durationMinutes }
            val longestStreak = habits.maxOfOrNull { it.streak } ?: 0
            val weeklyFocusMinutes = lastSevenDays(zoneId).map { day ->
                sessions.filter { it.completed && it.mode == PomodoroMode.FOCUS && it.finishedAtMillis.startOfDayMillis(zoneId) == day.startOfDayMillis(zoneId) }
                    .sumOf { it.durationMinutes }
            }
            val weeklyHabitCompletions = lastSevenDays(zoneId).map { day ->
                completions.count { it.dayStartMillis == day.startOfDayMillis(zoneId) }
            }
            val weeklyConsistency = ((weeklyHabitCompletions.count { it > 0 } / 7f) * 100).toInt()
            val gardenGrowth = (habits.size * 8) + (longestStreak * 4) + (focusMinutesToday / 5) + todayHabitCompletions

            BloomStatistics(
                focusMinutesToday = focusMinutesToday,
                habitsDoneToday = todayHabitCompletions,
                totalHabits = habits.size,
                longestStreak = longestStreak,
                weeklyConsistency = weeklyConsistency,
                weeklyFocusMinutes = weeklyFocusMinutes,
                weeklyHabitCompletions = weeklyHabitCompletions,
                gardenGrowth = gardenGrowth.coerceAtLeast(0),
            )
        }
    }

    private fun lastSevenDays(zoneId: ZoneId): List<LocalDate> {
        val today = LocalDate.now(zoneId)
        return (6 downTo 0).map { daysAgo -> today.minusDays(daysAgo.toLong()) }
    }
}

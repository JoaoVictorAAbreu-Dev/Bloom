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
import java.time.Instant
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
            val last28Days = lastDays(days = 28, zoneId = zoneId)
            val monthlyHabitCompletions = last28Days.map { day ->
                completions.count { it.dayStartMillis == day.startOfDayMillis(zoneId) }
            }
            val monthlyFocusMinutes = last28Days.chunked(7).map { week ->
                val weekStarts = week.map { it.startOfDayMillis(zoneId) }.toSet()
                sessions
                    .filter { it.completed && it.mode == PomodoroMode.FOCUS && it.finishedAtMillis.startOfDayMillis(zoneId) in weekStarts }
                    .sumOf { it.durationMinutes }
            }
            val completedFocusSessions = sessions.filter { it.completed && it.mode == PomodoroMode.FOCUS }
            val averageFocusMinutes = completedFocusSessions
                .takeIf { it.isNotEmpty() }
                ?.map { it.durationMinutes }
                ?.average()
                ?.toInt()
                ?: 0
            val mostProductiveHourLabel = completedFocusSessions
                .groupBy { session -> Instant.ofEpochMilli(session.startedAtMillis).atZone(zoneId).hour }
                .maxByOrNull { entry -> entry.value.sumOf { it.durationMinutes } }
                ?.key
                ?.let { hour -> "${hour.toString().padStart(2, '0')}:00" }
                ?: "No focus yet"
            val topHabitName = completions
                .groupingBy { it.habitId }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key
                ?.let { habitId -> habits.firstOrNull { it.id == habitId }?.name }
                ?: "No habit yet"
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
                monthlyFocusMinutes = monthlyFocusMinutes,
                monthlyHabitCompletions = monthlyHabitCompletions,
                averageFocusMinutes = averageFocusMinutes,
                mostProductiveHourLabel = mostProductiveHourLabel,
                topHabitName = topHabitName,
                gardenGrowth = gardenGrowth.coerceAtLeast(0),
            )
        }
    }

    private fun lastSevenDays(zoneId: ZoneId): List<LocalDate> {
        val today = LocalDate.now(zoneId)
        return (6 downTo 0).map { daysAgo -> today.minusDays(daysAgo.toLong()) }
    }

    private fun lastDays(days: Int, zoneId: ZoneId): List<LocalDate> {
        val today = LocalDate.now(zoneId)
        return ((days - 1) downTo 0).map { daysAgo -> today.minusDays(daysAgo.toLong()) }
    }
}

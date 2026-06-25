package com.bloom.app.domain.usecase

import java.time.LocalDate
import java.time.ZoneId

class CalculateHabitStreakUseCase {
    operator fun invoke(completionDays: List<Long>): Int {
        if (completionDays.isEmpty()) return 0

        val zone = ZoneId.systemDefault()
        val distinctDays = completionDays
            .map { LocalDate.ofInstant(java.time.Instant.ofEpochMilli(it), zone) }
            .distinct()
            .sortedDescending()

        var streak = 0
        var cursor = LocalDate.now(zone)
        for (day in distinctDays) {
            if (day == cursor) {
                streak++
                cursor = cursor.minusDays(1)
            } else if (day.isBefore(cursor)) {
                break
            }
        }
        return streak
    }
}

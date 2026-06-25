package com.bloom.app.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class CalculateHabitStreakUseCaseTest {
    private val useCase = CalculateHabitStreakUseCase()

    @Test
    fun `returns current consecutive streak`() {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val completions = listOf(
            today.minusDays(2).atStartOfDay(zone).toInstant().toEpochMilli(),
            today.minusDays(1).atStartOfDay(zone).toInstant().toEpochMilli(),
            today.atStartOfDay(zone).toInstant().toEpochMilli(),
        )

        assertEquals(3, useCase(completions))
    }

    @Test
    fun `returns zero when streak is broken today`() {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val completions = listOf(
            today.minusDays(3).atStartOfDay(zone).toInstant().toEpochMilli(),
            today.minusDays(1).atStartOfDay(zone).toInstant().toEpochMilli(),
        )

        assertEquals(0, useCase(completions))
    }
}

package com.bloom.app.domain.model

data class AiCoachContext(
    val userName: String,
    val preferences: UserPreferences,
    val habits: List<Habit>,
    val routineBlocks: List<RoutineBlock>,
    val statistics: BloomStatistics,
    val rewardsUnlocked: Int,
    val recentSessions: List<PomodoroSession>,
)


package com.bloom.app.data.repository

import com.bloom.app.domain.model.BloomStatistics
import com.bloom.app.domain.model.Reward
import com.bloom.app.domain.repository.RewardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class RewardRepositoryImpl : RewardRepository {
    override fun observeRewards(statistics: Flow<BloomStatistics>): Flow<List<Reward>> {
        return statistics.map { stats ->
            listOf(
                Reward(
                    id = "sprout",
                    title = "Sprout Badge",
                    description = "Complete 5 tasks in your garden.",
                    threshold = 5,
                    category = "Habits",
                    unlocked = stats.gardenGrowth >= 5,
                    accentColorArgb = 0xFFC6E9E9.toInt(),
                    iconKey = "sprout",
                ),
                Reward(
                    id = "bloom",
                    title = "Bloom Badge",
                    description = "Reach 15 growth points.",
                    threshold = 15,
                    category = "Growth",
                    unlocked = stats.gardenGrowth >= 15,
                    accentColorArgb = 0xFFAE98D6.toInt(),
                    iconKey = "flower",
                ),
                Reward(
                    id = "forest",
                    title = "Forest Badge",
                    description = "Complete 45 minutes of focus.",
                    threshold = 45,
                    category = "Focus",
                    unlocked = stats.focusMinutesToday >= 45 || stats.gardenGrowth >= 45,
                    accentColorArgb = 0xFF8DAA91.toInt(),
                    iconKey = "tree",
                ),
                Reward(
                    id = "guardian",
                    title = "Garden Guardian",
                    description = "Build a 7 day streak.",
                    threshold = 7,
                    category = "Streak",
                    unlocked = stats.longestStreak >= 7,
                    accentColorArgb = 0xFFD9A441.toInt(),
                    iconKey = "mascot",
                ),
            )
        }
    }
}

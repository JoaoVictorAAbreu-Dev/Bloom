package com.bloom.app.domain.repository

import com.bloom.app.domain.model.Reward
import com.bloom.app.domain.model.BloomStatistics
import kotlinx.coroutines.flow.Flow

interface RewardRepository {
    fun observeRewards(statistics: Flow<BloomStatistics>): Flow<List<Reward>>
}

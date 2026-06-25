package com.bloom.app.domain.usecase

import com.bloom.app.domain.repository.RewardRepository

class ObserveRewardsUseCase(private val rewardRepository: RewardRepository) {
    operator fun invoke(statistics: kotlinx.coroutines.flow.Flow<com.bloom.app.domain.model.BloomStatistics>) =
        rewardRepository.observeRewards(statistics)
}

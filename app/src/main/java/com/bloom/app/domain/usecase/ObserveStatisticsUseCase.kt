package com.bloom.app.domain.usecase

import com.bloom.app.domain.repository.StatisticsRepository

class ObserveStatisticsUseCase(private val statisticsRepository: StatisticsRepository) {
    operator fun invoke() = statisticsRepository.observeStatistics()
}

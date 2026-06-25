package com.bloom.app.domain.repository

import com.bloom.app.domain.model.BloomStatistics
import kotlinx.coroutines.flow.Flow

interface StatisticsRepository {
    fun observeStatistics(): Flow<BloomStatistics>
}

package com.bloom.app.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloom.app.BloomAppContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class GardenViewModel(
    private val container: BloomAppContainer,
) : ViewModel() {
    private val statisticsFlow = container.observeStatisticsUseCase()

    val uiState = combine(
        statisticsFlow,
        container.observeRewardsUseCase(statisticsFlow),
    ) { statistics, rewards ->
        GardenUiState(
            rewards = rewards,
            statistics = statistics,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GardenUiState())
}

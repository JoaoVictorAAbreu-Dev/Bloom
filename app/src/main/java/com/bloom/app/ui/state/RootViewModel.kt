package com.bloom.app.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloom.app.BloomAppContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RootViewModel(
    private val container: BloomAppContainer,
) : ViewModel() {
    val uiState = container.observePreferencesUseCase()
        .map { RootUiState(preferences = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RootUiState())

    init {
        viewModelScope.launch {
            val preferences = container.preferencesRepository.preferences.first()
            if (!preferences.seedDataCreated) {
                container.seedDemoContentUseCase()
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            container.completeOnboardingUseCase()
        }
    }

    fun completeAuth(userName: String, email: String = "") {
        viewModelScope.launch {
            container.updatePreferencesUseCase { current ->
                current.copy(
                    userName = userName.ifBlank { current.userName },
                    userEmail = email,
                    authCompleted = true,
                )
            }
        }
    }

    fun updateThemeMode(mode: com.bloom.app.domain.model.ThemeMode) {
        viewModelScope.launch {
            container.updateThemeModeUseCase(mode)
        }
    }
}

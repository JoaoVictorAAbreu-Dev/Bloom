package com.bloom.app.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloom.app.BloomAppContainer
import com.bloom.app.domain.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val container: BloomAppContainer,
) : ViewModel() {
    private val resetInProgress = MutableStateFlow(false)

    val uiState = combine(
        container.observePreferencesUseCase(),
        resetInProgress,
    ) { preferences, resetting ->
        SettingsUiState(
            preferences = preferences,
            resetInProgress = resetting,
            aiIntegration = CoachIntegrationUiState(
                configured = container.aiCoachRepository.isConfigured,
                modelId = container.aiCoachRepository.modelId,
                baseUrl = container.aiCoachRepository.baseUrl,
            ),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun updateName(name: String) {
        viewModelScope.launch {
            container.updatePreferencesUseCase { current ->
                current.copy(userName = name.takeIf { it.isNotBlank() } ?: current.userName)
            }
        }
    }

    fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            container.updateThemeModeUseCase(mode)
        }
    }

    fun updateFocusMinutes(value: Int) {
        viewModelScope.launch {
            container.updatePreferencesUseCase { current -> current.copy(focusMinutes = value.coerceIn(15, 60)) }
        }
    }

    fun updateShortBreakMinutes(value: Int) {
        viewModelScope.launch {
            container.updatePreferencesUseCase { current -> current.copy(shortBreakMinutes = value.coerceIn(3, 15)) }
        }
    }

    fun updateLongBreakMinutes(value: Int) {
        viewModelScope.launch {
            container.updatePreferencesUseCase { current -> current.copy(longBreakMinutes = value.coerceIn(10, 30)) }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            container.updatePreferencesUseCase { current -> current.copy(notificationsEnabled = enabled) }
        }
    }

    fun toggleAutoStart(enabled: Boolean) {
        viewModelScope.launch {
            container.updatePreferencesUseCase { current -> current.copy(autoStartNextSession = enabled) }
        }
    }

    fun resetData() {
        viewModelScope.launch {
            resetInProgress.update { true }
            container.resetAllDataUseCase()
            resetInProgress.update { false }
        }
    }
}

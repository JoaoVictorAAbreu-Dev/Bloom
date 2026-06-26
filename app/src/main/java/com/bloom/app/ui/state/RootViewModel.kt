package com.bloom.app.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloom.app.BloomAppContainer
import com.bloom.app.domain.model.Habit
import com.bloom.app.domain.model.HabitCategory
import com.bloom.app.domain.model.HabitFrequency
import com.bloom.app.domain.model.OnboardingSetup
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class RootViewModel(
    private val container: BloomAppContainer,
) : ViewModel() {
    val uiState = container.observePreferencesUseCase()
        .map { RootUiState(preferences = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RootUiState())

    init {
        viewModelScope.launch {
            container.habitRepository.ensureLocalFieldsEncrypted()
            container.preferencesRepository.ensureSensitiveFieldsEncrypted()
            val preferences = container.preferencesRepository.preferences.first()
            if (preferences.onboardingCompleted && !preferences.seedDataCreated) {
                container.seedDemoContentUseCase()
            }
        }
    }

    fun completeOnboarding(setup: OnboardingSetup) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            setup.starterHabits
                .ifEmpty { listOf("Drink water") }
                .distinct()
                .forEachIndexed { index, label ->
                    container.upsertHabitUseCase(label.toStarterHabit(index, now))
                }
            container.updatePreferencesUseCase { current ->
                current.copy(
                    primaryGoal = setup.primaryGoal,
                    focusMinutes = setup.focusMinutes,
                    shortBreakMinutes = setup.shortBreakMinutes,
                    notificationsEnabled = setup.notificationsEnabled,
                    onboardingCompleted = true,
                    seedDataCreated = true,
                )
            }
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

    private fun String.toStarterHabit(index: Int, createdAtMillis: Long): Habit {
        val template = starterHabitTemplates[this] ?: StarterHabitTemplate(
            category = HabitCategory.MIND,
            iconKey = "leaf",
            colorArgb = 0xFFCFE0B1.toInt(),
            reminderHour = 8 + index,
            dailyGoal = 1,
            weeklyGoal = 5,
        )
        return Habit(
            id = UUID.randomUUID().toString(),
            name = this,
            category = template.category,
            frequency = HabitFrequency.DAILY,
            reminderHour = template.reminderHour,
            reminderMinute = 0,
            colorArgb = template.colorArgb,
            iconKey = template.iconKey,
            priority = if (index == 0) "High" else "Medium",
            dailyGoal = template.dailyGoal,
            weeklyGoal = template.weeklyGoal,
            customRepeat = "Daily",
            createdAtMillis = createdAtMillis,
        )
    }

    private data class StarterHabitTemplate(
        val category: HabitCategory,
        val iconKey: String,
        val colorArgb: Int,
        val reminderHour: Int,
        val dailyGoal: Int,
        val weeklyGoal: Int,
    )

    private companion object {
        val starterHabitTemplates = mapOf(
            "Drink water" to StarterHabitTemplate(HabitCategory.HEALTH, "watering_can", 0xFFB4D2C0.toInt(), 9, 1, 7),
            "Read 10 pages" to StarterHabitTemplate(HabitCategory.MIND, "journal", 0xFFD6C8F5.toInt(), 21, 1, 5),
            "Meditate" to StarterHabitTemplate(HabitCategory.MIND, "leaf", 0xFFCFE0B1.toInt(), 8, 1, 5),
            "Exercise" to StarterHabitTemplate(HabitCategory.HEALTH, "shoe", 0xFFD9A441.toInt(), 7, 1, 4),
            "Plan tomorrow" to StarterHabitTemplate(HabitCategory.WORK, "journal", 0xFFC6E9E9.toInt(), 20, 1, 5),
            "Study" to StarterHabitTemplate(HabitCategory.WORK, "focus", 0xFFAE98D6.toInt(), 18, 1, 5),
        )
    }
}

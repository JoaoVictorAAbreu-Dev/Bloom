package com.bloom.app.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloom.app.BloomAppContainer
import com.bloom.app.domain.model.BloomStatistics
import com.bloom.app.domain.model.Habit
import com.bloom.app.domain.model.HabitCategory
import com.bloom.app.domain.model.HabitFrequency
import com.bloom.app.domain.model.PomodoroSession
import com.bloom.app.domain.model.PomodoroMode
import com.bloom.app.domain.model.Reward
import com.bloom.app.domain.model.ThemeMode
import com.bloom.app.domain.model.UserPreferences
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val container: BloomAppContainer,
) : ViewModel() {
    private val resetInProgress = MutableStateFlow(false)
    private val exportSnapshot = MutableStateFlow("")
    private val importInProgress = MutableStateFlow(false)
    private val statistics = container.observeStatisticsUseCase()

    val uiState = combine(
        container.observePreferencesUseCase(),
        statistics,
        container.observeRewardsUseCase(statistics),
        resetInProgress,
        importInProgress,
        exportSnapshot,
    ) { values ->
        val preferences = values[0] as UserPreferences
        val statisticsValue = values[1] as BloomStatistics
        val rewards = values[2] as List<Reward>
        val resetting = values[3] as Boolean
        val importing = values[4] as Boolean
        val snapshot = values[5] as String
        SettingsUiState(
            preferences = preferences,
            statistics = statisticsValue,
            rewards = rewards,
            resetInProgress = resetting,
            importInProgress = importing,
            exportSnapshot = snapshot,
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

    fun toggleBloomCoach(enabled: Boolean) {
        viewModelScope.launch {
            container.updatePreferencesUseCase { current ->
                current.copy(
                    bloomCoachEnabled = enabled,
                    allowHabitContextForAi = if (enabled) current.allowHabitContextForAi else false,
                )
            }
        }
    }

    fun toggleHabitContextForAi(enabled: Boolean) {
        viewModelScope.launch {
            container.updatePreferencesUseCase { current ->
                current.copy(allowHabitContextForAi = enabled && current.bloomCoachEnabled)
            }
        }
    }

    fun exportData() {
        viewModelScope.launch {
            val preferences = container.observePreferencesUseCase().first()
            val habits = container.observeHabitsUseCase().first()
            val sessions = container.observePomodoroSessionsUseCase().first()
            val currentStatistics = container.observeStatisticsUseCase().first()
            val rewards = container.observeRewardsUseCase(statistics).first()
            exportSnapshot.value = buildExportSnapshot(
                preferences = preferences,
                habits = habits,
                sessions = sessions,
                statistics = currentStatistics,
                rewards = rewards,
            )
        }
    }

    fun importData(snapshot: String) {
        if (snapshot.isBlank()) return
        viewModelScope.launch {
            importInProgress.update { true }
            try {
                require(snapshot.length <= MAX_IMPORT_BYTES) { "Import file is too large" }
                val payload = parseImportPayload(JSONObject(snapshot))

                container.resetAllDataUseCase()

                payload.preferences?.let { importedPreferences ->
                    container.updatePreferencesUseCase { current ->
                        current.copy(
                            userName = importedPreferences.userName,
                            userEmail = importedPreferences.userEmail,
                            primaryGoal = importedPreferences.primaryGoal,
                            themeMode = importedPreferences.themeMode,
                            focusMinutes = importedPreferences.focusMinutes,
                            shortBreakMinutes = importedPreferences.shortBreakMinutes,
                            longBreakMinutes = importedPreferences.longBreakMinutes,
                            autoStartNextSession = importedPreferences.autoStartNextSession,
                            notificationsEnabled = importedPreferences.notificationsEnabled,
                            bloomCoachEnabled = importedPreferences.bloomCoachEnabled,
                            allowHabitContextForAi = importedPreferences.allowHabitContextForAi,
                            onboardingCompleted = importedPreferences.onboardingCompleted,
                            authCompleted = importedPreferences.authCompleted,
                            seedDataCreated = importedPreferences.seedDataCreated,
                        )
                    }
                }

                payload.habits.forEach { habit ->
                    container.habitRepository.upsertHabit(habit)
                }

                payload.sessions.forEach { session ->
                    container.pomodoroRepository.saveSession(session)
                }
                exportSnapshot.value = ""
            } catch (_: IllegalArgumentException) {
                // Invalid imports are ignored so existing local data is preserved.
            } catch (_: JSONException) {
                // Invalid imports are ignored so existing local data is preserved.
            } finally {
                importInProgress.update { false }
            }
        }
    }

    fun clearExport() {
        exportSnapshot.value = ""
    }

    fun resetData() {
        viewModelScope.launch {
            resetInProgress.update { true }
            container.resetAllDataUseCase()
            resetInProgress.update { false }
        }
    }

    private fun buildExportSnapshot(
        preferences: UserPreferences,
        habits: List<Habit>,
        sessions: List<PomodoroSession>,
        statistics: BloomStatistics,
        rewards: List<Reward>,
    ): String {
        return buildString {
            appendLine("{")
            appendLine("  \"schemaVersion\": $EXPORT_SCHEMA_VERSION,")
            appendLine("  \"user\": {")
            appendLine("    \"name\": \"${preferences.userName.jsonEscaped()}\",")
            appendLine("    \"email\": \"${preferences.userEmail.jsonEscaped()}\",")
            appendLine("    \"primaryGoal\": \"${preferences.primaryGoal.jsonEscaped()}\",")
            appendLine("    \"themeMode\": \"${preferences.themeMode.name}\",")
            appendLine("    \"focusMinutes\": ${preferences.focusMinutes},")
            appendLine("    \"shortBreakMinutes\": ${preferences.shortBreakMinutes},")
            appendLine("    \"longBreakMinutes\": ${preferences.longBreakMinutes},")
            appendLine("    \"autoStartNextSession\": ${preferences.autoStartNextSession},")
            appendLine("    \"notificationsEnabled\": ${preferences.notificationsEnabled},")
            appendLine("    \"bloomCoachEnabled\": ${preferences.bloomCoachEnabled},")
            appendLine("    \"allowHabitContextForAi\": ${preferences.allowHabitContextForAi},")
            appendLine("    \"onboardingCompleted\": ${preferences.onboardingCompleted},")
            appendLine("    \"authCompleted\": ${preferences.authCompleted},")
            appendLine("    \"seedDataCreated\": ${preferences.seedDataCreated}")
            appendLine("  },")
            appendLine("  \"statistics\": {")
            appendLine("    \"focusMinutesToday\": ${statistics.focusMinutesToday},")
            appendLine("    \"habitsDoneToday\": ${statistics.habitsDoneToday},")
            appendLine("    \"totalHabits\": ${statistics.totalHabits},")
            appendLine("    \"longestStreak\": ${statistics.longestStreak},")
            appendLine("    \"weeklyConsistency\": ${statistics.weeklyConsistency},")
            appendLine("    \"gardenGrowth\": ${statistics.gardenGrowth}")
            appendLine("  },")
            appendLine("  \"habits\": [")
            habits.forEachIndexed { index, habit ->
                appendLine("    {")
                appendLine("      \"id\": \"${habit.id.jsonEscaped()}\",")
                appendLine("      \"name\": \"${habit.name.jsonEscaped()}\",")
                appendLine("      \"category\": \"${habit.category.label.jsonEscaped()}\",")
                appendLine("      \"frequency\": \"${habit.frequency.label.jsonEscaped()}\",")
                appendLine("      \"reminderHour\": ${habit.reminderHour ?: -1},")
                appendLine("      \"reminderMinute\": ${habit.reminderMinute ?: -1},")
                appendLine("      \"colorArgb\": ${habit.colorArgb},")
                appendLine("      \"iconKey\": \"${habit.iconKey.jsonEscaped()}\",")
                appendLine("      \"priority\": \"${habit.priority.jsonEscaped()}\",")
                appendLine("      \"emoji\": \"${habit.emoji.jsonEscaped()}\",")
                appendLine("      \"dailyGoal\": ${habit.dailyGoal},")
                appendLine("      \"weeklyGoal\": ${habit.weeklyGoal},")
                appendLine("      \"customRepeat\": \"${habit.customRepeat.jsonEscaped()}\",")
                appendLine("      \"createdAtMillis\": ${habit.createdAtMillis},")
                appendLine("      \"streak\": ${habit.streak},")
                appendLine("      \"completedToday\": ${habit.completedToday},")
                appendLine("      \"completionCount\": ${habit.completionCount}")
                append("    }")
                appendLine(if (index < habits.lastIndex) "," else "")
            }
            appendLine("  ],")
            appendLine("  \"focusSessions\": [")
            sessions.forEachIndexed { index, session ->
                appendLine("    {")
                appendLine("      \"id\": \"${session.id.jsonEscaped()}\",")
                appendLine("      \"mode\": \"${session.mode.name}\",")
                appendLine("      \"durationMinutes\": ${session.durationMinutes},")
                appendLine("      \"startedAtMillis\": ${session.startedAtMillis},")
                appendLine("      \"finishedAtMillis\": ${session.finishedAtMillis},")
                appendLine("      \"completed\": ${session.completed}")
                append("    }")
                appendLine(if (index < sessions.lastIndex) "," else "")
            }
            appendLine("  ],")
            appendLine("  \"unlockedRewards\": ${rewards.count { it.unlocked }}")
            appendLine("}")
        }
    }

    private fun parseImportPayload(payload: JSONObject): ImportPayload {
        val version = payload.optInt("schemaVersion", EXPORT_SCHEMA_VERSION)
        require(version in 1..EXPORT_SCHEMA_VERSION) { "Unsupported Bloom export version" }

        val preferences = payload.optJSONObject("user")?.let(::parsePreferences)
        val habitsArray = payload.optJSONArray("habits") ?: JSONArray()
        val sessionsArray = payload.optJSONArray("focusSessions") ?: JSONArray()
        require(habitsArray.length() <= MAX_IMPORT_HABITS) { "Too many habits" }
        require(sessionsArray.length() <= MAX_IMPORT_SESSIONS) { "Too many focus sessions" }

        return ImportPayload(
            preferences = preferences,
            habits = parseHabits(habitsArray),
            sessions = parseSessions(sessionsArray),
        )
    }

    private fun parsePreferences(user: JSONObject): UserPreferences {
        return UserPreferences(
            userName = user.optBoundedString("name", "Alex", MAX_SHORT_TEXT),
            userEmail = user.optBoundedString("email", "", MAX_SHORT_TEXT),
            primaryGoal = user.optBoundedString("primaryGoal", "Build consistency", MAX_SHORT_TEXT),
            themeMode = ThemeMode.entries.firstOrNull { it.name == user.optString("themeMode", ThemeMode.SYSTEM.name) } ?: ThemeMode.SYSTEM,
            focusMinutes = user.optInt("focusMinutes", 25).coerceIn(15, 60),
            shortBreakMinutes = user.optInt("shortBreakMinutes", 5).coerceIn(3, 15),
            longBreakMinutes = user.optInt("longBreakMinutes", 15).coerceIn(10, 30),
            autoStartNextSession = user.optBoolean("autoStartNextSession", true),
            notificationsEnabled = user.optBoolean("notificationsEnabled", true),
            bloomCoachEnabled = user.optBoolean("bloomCoachEnabled", false),
            allowHabitContextForAi = user.optBoolean("allowHabitContextForAi", false),
            onboardingCompleted = user.optBoolean("onboardingCompleted", false),
            authCompleted = user.optBoolean("authCompleted", false),
            seedDataCreated = user.optBoolean("seedDataCreated", false),
        )
    }

    private fun parseHabits(array: JSONArray): List<Habit> {
        return buildList {
            for (index in 0 until array.length()) {
                val item = array.optJSONObject(index) ?: continue
                add(
                    Habit(
                        id = item.optBoundedString("id", "imported-habit-$index", MAX_ID_TEXT).ifBlank { "imported-habit-$index" },
                        name = item.optBoundedString("name", "Imported habit", MAX_LONG_TEXT).ifBlank { "Imported habit" },
                        category = HabitCategory.entries.firstOrNull { it.label == item.optString("category") } ?: HabitCategory.HEALTH,
                        frequency = HabitFrequency.entries.firstOrNull { it.label == item.optString("frequency") } ?: HabitFrequency.DAILY,
                        reminderHour = item.optInt("reminderHour", -1).takeIf { it in 0..23 },
                        reminderMinute = item.optInt("reminderMinute", -1).takeIf { it in 0..59 },
                        colorArgb = item.optInt("colorArgb", 0xFF8DAA91.toInt()),
                        iconKey = item.optBoundedString("iconKey", "watering_can", MAX_SHORT_TEXT),
                        priority = item.optBoundedString("priority", "Medium", MAX_SHORT_TEXT),
                        emoji = item.optBoundedString("emoji", "", MAX_SHORT_TEXT),
                        dailyGoal = item.optInt("dailyGoal", 1).coerceIn(1, 99),
                        weeklyGoal = item.optInt("weeklyGoal", 5).coerceIn(1, 99),
                        customRepeat = item.optBoundedString("customRepeat", "", MAX_LONG_TEXT),
                        createdAtMillis = item.optLong("createdAtMillis", System.currentTimeMillis()),
                        streak = item.optInt("streak", 0).coerceAtLeast(0),
                        completedToday = item.optBoolean("completedToday", false),
                        completionCount = item.optInt("completionCount", 0).coerceAtLeast(0),
                    )
                )
            }
        }
    }

    private fun parseSessions(array: JSONArray): List<PomodoroSession> {
        return buildList {
            for (index in 0 until array.length()) {
                val item = array.optJSONObject(index) ?: continue
                add(
                    PomodoroSession(
                        id = item.optBoundedString("id", "imported-session-$index", MAX_ID_TEXT).ifBlank { "imported-session-$index" },
                        mode = PomodoroMode.entries.firstOrNull { it.name == item.optString("mode") } ?: PomodoroMode.FOCUS,
                        durationMinutes = item.optInt("durationMinutes", 25).coerceIn(1, 240),
                        startedAtMillis = item.optLong("startedAtMillis", System.currentTimeMillis()),
                        finishedAtMillis = item.optLong("finishedAtMillis", System.currentTimeMillis()),
                        completed = item.optBoolean("completed", false),
                    )
                )
            }
        }
    }

    private fun String.jsonEscaped(): String {
        return replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
    }

    private fun JSONObject.optBoundedString(key: String, defaultValue: String, maxLength: Int): String {
        return optString(key, defaultValue).take(maxLength)
    }

    private data class ImportPayload(
        val preferences: UserPreferences?,
        val habits: List<Habit>,
        val sessions: List<PomodoroSession>,
    )

    private companion object {
        const val EXPORT_SCHEMA_VERSION = 1
        const val MAX_IMPORT_BYTES = 512 * 1024
        const val MAX_IMPORT_HABITS = 500
        const val MAX_IMPORT_SESSIONS = 2_000
        const val MAX_ID_TEXT = 80
        const val MAX_SHORT_TEXT = 120
        const val MAX_LONG_TEXT = 240
    }
}

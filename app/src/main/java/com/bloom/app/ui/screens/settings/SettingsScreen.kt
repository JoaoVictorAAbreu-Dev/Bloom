package com.bloom.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bloom.app.domain.model.ThemeMode
import com.bloom.app.ui.components.BloomAiBadge
import com.bloom.app.ui.components.BloomButton
import com.bloom.app.ui.components.BloomCard
import com.bloom.app.ui.components.BloomHeader
import com.bloom.app.ui.components.BloomOutlinedButton
import com.bloom.app.ui.state.SettingsUiState
import com.bloom.app.ui.theme.BloomRadius
import com.bloom.app.ui.theme.BloomSpacing

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onNameChange: (String) -> Unit,
    onThemeChange: (ThemeMode) -> Unit,
    onFocusMinutesChange: (Int) -> Unit,
    onShortBreakMinutesChange: (Int) -> Unit,
    onLongBreakMinutesChange: (Int) -> Unit,
    onNotificationsToggle: (Boolean) -> Unit,
    onAutoStartToggle: (Boolean) -> Unit,
    onBloomCoachToggle: (Boolean) -> Unit,
    onHabitContextForAiToggle: (Boolean) -> Unit,
    onExportData: () -> Unit,
    onClearExport: () -> Unit,
    onResetData: () -> Unit,
    onOpenCoach: () -> Unit,
    onNotificationsClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(vertical = BloomSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(BloomSpacing.cardGap),
    ) {
        BloomHeader(
            title = "Bloom",
            subtitle = "Settings",
            onNotificationsClick = onNotificationsClick,
        )

        Column(
            modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(BloomSpacing.xs),
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "Tune your calm productivity system.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        SettingsSection(title = "General", subtitle = "Profile, language, theme, and appearance.") {
            OutlinedTextField(
                value = uiState.preferences.userName,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Display name") },
                singleLine = true,
            )
            ThemeSelector(
                selected = uiState.preferences.themeMode,
                onSelected = onThemeChange,
            )
            StaticSettingRow(
                title = "Primary goal",
                description = "Chosen during onboarding.",
                value = uiState.preferences.primaryGoal,
            )
            StaticSettingRow(
                title = "Language",
                description = "English now. Localization-ready.",
                value = "System",
            )
            StaticSettingRow(
                title = "Appearance",
                description = "Organic Productivity visual system.",
                value = "Bloom",
            )
        }

        SettingsSection(title = "Pomodoro", subtitle = "Focus timing, breaks, and session behavior.") {
            DurationSlider(
                label = "Focus",
                value = uiState.preferences.focusMinutes,
                valueRange = 15f..60f,
                onValueChange = onFocusMinutesChange,
            )
            DurationSlider(
                label = "Short break",
                value = uiState.preferences.shortBreakMinutes,
                valueRange = 3f..15f,
                onValueChange = onShortBreakMinutesChange,
            )
            DurationSlider(
                label = "Long break",
                value = uiState.preferences.longBreakMinutes,
                valueRange = 10f..30f,
                onValueChange = onLongBreakMinutesChange,
            )
            SettingRow(
                title = "Auto-start next session",
                description = "Move between focus and break automatically.",
                checked = uiState.preferences.autoStartNextSession,
                onCheckedChange = onAutoStartToggle,
            )
            StaticSettingRow(
                title = "Focus sounds",
                description = "Ambient sounds can be added in the next media pass.",
                value = "Planned",
            )
        }

        SettingsSection(title = "Habits", subtitle = "Reminders, weekly rules, and completion behavior.") {
            SettingRow(
                title = "Notifications",
                description = "Gentle reminders and focus alerts.",
                checked = uiState.preferences.notificationsEnabled,
                onCheckedChange = onNotificationsToggle,
            )
            StaticSettingRow(
                title = "Week starts on",
                description = "Used by statistics and heatmaps.",
                value = "Monday",
            )
            StaticSettingRow(
                title = "Completion feedback",
                description = "Animation, haptics, and garden growth hooks.",
                value = "Ready",
            )
        }

        SettingsSection(title = "AI", subtitle = "Bloom Coach configuration and history.") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(BloomSpacing.md),
            ) {
                BloomAiBadge()
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Bloom Coach", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = if (!uiState.preferences.bloomCoachEnabled) {
                            "Disabled by privacy settings. Enable it below to use AI suggestions."
                        } else if (uiState.aiIntegration.configured) {
                            "Groq ready with ${uiState.aiIntegration.modelId}."
                        } else {
                            "Local fallback active. Add groqApiKey in local.properties to connect Groq."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                BloomButton(text = "Open", onClick = onOpenCoach)
            }
            StaticSettingRow(
                title = "API",
                description = uiState.aiIntegration.baseUrl,
                value = when {
                    !uiState.preferences.bloomCoachEnabled -> "Disabled"
                    uiState.aiIntegration.configured -> "Online"
                    else -> "Local"
                },
            )
            StaticSettingRow(
                title = "History",
                description = "Weekly and monthly summaries will use this space.",
                value = "Planned",
            )
        }

        SettingsSection(title = "Privacy and AI", subtitle = "Control what can leave the device.") {
            Text(
                text = "Bloom works offline. If enabled, Bloom Coach may send a minimized and sanitized prompt to the configured AI provider. Habit context is never sent unless you allow it.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            SettingRow(
                title = "Enable Bloom Coach",
                description = "Allow AI replies when Groq is configured. Local guidance still works without a key.",
                checked = uiState.preferences.bloomCoachEnabled,
                onCheckedChange = onBloomCoachToggle,
            )
            SettingRow(
                title = "Allow habit context",
                description = "Share sanitized habit names and routine labels for better suggestions.",
                checked = uiState.preferences.allowHabitContextForAi,
                enabled = uiState.preferences.bloomCoachEnabled,
                onCheckedChange = onHabitContextForAiToggle,
            )
        }

        SettingsSection(title = "Account", subtitle = "Local data, backup, export, and restore.") {
            StaticSettingRow(
                title = "Backup",
                description = "Cloud backup is reserved for the sync phase.",
                value = "Offline",
            )
            StaticSettingRow(
                title = "Export",
                description = "Generate a local JSON snapshot from current data.",
                value = if (uiState.exportSnapshot.isBlank()) "Ready" else "Generated",
            )
            Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.sm)) {
                BloomButton(
                    modifier = Modifier.weight(1f),
                    text = "Export JSON",
                    onClick = onExportData,
                )
                BloomOutlinedButton(
                    modifier = Modifier.weight(1f),
                    text = "Clear",
                    enabled = uiState.exportSnapshot.isNotBlank(),
                    onClick = onClearExport,
                )
            }
            if (uiState.exportSnapshot.isNotBlank()) {
                BloomCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(BloomSpacing.sm),
                ) {
                    Text(
                        text = uiState.exportSnapshot,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            StaticSettingRow(
                title = "Restore",
                description = "Import from JSON will be added after file-picker validation.",
                value = "Next",
            )
            BloomOutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                text = if (uiState.resetInProgress) "Resetting..." else "Reset data",
                enabled = !uiState.resetInProgress,
                onClick = onResetData,
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
        Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
            Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.xxs)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            content()
        }
    }
}

@Composable
private fun ThemeSelector(
    selected: ThemeMode,
    onSelected: (ThemeMode) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.xs)) {
        ThemeMode.values().forEach { mode ->
            AssistChip(
                onClick = { onSelected(mode) },
                label = { Text(mode.name.lowercase().replaceFirstChar(Char::uppercaseChar)) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (selected == mode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = if (selected == mode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}

@Composable
private fun DurationSlider(
    label: String,
    value: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.xs)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "$value min",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = valueRange,
        )
    }
}

@Composable
private fun SettingRow(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(checked = checked, enabled = enabled, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun StaticSettingRow(
    title: String,
    description: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Surface(
            shape = BloomRadius.pill,
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Text(
                text = value,
                modifier = Modifier.padding(horizontal = BloomSpacing.sm, vertical = BloomSpacing.xs),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

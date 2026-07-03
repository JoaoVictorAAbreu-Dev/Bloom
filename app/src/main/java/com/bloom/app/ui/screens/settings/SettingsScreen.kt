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
import androidx.compose.ui.res.stringResource
import com.bloom.app.R
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
    onSaveExport: (String) -> Unit,
    onShareExport: (String) -> Unit,
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
            subtitle = stringResource(R.string.settings_subtitle),
            onNotificationsClick = onNotificationsClick,
        )

        Column(
            modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(BloomSpacing.xs),
        ) {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = stringResource(R.string.settings_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        SettingsSection(title = stringResource(R.string.settings_general), subtitle = stringResource(R.string.settings_general_subtitle)) {
            OutlinedTextField(
                value = uiState.preferences.userName,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.settings_display_name)) },
                singleLine = true,
            )
            ThemeSelector(
                selected = uiState.preferences.themeMode,
                onSelected = onThemeChange,
            )
            StaticSettingRow(
                title = stringResource(R.string.settings_primary_goal),
                description = stringResource(R.string.settings_chosen_onboarding),
                value = uiState.preferences.primaryGoal,
            )
            StaticSettingRow(
                title = stringResource(R.string.settings_language),
                description = stringResource(R.string.settings_localization_ready),
                value = stringResource(R.string.settings_local),
            )
            StaticSettingRow(
                title = stringResource(R.string.settings_appearance),
                description = stringResource(R.string.settings_appearance_desc),
                value = stringResource(R.string.settings_appearance_value),
            )
        }

        SettingsSection(title = stringResource(R.string.settings_pomodoro), subtitle = stringResource(R.string.settings_pomodoro_subtitle)) {
            DurationSlider(
                label = stringResource(R.string.settings_focus),
                value = uiState.preferences.focusMinutes,
                valueRange = 15f..60f,
                onValueChange = onFocusMinutesChange,
            )
            DurationSlider(
                label = stringResource(R.string.settings_short_break),
                value = uiState.preferences.shortBreakMinutes,
                valueRange = 3f..15f,
                onValueChange = onShortBreakMinutesChange,
            )
            DurationSlider(
                label = stringResource(R.string.settings_long_break),
                value = uiState.preferences.longBreakMinutes,
                valueRange = 10f..30f,
                onValueChange = onLongBreakMinutesChange,
            )
            SettingRow(
                title = stringResource(R.string.settings_auto_start),
                description = stringResource(R.string.settings_auto_start_desc),
                checked = uiState.preferences.autoStartNextSession,
                onCheckedChange = onAutoStartToggle,
            )
            StaticSettingRow(
                title = stringResource(R.string.settings_focus_sounds),
                description = stringResource(R.string.settings_focus_sounds_desc),
                value = stringResource(R.string.settings_planned),
            )
        }

        SettingsSection(title = stringResource(R.string.settings_habits), subtitle = stringResource(R.string.settings_habits_subtitle)) {
            SettingRow(
                title = stringResource(R.string.settings_notifications),
                description = stringResource(R.string.settings_notifications_desc),
                checked = uiState.preferences.notificationsEnabled,
                onCheckedChange = onNotificationsToggle,
            )
            StaticSettingRow(
                title = stringResource(R.string.settings_week_starts_on),
                description = stringResource(R.string.settings_week_starts_desc),
                value = "Monday",
            )
            StaticSettingRow(
                title = stringResource(R.string.settings_completion_feedback),
                description = stringResource(R.string.settings_completion_feedback_desc),
                value = stringResource(R.string.settings_ready),
            )
        }

        SettingsSection(title = stringResource(R.string.settings_ai), subtitle = stringResource(R.string.settings_ai_subtitle)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(BloomSpacing.md),
            ) {
                BloomAiBadge()
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(R.string.settings_bloom_coach), style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = if (!uiState.preferences.bloomCoachEnabled) {
                            stringResource(R.string.settings_disabled_privacy)
                        } else if (uiState.aiIntegration.configured) {
                            stringResource(R.string.settings_groq_ready, uiState.aiIntegration.modelId)
                        } else {
                            stringResource(R.string.settings_local_fallback)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                BloomButton(text = stringResource(R.string.settings_open), onClick = onOpenCoach)
            }
            StaticSettingRow(
                title = stringResource(R.string.settings_api),
                description = uiState.aiIntegration.baseUrl,
                value = when {
                    !uiState.preferences.bloomCoachEnabled -> stringResource(R.string.settings_disabled)
                    uiState.aiIntegration.configured -> stringResource(R.string.settings_online)
                    else -> stringResource(R.string.settings_local)
                },
            )
            StaticSettingRow(
                title = stringResource(R.string.settings_history),
                description = stringResource(R.string.settings_history_desc),
                value = stringResource(R.string.settings_planned),
            )
        }

        SettingsSection(title = stringResource(R.string.settings_privacy_ai), subtitle = stringResource(R.string.settings_privacy_ai_subtitle)) {
            Text(
                text = stringResource(R.string.settings_offline_privacy),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            SettingRow(
                title = stringResource(R.string.settings_enable_bloom_coach),
                description = stringResource(R.string.settings_enable_bloom_coach_desc),
                checked = uiState.preferences.bloomCoachEnabled,
                onCheckedChange = onBloomCoachToggle,
            )
            SettingRow(
                title = stringResource(R.string.settings_allow_habit_context),
                description = stringResource(R.string.settings_allow_habit_context_desc),
                checked = uiState.preferences.allowHabitContextForAi,
                enabled = uiState.preferences.bloomCoachEnabled,
                onCheckedChange = onHabitContextForAiToggle,
            )
        }

        SettingsSection(title = stringResource(R.string.settings_account), subtitle = stringResource(R.string.settings_account_subtitle)) {
            StaticSettingRow(
                title = stringResource(R.string.settings_backup),
                description = stringResource(R.string.settings_backup_desc),
                value = stringResource(R.string.settings_offline),
            )
            StaticSettingRow(
                title = stringResource(R.string.settings_export),
                description = stringResource(R.string.settings_export_desc),
                value = if (uiState.exportSnapshot.isBlank()) stringResource(R.string.settings_export_ready) else stringResource(R.string.settings_export_generated),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.sm)) {
                BloomButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.settings_export_json),
                    onClick = onExportData,
                )
                BloomOutlinedButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.settings_save_json),
                    enabled = uiState.exportSnapshot.isNotBlank(),
                    onClick = { onSaveExport(uiState.exportSnapshot) },
                )
                BloomOutlinedButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.settings_share_json),
                    enabled = uiState.exportSnapshot.isNotBlank(),
                    onClick = { onShareExport(uiState.exportSnapshot) },
                )
                BloomOutlinedButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.settings_clear),
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
                title = stringResource(R.string.settings_restore),
                description = stringResource(R.string.settings_restore_desc),
                value = stringResource(R.string.settings_next),
            )
            BloomOutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                text = if (uiState.resetInProgress) stringResource(R.string.settings_resetting) else stringResource(R.string.settings_reset_data),
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
                text = stringResource(R.string.settings_minutes, value),
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

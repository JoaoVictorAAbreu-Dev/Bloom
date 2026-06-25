package com.bloom.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bloom.app.domain.model.ThemeMode
import com.bloom.app.ui.components.BloomButton
import com.bloom.app.ui.components.BloomAiBadge
import com.bloom.app.ui.components.BloomCard
import com.bloom.app.ui.components.BloomHeader
import com.bloom.app.ui.components.BloomOutlinedButton
import com.bloom.app.ui.state.SettingsUiState
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

        BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
            Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
                Text(text = "Profile", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = uiState.preferences.userName,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Display name") },
                )
            }
        }

        BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
            Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
                Text(text = "Theme", style = MaterialTheme.typography.titleLarge)
                ThemeSelector(
                    selected = uiState.preferences.themeMode,
                    onSelected = onThemeChange,
                )
            }
        }

        BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
            Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
                Text(text = "Pomodoro", style = MaterialTheme.typography.titleLarge)
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
                    title = "Notifications",
                    description = "Gentle reminders and alerts.",
                    checked = uiState.preferences.notificationsEnabled,
                    onCheckedChange = onNotificationsToggle,
                )
                SettingRow(
                    title = "Auto-start next session",
                    description = "Move between focus and break automatically.",
                    checked = uiState.preferences.autoStartNextSession,
                    onCheckedChange = onAutoStartToggle,
                )
            }
        }

        BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
            Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.sm)) {
                Text(text = "About", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "Bloom is a local-first habit, routine, and Pomodoro companion built with Jetpack Compose.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "Core app data stays on device. Bloom Coach can connect to Groq when configured.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(BloomSpacing.md),
            ) {
                BloomAiBadge()
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Bloom Coach", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = if (uiState.aiIntegration.configured) {
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
        }

        BloomOutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = BloomSpacing.screenPadding),
            text = if (uiState.resetInProgress) "Resetting..." else "Reset data",
            enabled = !uiState.resetInProgress,
            onClick = onResetData,
        )
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
            Text(text = "$value min", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

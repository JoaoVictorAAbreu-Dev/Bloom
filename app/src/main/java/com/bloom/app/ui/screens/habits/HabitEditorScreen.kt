package com.bloom.app.ui.screens.habits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bloom.app.domain.model.HabitCategory
import com.bloom.app.domain.model.HabitFrequency
import com.bloom.app.ui.components.BloomButton
import com.bloom.app.ui.components.BloomCard
import com.bloom.app.ui.components.BloomIconButton
import com.bloom.app.ui.state.HabitEditorUiState
import com.bloom.app.ui.theme.BloomColors
import com.bloom.app.ui.theme.BloomSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitEditorScreen(
    uiState: HabitEditorUiState,
    onBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onCategoryChange: (HabitCategory) -> Unit,
    onFrequencyChange: (HabitFrequency) -> Unit,
    onReminderHourChange: (Int) -> Unit,
    onReminderMinuteChange: (Int) -> Unit,
    onColorChange: (Int) -> Unit,
    onIconChange: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = BloomSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(BloomSpacing.cardGap),
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = if (uiState.habitId == null) "Create Habit" else "Edit Habit",
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Keep your routine clear and calm.",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    )
                }
            },
            navigationIcon = {
                BloomIconButton(
                    onClick = onBack,
                    modifier = Modifier.padding(start = BloomSpacing.screenPadding),
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                ) {
                    Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                if (uiState.habitId != null) {
                    BloomIconButton(
                        onClick = onDelete,
                        modifier = Modifier.padding(end = BloomSpacing.screenPadding),
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.errorContainer,
                        contentColor = androidx.compose.material3.MaterialTheme.colorScheme.error,
                    ) {
                        Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Delete habit")
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
            ),
        )

        BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
            Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
                Text("Name", style = androidx.compose.material3.MaterialTheme.typography.labelLarge)
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Read 10 pages") },
                )
                Text("Category", style = androidx.compose.material3.MaterialTheme.typography.labelLarge)
                CategoryRow(selected = uiState.category, onSelected = onCategoryChange)
                Text("Frequency", style = androidx.compose.material3.MaterialTheme.typography.labelLarge)
                FrequencyRow(selected = uiState.frequency, onSelected = onFrequencyChange)
            }
        }

        BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
            Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
                Text("Reminder", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.sm)) {
                    OutlinedTextField(
                        value = uiState.reminderHour.toString().padStart(2, '0'),
                        onValueChange = { value ->
                            value.filter(Char::isDigit).takeIf { it.isNotBlank() }?.toIntOrNull()?.let(onReminderHourChange)
                        },
                        label = { Text("Hour") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = uiState.reminderMinute.toString().padStart(2, '0'),
                        onValueChange = { value ->
                            value.filter(Char::isDigit).takeIf { it.isNotBlank() }?.toIntOrNull()?.let(onReminderMinuteChange)
                        },
                        label = { Text("Minute") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                }
            }
        }

        BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
            Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
                Text("Color", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                ColorRow(selectedColor = uiState.colorArgb, onSelected = onColorChange)
                Text("Icon", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                IconRow(selectedIcon = uiState.iconKey, onSelected = onIconChange)
            }
        }

        BloomButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = BloomSpacing.screenPadding),
            text = if (uiState.saving) "Saving..." else "Save Habit",
            enabled = !uiState.saving && uiState.name.isNotBlank(),
            onClick = onSave,
        )
    }
}

@Composable
private fun CategoryRow(
    selected: HabitCategory,
    onSelected: (HabitCategory) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.xs)) {
        HabitCategory.defaultCategories.forEach { category ->
            AssistChip(
                onClick = { onSelected(category) },
                label = { Text(category.label) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (selected == category) androidx.compose.material3.MaterialTheme.colorScheme.primary else androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = if (selected == category) androidx.compose.material3.MaterialTheme.colorScheme.onPrimary else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}

@Composable
private fun FrequencyRow(
    selected: HabitFrequency,
    onSelected: (HabitFrequency) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.xs)) {
        HabitFrequency.values().forEach { frequency ->
            AssistChip(
                onClick = { onSelected(frequency) },
                label = { Text(frequency.label) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (selected == frequency) androidx.compose.material3.MaterialTheme.colorScheme.primary else androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = if (selected == frequency) androidx.compose.material3.MaterialTheme.colorScheme.onPrimary else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}

@Composable
private fun ColorRow(
    selectedColor: Int,
    onSelected: (Int) -> Unit,
) {
    val colors = listOf(
        BloomColors.Primary.toArgb(),
        BloomColors.AccentLavender.toArgb(),
        BloomColors.AccentBlue.toArgb(),
        BloomColors.Warning.toArgb(),
        Color(0xFFCFE0B1).toArgb(),
        Color(0xFFD6C8F5).toArgb(),
    )
    Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.xs)) {
        colors.forEach { colorArgb ->
            BloomIconButton(
                onClick = { onSelected(colorArgb) },
                modifier = Modifier,
                containerColor = Color(colorArgb),
                contentColor = Color.White,
            ) {
                Text(text = if (selectedColor == colorArgb) "On" else "")
            }
        }
    }
}

@Composable
private fun IconRow(
    selectedIcon: String,
    onSelected: (String) -> Unit,
) {
    val icons = listOf("watering_can", "journal", "focus", "shoe", "leaf", "sprout")
    Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.xs)) {
        icons.forEach { icon ->
            AssistChip(
                onClick = { onSelected(icon) },
                label = { Text(icon.replace("_", " ")) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (selectedIcon == icon) androidx.compose.material3.MaterialTheme.colorScheme.primary else androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = if (selectedIcon == icon) androidx.compose.material3.MaterialTheme.colorScheme.onPrimary else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}

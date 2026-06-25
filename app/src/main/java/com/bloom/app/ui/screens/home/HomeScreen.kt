package com.bloom.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bloom.app.domain.model.Habit
import com.bloom.app.ui.components.BloomButton
import com.bloom.app.ui.components.BloomAiBadge
import com.bloom.app.ui.components.BloomCard
import com.bloom.app.ui.components.BloomHabitCard
import com.bloom.app.ui.components.BloomIconButton
import com.bloom.app.ui.components.BloomLogoMark
import com.bloom.app.ui.components.BloomPixelMascot
import com.bloom.app.ui.components.BloomProgressRing
import com.bloom.app.ui.state.HomeUiState
import com.bloom.app.ui.theme.BloomColors
import com.bloom.app.ui.theme.BloomSpacing

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onHabitToggle: (String) -> Unit,
    onOpenHabits: () -> Unit,
    onOpenFocus: () -> Unit,
    onOpenRoutine: () -> Unit,
    onOpenGarden: () -> Unit,
    onOpenCoach: () -> Unit,
    onNotificationsClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = BloomSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(BloomSpacing.cardGap),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = BloomSpacing.screenPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BloomLogoMark()
                    Column(modifier = Modifier.padding(start = BloomSpacing.sm)) {
                        Text(
                            text = "Bloom",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = "Good morning, ${uiState.userName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                BloomIconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.NotificationsNone,
                        contentDescription = "Notifications",
                    )
                }
            }
        }

        item {
            BloomCard(
                modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
                    Text(
                        text = "Today’s Progress",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    AssistChip(
                        onClick = onOpenHabits,
                        label = { Text("${uiState.progressLabel}") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(BloomSpacing.lg),
                    ) {
                        BloomProgressRing(
                            progress = uiState.progress,
                            ringSize = 86.dp,
                            strokeWidth = 8.dp,
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = "${(uiState.progress * 100).toInt()}%",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${uiState.statistics.habitsDoneToday} of ${uiState.statistics.totalHabits} tasks done",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = if (uiState.progress >= 1f) "Beautiful work. The garden is thriving." else "Almost there. One gentle step at a time.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = BloomSpacing.xs),
                            )
                        }
                    }
                }
            }
        }

        item {
            BloomCard(
                modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(BloomSpacing.md),
                ) {
                    BloomAiBadge()
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bloom Coach",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Ask for a plan, a habit nudge, or a calm focus reset.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = BloomSpacing.xs),
                        )
                    }
                    BloomButton(
                        text = "Open",
                        onClick = onOpenCoach,
                    )
                }
            }
        }

        item {
            BloomCard(
                modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Current Focus",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "Pomodoro active",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        BloomIconButton(
                            onClick = onOpenFocus,
                            modifier = Modifier,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = "Open focus",
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text(
                                text = uiState.focusLabel,
                                style = MaterialTheme.typography.displaySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = uiState.focusSubtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        BloomProgressRing(
                            progress = uiState.focusProgress,
                            ringSize = 74.dp,
                            strokeWidth = 8.dp,
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = uiState.statistics.focusMinutesToday.toString(),
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                )
                                Text(
                                    text = "min",
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            BloomCard(
                modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFF2F0E9),
                                    Color(0xFFDCE7DB),
                                ),
                            ),
                        ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(BloomSpacing.lg),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Garden level ${uiState.statistics.gardenGrowth}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "Your garden is growing",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = "Unlock more plants with consistency.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = BloomSpacing.xs),
                            )
                            BloomButton(
                                modifier = Modifier.padding(top = BloomSpacing.md),
                                text = "View Garden",
                                onClick = onOpenGarden,
                            )
                        }
                        BloomPixelMascot(modifier = Modifier.weight(0.8f), size = 108.dp)
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = BloomSpacing.screenPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Today’s Routine",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(end = BloomSpacing.xs)
                        .clickable { onOpenRoutine() },
                )
            }
        }

        items(uiState.habits, key = { it.id }) { habit ->
            BloomHabitCard(
                habit = habit,
                modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
                onClick = onOpenHabits,
                onToggle = { onHabitToggle(habit.id) },
            )
        }

        item {
            Spacer(modifier = Modifier.height(BloomSpacing.xl))
        }
    }
}

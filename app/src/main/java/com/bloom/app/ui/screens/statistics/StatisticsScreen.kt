package com.bloom.app.ui.screens.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bloom.app.ui.components.BloomCard
import com.bloom.app.ui.components.BloomHeader
import com.bloom.app.ui.components.BloomPixelMascot
import com.bloom.app.ui.components.BloomStatCard
import com.bloom.app.ui.state.StatisticsUiState
import com.bloom.app.ui.theme.BloomSpacing

@Composable
fun StatisticsScreen(
    uiState: StatisticsUiState,
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
            subtitle = "Your Growth",
            onNotificationsClick = onNotificationsClick,
        )

        Column(
            modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(BloomSpacing.xs),
        ) {
            Text(
                text = "Your Growth",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "Here is how your garden is flourishing this week.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (uiState.statistics.totalHabits == 0 && uiState.sessions.isEmpty()) {
            EmptyStatsState()
        } else {
            WeeklyConsistencyCard(
                statisticsUiState = uiState,
            )

            Row(
                modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
                horizontalArrangement = Arrangement.spacedBy(BloomSpacing.cardGap),
            ) {
                BloomStatCard(
                    title = "Focus Time",
                    value = "${uiState.statistics.focusMinutesToday}m",
                    caption = "Today",
                    modifier = Modifier.weight(1f),
                    accentColor = Color(0xFFC6E9E9),
                )
                BloomStatCard(
                    title = "Habits Done",
                    value = uiState.statistics.habitsDoneToday.toString(),
                    caption = "Today",
                    modifier = Modifier.weight(1f),
                    accentColor = Color(0xFF8DAA91),
                )
            }

            Row(
                modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
                horizontalArrangement = Arrangement.spacedBy(BloomSpacing.cardGap),
            ) {
                BloomStatCard(
                    title = "Longest Streak",
                    value = "${uiState.statistics.longestStreak} days",
                    caption = "Best run",
                    modifier = Modifier.weight(1f),
                    accentColor = Color(0xFFAE98D6),
                )
                BloomStatCard(
                    title = "Garden Growth",
                    value = uiState.statistics.gardenGrowth.toString(),
                    caption = "Points",
                    modifier = Modifier.weight(1f),
                    accentColor = Color(0xFFD9A441),
                )
            }

            WeeklyChartCard(
                uiState = uiState,
            )

            BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
                Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.sm)) {
                    Text(
                        text = "Recent Sessions",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    uiState.sessions.take(6).forEach { session ->
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = "${session.mode.label} • ${session.durationMinutes} min • ${if (session.completed) "Done" else "Stopped"}",
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyConsistencyCard(
    statisticsUiState: StatisticsUiState,
) {
    BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
        Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(text = "Weekly Consistency", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = "+${statisticsUiState.statistics.weeklyConsistency}% from last week",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                AssistChip(
                    onClick = { },
                    label = { Text("${statisticsUiState.statistics.weeklyConsistency}%") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
            WeeklyBars(
                values = statisticsUiState.statistics.weeklyHabitCompletions,
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun WeeklyChartCard(
    uiState: StatisticsUiState,
) {
    BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
        Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
            Text(text = "Focus Time by Day", style = MaterialTheme.typography.titleLarge)
            WeeklyBars(
                values = uiState.statistics.weeklyFocusMinutes,
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun WeeklyBars(
    values: List<Int>,
    labelColor: Color,
) {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    val max = (values.maxOrNull() ?: 1).coerceAtLeast(1)
    val activeBarColor = MaterialTheme.colorScheme.primary
    val inactiveBarColor = MaterialTheme.colorScheme.surfaceVariant
    Row(
        modifier = Modifier.fillMaxWidth().height(180.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        days.indices.forEach { index ->
            val value = values.getOrNull(index) ?: 0
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                Canvas(modifier = Modifier.height(120.dp).fillMaxWidth()) {
                    val barHeight = (size.height * (value / max.toFloat())).coerceAtLeast(if (value > 0) 10f else 4f)
                    drawRoundRect(
                        color = if (value > 0) activeBarColor else inactiveBarColor,
                        topLeft = Offset(size.width * 0.2f, size.height - barHeight),
                        size = androidx.compose.ui.geometry.Size(size.width * 0.6f, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f),
                    )
                }
                Text(text = days[index], color = labelColor, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun EmptyStatsState() {
    BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
            BloomPixelMascot(size = 120.dp)
            Text(text = "No data yet", style = MaterialTheme.typography.titleLarge)
            Text(
                text = "Complete a habit or a focus session to start seeing growth.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

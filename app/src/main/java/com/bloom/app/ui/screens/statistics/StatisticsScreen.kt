package com.bloom.app.ui.screens.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import com.bloom.app.R
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
            subtitle = stringResource(R.string.stats_subtitle),
            onNotificationsClick = onNotificationsClick,
        )

        Column(
            modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(BloomSpacing.xs),
        ) {
            Text(
                text = stringResource(R.string.stats_title),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = stringResource(R.string.stats_subtitle),
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

            ConsistencyHeatmapCard(
                values = uiState.statistics.monthlyHabitCompletions,
            )

            Row(
                modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
                horizontalArrangement = Arrangement.spacedBy(BloomSpacing.cardGap),
            ) {
                BloomStatCard(
                    title = stringResource(R.string.stats_focus_time),
                    value = "${uiState.statistics.focusMinutesToday}m",
                    caption = stringResource(R.string.stats_today),
                    modifier = Modifier.weight(1f),
                    accentColor = Color(0xFFC6E9E9),
                )
                BloomStatCard(
                    title = stringResource(R.string.stats_habits_done),
                    value = uiState.statistics.habitsDoneToday.toString(),
                    caption = stringResource(R.string.stats_today),
                    modifier = Modifier.weight(1f),
                    accentColor = Color(0xFF8DAA91),
                )
            }

            Row(
                modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
                horizontalArrangement = Arrangement.spacedBy(BloomSpacing.cardGap),
            ) {
                BloomStatCard(
                    title = stringResource(R.string.stats_longest_streak),
                    value = "${uiState.statistics.longestStreak} days",
                    caption = stringResource(R.string.stats_best_run),
                    modifier = Modifier.weight(1f),
                    accentColor = Color(0xFFAE98D6),
                )
                BloomStatCard(
                    title = stringResource(R.string.stats_garden_growth),
                    value = uiState.statistics.gardenGrowth.toString(),
                    caption = stringResource(R.string.stats_points),
                    modifier = Modifier.weight(1f),
                    accentColor = Color(0xFFD9A441),
                )
            }

            WeeklyChartCard(
                uiState = uiState,
            )

            MonthlyFocusCard(
                values = uiState.statistics.monthlyFocusMinutes,
            )

            PatternsCard(
                averageFocusMinutes = uiState.statistics.averageFocusMinutes,
                mostProductiveHour = uiState.statistics.mostProductiveHourLabel,
                topHabitName = uiState.statistics.topHabitName,
            )

            BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
                Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.sm)) {
                    Text(
                        text = stringResource(R.string.stats_recent_sessions),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    uiState.sessions.take(6).forEach { session ->
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = "${session.mode.label} - ${session.durationMinutes} min - ${if (session.completed) stringResource(R.string.stats_done) else stringResource(R.string.stats_stopped)}",
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
private fun ConsistencyHeatmapCard(
    values: List<Int>,
) {
    val heatmapValues = values.takeLast(28).let { recent ->
        List((28 - recent.size).coerceAtLeast(0)) { 0 } + recent
    }
    val activeDays = heatmapValues.count { it > 0 }

    BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
        Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(text = stringResource(R.string.stats_heatmap_title), style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = stringResource(R.string.stats_heatmap_days, activeDays),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                AssistChip(
                    onClick = { },
                    label = { Text(stringResource(R.string.stats_28_days)) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.xs)) {
                heatmapValues.chunked(7).forEach { week ->
                    Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.xs)) {
                        week.forEach { value ->
                            HeatmapCell(value = value)
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(BloomSpacing.xs),
            ) {
                Text(
                    text = stringResource(R.string.stats_less),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                listOf(0, 1, 2, 4).forEach { value ->
                    HeatmapCell(value = value, compact = true)
                }
                Text(
                    text = stringResource(R.string.stats_more),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun HeatmapCell(
    value: Int,
    compact: Boolean = false,
) {
    val primary = MaterialTheme.colorScheme.primary
    val color = when {
        value <= 0 -> MaterialTheme.colorScheme.surfaceVariant
        value == 1 -> primary.copy(alpha = 0.38f)
        value == 2 -> primary.copy(alpha = 0.62f)
        else -> primary
    }
    Spacer(
        modifier = Modifier
            .size(if (compact) 12.dp else 28.dp)
            .background(color = color, shape = RoundedCornerShape(6.dp)),
    )
}

@Composable
private fun WeeklyConsistencyCard(
    statisticsUiState: StatisticsUiState,
) {
    BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
        Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(text = stringResource(R.string.stats_weekly_consistency), style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = stringResource(R.string.stats_weekly_plus, statisticsUiState.statistics.weeklyConsistency),
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
            Text(text = stringResource(R.string.stats_focus_by_day), style = MaterialTheme.typography.titleLarge)
            WeeklyBars(
                values = uiState.statistics.weeklyFocusMinutes,
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun MonthlyFocusCard(
    values: List<Int>,
) {
    BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
        Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
            Text(text = stringResource(R.string.stats_monthly_focus), style = MaterialTheme.typography.titleLarge)
            SimpleBars(
                values = values,
                labels = listOf("W1", "W2", "W3", "W4"),
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PatternsCard(
    averageFocusMinutes: Int,
    mostProductiveHour: String,
    topHabitName: String,
) {
    BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
        Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
            Text(text = stringResource(R.string.stats_productivity_patterns), style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.sm)) {
                BloomStatCard(
                    title = stringResource(R.string.stats_avg_focus),
                    value = "${averageFocusMinutes}m",
                    caption = stringResource(R.string.stats_per_session),
                    modifier = Modifier.weight(1f),
                    accentColor = Color(0xFFC6E9E9),
                )
                BloomStatCard(
                    title = stringResource(R.string.stats_best_hour),
                    value = mostProductiveHour,
                    caption = stringResource(R.string.stats_most_focused),
                    modifier = Modifier.weight(1f),
                    accentColor = Color(0xFFD9A441),
                )
            }
            BloomCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(BloomSpacing.md),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.xxs)) {
                    Text(
                        text = stringResource(R.string.stats_most_completed_habit),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = topHabitName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
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
private fun SimpleBars(
    values: List<Int>,
    labels: List<String>,
    labelColor: Color,
) {
    val max = (values.maxOrNull() ?: 1).coerceAtLeast(1)
    val activeBarColor = MaterialTheme.colorScheme.primary
    val inactiveBarColor = MaterialTheme.colorScheme.surfaceVariant
    Row(
        modifier = Modifier.fillMaxWidth().height(160.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        labels.indices.forEach { index ->
            val value = values.getOrNull(index) ?: 0
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                Canvas(modifier = Modifier.height(104.dp).fillMaxWidth()) {
                    val barHeight = (size.height * (value / max.toFloat())).coerceAtLeast(if (value > 0) 10f else 4f)
                    drawRoundRect(
                        color = if (value > 0) activeBarColor else inactiveBarColor,
                        topLeft = Offset(size.width * 0.25f, size.height - barHeight),
                        size = androidx.compose.ui.geometry.Size(size.width * 0.5f, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f),
                    )
                }
                Text(text = labels[index], color = labelColor, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun EmptyStatsState() {
    BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
            BloomPixelMascot(size = 120.dp)
            Text(text = stringResource(R.string.stats_empty_title), style = MaterialTheme.typography.titleLarge)
            Text(
                text = stringResource(R.string.stats_empty_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bloom.app.domain.model.Reward
import com.bloom.app.ui.components.BloomButton
import com.bloom.app.ui.components.BloomCard
import com.bloom.app.ui.components.BloomHeader
import com.bloom.app.ui.components.BloomOutlinedButton
import com.bloom.app.ui.components.BloomPixelMascot
import com.bloom.app.ui.state.SettingsUiState
import com.bloom.app.ui.theme.BloomRadius
import com.bloom.app.ui.theme.BloomSpacing

@Composable
fun ProfileScreen(
    uiState: SettingsUiState,
    onOpenGarden: () -> Unit,
    onOpenSettings: () -> Unit,
    onNotificationsClick: () -> Unit,
) {
    val statistics = uiState.statistics
    val activeDays = statistics.weeklyHabitCompletions.count { it > 0 }
    val unlockedRewards = uiState.rewards.filter { it.unlocked }

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
            subtitle = "Profile",
            onNotificationsClick = onNotificationsClick,
        )

        BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(BloomSpacing.lg),
            ) {
                Surface(
                    shape = BloomRadius.circle,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                ) {
                    BloomPixelMascot(
                        modifier = Modifier.padding(BloomSpacing.sm),
                        size = 96.dp,
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(BloomSpacing.xs),
                ) {
                    Text(
                        text = uiState.preferences.userName,
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = uiState.preferences.primaryGoal,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Garden level ${statistics.gardenGrowth}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        ProfileStatsGrid(
            activeDays = activeDays,
            totalHabits = statistics.totalHabits,
            focusMinutes = statistics.focusMinutesToday,
            longestStreak = statistics.longestStreak,
            unlockedRewards = unlockedRewards.size,
        )

        BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
            Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
                Text(
                    text = "Achievements",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (unlockedRewards.isEmpty()) {
                    Text(
                        text = "Complete habits and focus sessions to unlock your first rewards.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    unlockedRewards.take(4).forEach { reward ->
                        AchievementRow(reward = reward)
                    }
                }
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(BloomSpacing.sm),
        ) {
            BloomButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Open Garden",
                onClick = onOpenGarden,
            )
            BloomOutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Settings",
                onClick = onOpenSettings,
            )
        }
    }
}

@Composable
private fun ProfileStatsGrid(
    activeDays: Int,
    totalHabits: Int,
    focusMinutes: Int,
    longestStreak: Int,
    unlockedRewards: Int,
) {
    val stats = listOf(
        ProfileMetric("Active days", activeDays.coerceAtLeast(0).toString(), "This week"),
        ProfileMetric("Habits", totalHabits.toString(), "In your garden"),
        ProfileMetric("Focus", "${focusMinutes}m", "Today"),
        ProfileMetric("Best streak", "$longestStreak", "Days"),
        ProfileMetric("Rewards", unlockedRewards.toString(), "Unlocked"),
        ProfileMetric("Rhythm", if (activeDays >= 4) "Strong" else "Growing", "Weekly"),
    )

    Column(
        modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(BloomSpacing.sm),
    ) {
        stats.chunked(2).forEach { rowStats ->
            Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.sm)) {
                rowStats.forEach { stat ->
                    ProfileMetricCard(
                        stat = stat,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowStats.size == 1) {
                    Column(modifier = Modifier.weight(1f)) {}
                }
            }
        }
    }
}

@Composable
private fun ProfileMetricCard(
    stat: ProfileMetric,
    modifier: Modifier = Modifier,
) {
    BloomCard(
        modifier = modifier,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(BloomSpacing.md),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.xxs)) {
            Text(
                text = stat.label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stat.value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stat.caption,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AchievementRow(reward: Reward) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = BloomRadius.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
    ) {
        Column(
            modifier = Modifier.padding(BloomSpacing.md),
            verticalArrangement = Arrangement.spacedBy(BloomSpacing.xxs),
        ) {
            Text(
                text = reward.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = reward.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private data class ProfileMetric(
    val label: String,
    val value: String,
    val caption: String,
)

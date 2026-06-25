package com.bloom.app.ui.screens.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bloom.app.domain.model.Reward
import com.bloom.app.ui.components.BloomCard
import com.bloom.app.ui.components.BloomHeader
import com.bloom.app.ui.components.BloomPixelFlower
import com.bloom.app.ui.components.BloomPixelMascot
import com.bloom.app.ui.components.BloomPixelPlant
import com.bloom.app.ui.state.GardenUiState
import com.bloom.app.ui.theme.BloomSpacing

@Composable
fun GardenScreen(
    uiState: GardenUiState,
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
            subtitle = "Garden Rewards",
            onNotificationsClick = onNotificationsClick,
        )

        Column(
            modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(BloomSpacing.xs),
        ) {
            Text(
                text = "Your Garden",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "Plants, decor, and small companions unlocked by consistency.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
            Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md), horizontalAlignment = Alignment.CenterHorizontally) {
                BloomPixelPlant(size = 140.dp)
                Text(
                    text = "Garden level ${uiState.statistics.gardenGrowth}",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = "Grow a little every day.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                AssistChip(
                    onClick = { },
                    label = { Text("${uiState.statistics.longestStreak} day streak") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(BloomSpacing.cardGap),
        ) {
            uiState.rewards.chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.cardGap)) {
                    pair.forEach { reward ->
                        RewardCard(
                            reward = reward,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (pair.size == 1) {
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardCard(
    reward: Reward,
    modifier: Modifier = Modifier,
) {
    BloomCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(BloomSpacing.md),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.sm)) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                when (reward.iconKey) {
                    "sprout" -> BloomPixelPlant(size = 84.dp)
                    "flower" -> BloomPixelFlower(size = 84.dp)
                    "tree" -> BloomPixelMascot(size = 84.dp)
                    else -> BloomPixelMascot(size = 84.dp)
                }
            }
            Text(text = reward.title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = reward.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            AssistChip(
                onClick = { },
                label = { Text(if (reward.unlocked) "Unlocked" else "Locked") },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (reward.unlocked) Color(reward.accentColorArgb).copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = if (reward.unlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}

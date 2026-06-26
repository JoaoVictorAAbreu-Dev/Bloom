package com.bloom.app.ui.screens.garden

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bloom.app.domain.model.Reward
import com.bloom.app.ui.components.BloomCard
import com.bloom.app.ui.components.BloomHeader
import com.bloom.app.ui.components.BloomPixelFlower
import com.bloom.app.ui.components.BloomPixelMascot
import com.bloom.app.ui.components.BloomPixelPlant
import com.bloom.app.ui.components.BloomPixelTree
import com.bloom.app.ui.state.GardenUiState
import com.bloom.app.ui.theme.BloomRadius
import com.bloom.app.ui.theme.BloomSpacing

@Composable
fun GardenScreen(
    uiState: GardenUiState,
    onNotificationsClick: () -> Unit,
) {
    val unlockedCount = uiState.rewards.count { it.unlocked }
    val gardenStage = when {
        uiState.statistics.gardenGrowth >= 80 -> "Thriving"
        uiState.statistics.gardenGrowth >= 40 -> "Blooming"
        uiState.statistics.gardenGrowth >= 12 -> "Sprouting"
        else -> "Seedling"
    }

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
                text = "Plants, decor, and companions unlocked by consistency.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        LivingGardenCard(
            level = uiState.statistics.gardenGrowth,
            streak = uiState.statistics.longestStreak,
            stage = gardenStage,
            unlockedCount = unlockedCount,
        )

        BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
            Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.sm)) {
                Text(
                    text = "Unlock Path",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Every habit completed and focus session feeds the garden.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.sm)) {
                    GardenMilestone("Grass", uiState.statistics.gardenGrowth >= 8, Modifier.weight(1f))
                    GardenMilestone("Flowers", uiState.statistics.gardenGrowth >= 24, Modifier.weight(1f))
                    GardenMilestone("Tree", uiState.statistics.gardenGrowth >= 48, Modifier.weight(1f))
                }
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
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun LivingGardenCard(
    level: Int,
    streak: Int,
    stage: String,
    unlockedCount: Int,
) {
    BloomCard(
        modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFAF7EF),
                            Color(0xFFE4EBDD),
                            Color(0xFFB8C9A8),
                        ),
                    ),
                ),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color(0xFFFFFFFF).copy(alpha = 0.65f),
                    radius = 64.dp.toPx(),
                    center = Offset(size.width * 0.18f, size.height * 0.18f),
                )
                drawRect(
                    color = Color(0xFF78936D).copy(alpha = 0.28f),
                    topLeft = Offset(0f, size.height * 0.72f),
                    size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.28f),
                )
                drawRect(
                    color = Color(0xFF5D7F63).copy(alpha = 0.22f),
                    topLeft = Offset(0f, size.height * 0.82f),
                    size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.18f),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(BloomSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(BloomSpacing.xs),
            ) {
                Text(
                    text = stage,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Garden level $level",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = BloomSpacing.lg, vertical = BloomSpacing.md),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom,
            ) {
                BloomPixelPlant(size = 84.dp)
                if (level >= 12) BloomPixelFlower(size = 72.dp)
                if (level >= 40) BloomPixelTree(size = 112.dp)
                if (unlockedCount >= 3 || streak >= 7) BloomPixelMascot(size = 88.dp)
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(BloomSpacing.lg),
                horizontalArrangement = Arrangement.spacedBy(BloomSpacing.xs),
            ) {
                AssistChip(
                    onClick = { },
                    label = { Text("$streak day streak") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
                        labelColor = MaterialTheme.colorScheme.onSurface,
                    ),
                )
            }
        }
    }
}

@Composable
private fun GardenMilestone(
    title: String,
    unlocked: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
                color = if (unlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceVariant,
                shape = BloomRadius.large,
            )
            .padding(BloomSpacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (unlocked) "$title unlocked" else title,
            style = MaterialTheme.typography.labelLarge,
            color = if (unlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
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
                    "tree" -> BloomPixelTree(size = 92.dp)
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

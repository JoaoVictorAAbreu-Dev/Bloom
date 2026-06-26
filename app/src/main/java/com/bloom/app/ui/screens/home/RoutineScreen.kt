package com.bloom.app.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bloom.app.domain.model.RoutineBlock
import com.bloom.app.ui.components.BloomCard
import com.bloom.app.ui.components.BloomHeader
import com.bloom.app.ui.state.RoutineUiState
import com.bloom.app.ui.theme.BloomRadius
import com.bloom.app.ui.theme.BloomSpacing

@Composable
fun RoutineScreen(
    uiState: RoutineUiState,
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
            BloomHeader(
                title = "Bloom",
                subtitle = "Routine Timeline",
                onNotificationsClick = onNotificationsClick,
            )
        }
        item {
            Column(
                modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
                verticalArrangement = Arrangement.spacedBy(BloomSpacing.xs),
            ) {
                Text(
                    text = "Routine Timeline",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "A clear rhythm for morning, afternoon, evening, and night.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        itemsIndexed(uiState.routineBlocks, key = { _, block -> block.id }) { index, block ->
            RoutineTimelineItem(
                block = block,
                isLast = index == uiState.routineBlocks.lastIndex,
                modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
            )
        }
    }
}

@Composable
private fun RoutineTimelineItem(
    block: RoutineBlock,
    isLast: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(BloomSpacing.md),
        verticalAlignment = Alignment.Top,
    ) {
        TimelineRail(
            color = if (block.active) MaterialTheme.colorScheme.primary else Color(block.colorArgb),
            active = block.active,
            isLast = isLast,
        )
        RoutineBlockCard(
            block = block,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun TimelineRail(
    color: Color,
    active: Boolean,
    isLast: Boolean,
) {
    Box(
        modifier = Modifier
            .size(width = 28.dp, height = 156.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        if (!isLast) {
            Canvas(modifier = Modifier.fillMaxHeight()) {
                drawLine(
                    color = color.copy(alpha = 0.26f),
                    start = Offset(size.width / 2f, 24.dp.toPx()),
                    end = Offset(size.width / 2f, size.height),
                    strokeWidth = 2.dp.toPx(),
                )
            }
        }
        Surface(
            modifier = Modifier.size(if (active) 24.dp else 18.dp),
            shape = BloomRadius.circle,
            color = color.copy(alpha = if (active) 1f else 0.55f),
        ) {}
    }
}

@Composable
private fun RoutineBlockCard(
    block: RoutineBlock,
    modifier: Modifier = Modifier,
) {
    BloomCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(BloomSpacing.md),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.xxs)) {
                    Text(
                        text = block.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "${block.durationMinutes} min block",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                AssistChip(
                    onClick = { },
                    label = { Text(if (block.active) "Now" else block.slot) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (block.active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = if (block.active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.xs)) {
                block.activities.forEach { activity ->
                    RoutineActivityRow(
                        title = activity,
                        active = block.active,
                    )
                }
            }
        }
    }
}

@Composable
private fun RoutineActivityRow(
    title: String,
    active: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(BloomSpacing.sm),
    ) {
        Surface(
            modifier = Modifier.size(18.dp),
            shape = BloomRadius.circle,
            color = if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            ),
        ) {}
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

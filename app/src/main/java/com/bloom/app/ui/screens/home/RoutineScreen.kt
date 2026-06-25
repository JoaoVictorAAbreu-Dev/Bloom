package com.bloom.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                    text = "Morning, afternoon, evening, and night.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        items(uiState.routineBlocks, key = { it.id }) { block ->
            RoutineBlockCard(
                block = block,
                modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
            )
        }
    }
}

@Composable
private fun RoutineBlockCard(
    block: RoutineBlock,
    modifier: Modifier = Modifier,
    ) {
    val activeColor = MaterialTheme.colorScheme.primary
    BloomCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(28.dp).padding(end = BloomSpacing.sm)) {
                    drawCircle(
                        color = if (block.active) activeColor else Color(block.colorArgb).copy(alpha = 0.45f),
                        radius = 10.dp.toPx(),
                    )
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(BloomSpacing.xs)) {
                Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.xs), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = block.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (block.active) {
                        AssistChip(
                            onClick = { },
                            label = { Text("Now") },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                labelColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        )
                    }
                }
                Text(
                    text = block.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${block.durationMinutes} min",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

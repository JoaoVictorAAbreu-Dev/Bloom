package com.bloom.app.ui.screens.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.bloom.app.R
import com.bloom.app.ui.components.BloomButton
import com.bloom.app.ui.components.BloomCard
import com.bloom.app.ui.components.BloomHeader
import com.bloom.app.ui.components.BloomOutlinedButton
import com.bloom.app.ui.components.BloomPixelTree
import com.bloom.app.ui.components.BloomProgressRing
import com.bloom.app.ui.components.BloomStatCard
import com.bloom.app.ui.state.FocusUiState
import com.bloom.app.domain.model.PomodoroMode
import com.bloom.app.ui.theme.BloomSpacing

@Composable
fun FocusScreen(
    uiState: FocusUiState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    onDeepFocusToggle: (Boolean) -> Unit,
    onNotificationsClick: () -> Unit,
) {
    val backgroundColors = if (uiState.deepFocusEnabled) {
        listOf(
            Color(0xFFE6EDE2),
            Color(0xFFD3E0D0),
            Color(0xFFBDCFBB),
        )
    } else {
        listOf(
            Color(0xFFF1F3E8),
            Color(0xFFE1E9DC),
            Color(0xFFD4DED2),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = backgroundColors,
                ),
            ),
    ) {
        BloomPixelTree(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 8.dp, bottom = 32.dp),
            size = 110.dp,
        )
        BloomPixelTree(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 4.dp, bottom = 42.dp),
            size = 96.dp,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = BloomSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(BloomSpacing.cardGap),
        ) {
            BloomHeader(
                title = "Bloom",
                subtitle = stringResource(R.string.focus_title),
                onNotificationsClick = onNotificationsClick,
            )

            Column(
                modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
                verticalArrangement = Arrangement.spacedBy(BloomSpacing.xs),
            ) {
                Text(
                    text = if (uiState.deepFocusEnabled) {
                        stringResource(R.string.focus_quote_deep)
                    } else {
                        stringResource(R.string.focus_quote_light)
                    },
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.xs)) {
                    AssistChip(
                        onClick = { onDeepFocusToggle(!uiState.deepFocusEnabled) },
                        label = {
                            Text(
                                if (uiState.deepFocusEnabled) {
                                    stringResource(R.string.focus_mode_on)
                                } else {
                                    stringResource(R.string.focus_mode_off)
                                },
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (uiState.deepFocusEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = if (uiState.deepFocusEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                    AssistChip(
                        onClick = { },
                        label = { Text(stringResource(R.string.focus_offline_first)) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                BloomProgressRing(
                    progress = uiState.progress,
                    ringSize = 300.dp,
                    strokeWidth = 12.dp,
                    progressColor = MaterialTheme.colorScheme.primary,
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = formatTime(uiState.remainingSeconds),
                            style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        AssistChip(
                            onClick = { },
                            label = { Text(uiState.mode.label.uppercase()) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = when (uiState.mode) {
                                    PomodoroMode.FOCUS -> Color(0xFFE3DDF6)
                                    PomodoroMode.SHORT_BREAK -> Color(0xFFDDF0EE)
                                    PomodoroMode.LONG_BREAK -> Color(0xFFF2EAD7)
                                },
                                labelColor = MaterialTheme.colorScheme.onSurface,
                            ),
                        )
                        Text(
                            text = stringResource(R.string.focus_round, uiState.round, uiState.totalRounds),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = BloomSpacing.sm),
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = BloomSpacing.screenPadding),
                horizontalArrangement = Arrangement.spacedBy(BloomSpacing.md),
            ) {
                BloomOutlinedButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.focus_stop),
                    onClick = onStop,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Stop,
                        contentDescription = stringResource(R.string.focus_stop),
                        modifier = Modifier.padding(start = BloomSpacing.xs),
                    )
                }
                if (uiState.running) {
                    BloomButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.focus_pause),
                        onClick = onPause,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Pause,
                            contentDescription = stringResource(R.string.focus_pause),
                            modifier = Modifier.padding(start = BloomSpacing.xs),
                        )
                    }
                } else if (uiState.paused) {
                    BloomButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.focus_resume),
                        onClick = onResume,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = stringResource(R.string.focus_resume),
                            modifier = Modifier.padding(start = BloomSpacing.xs),
                        )
                    }
                } else {
                    BloomButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.focus_start),
                        onClick = onStart,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = stringResource(R.string.focus_start),
                            modifier = Modifier.padding(start = BloomSpacing.xs),
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = BloomSpacing.screenPadding),
                horizontalArrangement = Arrangement.spacedBy(BloomSpacing.cardGap),
            ) {
                BloomStatCard(
                    title = stringResource(R.string.focus_time),
                    value = "${uiState.focusMinutesToday}m",
                    caption = "Today",
                    modifier = Modifier.weight(1f),
                    accentColor = MaterialTheme.colorScheme.secondary,
                )
                BloomStatCard(
                    title = stringResource(R.string.focus_sessions),
                    value = uiState.sessionsToday.toString(),
                    caption = "Today",
                    modifier = Modifier.weight(1f),
                    accentColor = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
    }
}

private fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

package com.bloom.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bloom.app.domain.model.Habit
import com.bloom.app.ui.theme.BloomColors
import com.bloom.app.ui.theme.BloomSpacing

@Composable
fun BloomHabitCard(
    habit: Habit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onToggle: () -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val cardScale by animateFloatAsState(
        targetValue = if (habit.completedToday) 1.015f else 1f,
        animationSpec = spring(dampingRatio = 0.58f, stiffness = 420f),
        label = "habit-card-scale",
    )
    val actionContainerColor by animateColorAsState(
        targetValue = if (habit.completedToday) Color(habit.colorArgb) else MaterialTheme.colorScheme.surfaceVariant,
        label = "habit-action-color",
    )
    val actionContentColor by animateColorAsState(
        targetValue = if (habit.completedToday) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "habit-action-content-color",
    )

    BloomCard(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = cardScale
                scaleY = cardScale
            }
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(BloomSpacing.md),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(habit.colorArgb).copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = habit.iconLabel(),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Spacer(modifier = Modifier.width(BloomSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(BloomSpacing.xxs))
                Text(
                    text = "${habit.category.label} - ${habit.frequency.label}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(BloomSpacing.xs))
                Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.xs)) {
                    HabitChip(text = "${habit.streak} day streak")
                    HabitChip(
                        text = if (habit.completedToday) "Done today" else "Pending",
                        containerColor = if (habit.completedToday) Color(habit.colorArgb).copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = if (habit.completedToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.height(BloomSpacing.xs))
                Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.xs)) {
                    HabitChip(
                        text = habit.priority,
                        containerColor = priorityColor(habit.priority).copy(alpha = 0.16f),
                        labelColor = priorityColor(habit.priority),
                    )
                    HabitChip(text = "Daily ${habit.dailyGoal}")
                    HabitChip(text = "Weekly ${habit.weeklyGoal}")
                }
            }
            Spacer(modifier = Modifier.width(BloomSpacing.sm))
            BloomIconButton(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onToggle()
                },
                modifier = Modifier.size(56.dp),
                containerColor = actionContainerColor,
                contentColor = actionContentColor,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun HabitChip(
    text: String,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    AssistChip(
        onClick = { },
        label = { Text(text) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = containerColor,
            labelColor = labelColor,
        ),
    )
}

private fun Habit.iconLabel(): String = emoji.ifBlank {
    when (iconKey) {
        "watering_can" -> "Pl"
        "journal" -> "Jn"
        "focus" -> "Fo"
        "shoe" -> "Mv"
        "leaf" -> "Lf"
        else -> "Bl"
    }
}

private fun priorityColor(priority: String): Color = when (priority) {
    "High" -> BloomColors.Warning
    "Low" -> BloomColors.PrimaryDark
    else -> BloomColors.Primary
}

package com.bloom.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun BloomProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    ringSize: Dp = 96.dp,
    strokeWidth: Dp = 8.dp,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    content: @Composable () -> Unit = {},
) {
    Box(
        modifier = modifier
            .size(ringSize)
            .drawBehind {
                val stroke = strokeWidth.toPx()
                val diameter = min(this.size.width, this.size.height) - stroke
                drawArc(
                    color = trackColor,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(stroke / 2, stroke / 2),
                    size = androidx.compose.ui.geometry.Size(diameter, diameter),
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress.coerceIn(0f, 1f),
                    useCenter = false,
                    topLeft = Offset(stroke / 2, stroke / 2),
                    size = androidx.compose.ui.geometry.Size(diameter, diameter),
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
            },
    ) {
        content()
    }
}

package com.bloom.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bloom.app.ui.theme.BloomColors
import com.bloom.app.ui.theme.BloomRadius
import com.bloom.app.ui.theme.BloomSpacing

@Composable
fun BloomCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(BloomSpacing.lg),
    tonalElevation: Dp = 0.dp,
    border: BorderStroke? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    Surface(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = BloomRadius.large,
                ambientColor = BloomColors.Shadow,
                spotColor = Color.Transparent,
            )
            .graphicsLayer {
                val scale = if (pressed) 0.99f else 1f
                scaleX = scale
                scaleY = scale
            }
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            ),
        shape = BloomRadius.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = tonalElevation,
        border = border,
    ) {
        androidx.compose.foundation.layout.Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}

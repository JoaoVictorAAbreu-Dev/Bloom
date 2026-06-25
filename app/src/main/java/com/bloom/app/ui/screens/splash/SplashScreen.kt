package com.bloom.app.ui.screens.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bloom.app.ui.components.BloomLogoMark
import com.bloom.app.ui.components.BloomPixelPlant
import com.bloom.app.ui.theme.BloomColors
import com.bloom.app.ui.theme.BloomSpacing
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "splashAlpha",
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.92f,
        animationSpec = tween(durationMillis = 700),
        label = "splashScale",
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(1400)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BloomColors.Background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .graphicsLayer(alpha = alpha, scaleX = scale, scaleY = scale)
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BloomLogoMark()
            Text(
                text = "Bloom",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = BloomSpacing.md),
            )
            Text(
                text = "Grow a little every day.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = BloomSpacing.xs),
            )
            BloomPixelPlant(
                modifier = Modifier.padding(top = BloomSpacing.xxl),
                size = 120.dp,
            )
        }
    }
}

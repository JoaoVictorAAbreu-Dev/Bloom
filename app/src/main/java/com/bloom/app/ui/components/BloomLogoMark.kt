package com.bloom.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloom.app.ui.theme.BloomRadius

@Composable
fun BloomLogoMark(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
    contentColor: Color = MaterialTheme.colorScheme.primary,
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(BloomRadius.large)
            .background(containerColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "B",
            color = contentColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}


package com.bloom.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bloom.app.R

@Composable
fun BloomLogoMark(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
) {
    Image(
        painter = painterResource(id = R.drawable.bloom_logo),
        contentDescription = "Bloom",
        modifier = modifier.then(Modifier.size(size)),
        contentScale = ContentScale.Fit,
    )
}

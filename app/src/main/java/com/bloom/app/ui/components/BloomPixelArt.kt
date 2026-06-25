package com.bloom.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BloomPixelPlant(
    modifier: Modifier = Modifier,
    size: Dp = 128.dp,
    stem: Color = Color(0xFF5D7F63),
    leaf: Color = Color(0xFF8DAA91),
    pot: Color = Color(0xFFE2D2B8),
) {
    PixelArtCanvas(modifier = modifier.size(size)) { cell, paint ->
        val center = cell * 5
        paint(4, 8, stem)
        paint(5, 8, stem)
        paint(5, 7, stem)
        paint(4, 7, leaf)
        paint(6, 7, leaf)
        paint(3, 6, leaf)
        paint(7, 6, leaf)
        paint(2, 5, leaf)
        paint(8, 5, leaf)
        paint(1, 4, leaf)
        paint(9, 4, leaf)
        paint(3, 9, pot)
        paint(4, 9, pot)
        paint(5, 9, pot)
        paint(6, 9, pot)
        paint(2, 10, pot)
        paint(3, 10, pot)
        paint(4, 10, pot)
        paint(5, 10, pot)
        paint(6, 10, pot)
        paint(7, 10, pot)
        paint(2, 11, pot)
        paint(7, 11, pot)
    }
}

@Composable
fun BloomPixelMascot(
    modifier: Modifier = Modifier,
    size: Dp = 128.dp,
) {
    PixelArtCanvas(modifier = modifier.size(size)) { _, paint ->
        val body = Color(0xFFB4D2C0)
        val outline = Color(0xFF5D7F63)
        val eye = Color(0xFF302D2A)
        val blush = Color(0xFFD9A441)
        paint(4, 1, body)
        paint(5, 1, body)
        paint(3, 2, body)
        paint(4, 2, body)
        paint(5, 2, body)
        paint(6, 2, body)
        paint(2, 3, body)
        paint(3, 3, body)
        paint(4, 3, body)
        paint(5, 3, body)
        paint(6, 3, body)
        paint(7, 3, body)
        paint(2, 4, body)
        paint(3, 4, body)
        paint(4, 4, body)
        paint(5, 4, body)
        paint(6, 4, body)
        paint(7, 4, body)
        paint(3, 5, body)
        paint(4, 5, body)
        paint(5, 5, body)
        paint(6, 5, body)
        paint(4, 6, body)
        paint(5, 6, body)
        paint(4, 2, outline)
        paint(5, 2, outline)
        paint(3, 3, outline)
        paint(6, 3, outline)
        paint(2, 4, outline)
        paint(7, 4, outline)
        paint(3, 5, outline)
        paint(6, 5, outline)
        paint(3, 4, eye)
        paint(6, 4, eye)
        paint(4, 7, blush)
        paint(5, 7, blush)
    }
}

@Composable
fun BloomPixelTree(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
) {
    PixelArtCanvas(modifier = modifier.size(size)) { _, paint ->
        val trunk = Color(0xFF8A6A4F)
        val leaves = Color(0xFF8DAA91)
        paint(4, 7, trunk)
        paint(5, 7, trunk)
        paint(4, 6, trunk)
        paint(5, 6, trunk)
        paint(3, 5, leaves)
        paint(4, 5, leaves)
        paint(5, 5, leaves)
        paint(6, 5, leaves)
        paint(2, 4, leaves)
        paint(3, 4, leaves)
        paint(4, 4, leaves)
        paint(5, 4, leaves)
        paint(6, 4, leaves)
        paint(7, 4, leaves)
        paint(3, 3, leaves)
        paint(4, 3, leaves)
        paint(5, 3, leaves)
        paint(6, 3, leaves)
        paint(4, 2, leaves)
        paint(5, 2, leaves)
    }
}

@Composable
fun BloomPixelFlower(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
) {
    PixelArtCanvas(modifier = modifier.size(size)) { _, paint ->
        val petals = Color(0xFFAE98D6)
        val center = Color(0xFFD9A441)
        val stem = Color(0xFF7E9A84)
        paint(4, 5, stem)
        paint(4, 6, stem)
        paint(4, 7, stem)
        paint(5, 5, stem)
        paint(3, 3, petals)
        paint(4, 3, petals)
        paint(5, 3, petals)
        paint(6, 3, petals)
        paint(2, 4, petals)
        paint(3, 4, petals)
        paint(4, 4, petals)
        paint(5, 4, petals)
        paint(6, 4, petals)
        paint(7, 4, petals)
        paint(3, 5, petals)
        paint(4, 5, petals)
        paint(5, 5, petals)
        paint(6, 5, petals)
        paint(4, 4, center)
        paint(5, 4, center)
        paint(4, 5, center)
        paint(5, 5, center)
    }
}

@Composable
private fun PixelArtCanvas(
    modifier: Modifier = Modifier,
    cellCount: Int = 12,
    content: (Int, (Int, Int, Color) -> Unit) -> Unit,
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cell = size.minDimension / cellCount
            fun paint(x: Int, y: Int, color: Color) {
                drawRect(
                    color = color,
                    topLeft = Offset(x * cell, y * cell),
                    size = androidx.compose.ui.geometry.Size(cell, cell),
                )
            }
            content(cellCount) { x, y, color -> paint(x, y, color) }
        }
    }
}

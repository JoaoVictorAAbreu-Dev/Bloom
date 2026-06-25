package com.bloom.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BloomLightScheme = lightColorScheme(
    primary = BloomColors.Primary,
    onPrimary = Color.White,
    primaryContainer = BloomColors.Primary.copy(alpha = 0.24f),
    onPrimaryContainer = BloomColors.PrimaryDark,
    secondary = BloomColors.PrimaryDark,
    onSecondary = Color.White,
    secondaryContainer = BloomColors.AccentBlue,
    onSecondaryContainer = BloomColors.TextPrimary,
    tertiary = BloomColors.AccentLavender,
    onTertiary = Color.White,
    background = BloomColors.Background,
    onBackground = BloomColors.TextPrimary,
    surface = BloomColors.Surface,
    onSurface = BloomColors.TextPrimary,
    surfaceVariant = BloomColors.SurfaceVariant,
    onSurfaceVariant = BloomColors.TextSecondary,
    outline = BloomColors.Outline,
    error = BloomColors.Error,
    onError = Color.White,
)

private val BloomDarkScheme = darkColorScheme(
    primary = BloomColors.Primary,
    onPrimary = BloomColors.DarkTextPrimary,
    primaryContainer = BloomColors.DarkSurfaceVariant,
    onPrimaryContainer = BloomColors.DarkTextPrimary,
    secondary = BloomColors.AccentBlue,
    onSecondary = BloomColors.DarkBackground,
    background = BloomColors.DarkBackground,
    onBackground = BloomColors.DarkTextPrimary,
    surface = BloomColors.DarkSurface,
    onSurface = BloomColors.DarkTextPrimary,
    surfaceVariant = BloomColors.DarkSurfaceVariant,
    onSurfaceVariant = BloomColors.DarkTextSecondary,
    outline = Color(0xFF4A574D),
    error = BloomColors.Error,
    onError = Color.White,
)

@Composable
fun BloomTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) BloomDarkScheme else BloomLightScheme,
        typography = BloomTypography,
        content = content,
    )
}

val BloomLightColorScheme: ColorScheme = BloomLightScheme
val BloomDarkColorScheme: ColorScheme = BloomDarkScheme

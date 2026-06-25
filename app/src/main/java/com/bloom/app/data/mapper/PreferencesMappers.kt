package com.bloom.app.data.mapper

import com.bloom.app.domain.model.ThemeMode
import com.bloom.app.domain.model.UserPreferences

fun UserPreferences.toThemeModeName(): String = themeMode.name

fun String.toThemeModeOrDefault(): ThemeMode = runCatching {
    ThemeMode.valueOf(this)
}.getOrDefault(ThemeMode.SYSTEM)

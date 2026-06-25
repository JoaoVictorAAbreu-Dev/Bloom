package com.bloom.app.domain.model

enum class PomodoroMode(val label: String, val isBreak: Boolean) {
    FOCUS("Focus", false),
    SHORT_BREAK("Short Break", true),
    LONG_BREAK("Long Break", true);
}

package com.bloom.app.domain.model

enum class HabitCategory(val label: String) {
    HEALTH("Health"),
    MIND("Mind"),
    WORK("Work"),
    WELLBEING("Wellbeing"),
    HOME("Home");

    companion object {
        val defaultCategories = listOf(HEALTH, MIND, WORK)
    }
}

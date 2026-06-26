package com.bloom.app.data.mapper

import com.bloom.app.data.entity.HabitCompletionEntity
import com.bloom.app.data.entity.HabitEntity
import com.bloom.app.data.security.FieldCipher
import com.bloom.app.domain.model.Habit
import com.bloom.app.domain.model.HabitCategory
import com.bloom.app.domain.model.HabitFrequency

fun HabitEntity.toDomain(
    streak: Int,
    completedToday: Boolean,
    completionCount: Int,
    fieldCipher: FieldCipher,
): Habit {
    return Habit(
        id = id,
        name = fieldCipher.decrypt(name),
        category = HabitCategory.valueOf(category),
        frequency = HabitFrequency.valueOf(frequency),
        reminderHour = reminderHour,
        reminderMinute = reminderMinute,
        colorArgb = colorArgb,
        iconKey = iconKey,
        priority = priority,
        emoji = emoji,
        dailyGoal = dailyGoal,
        weeklyGoal = weeklyGoal,
        customRepeat = fieldCipher.decrypt(customRepeat),
        createdAtMillis = createdAtMillis,
        streak = streak,
        completedToday = completedToday,
        completionCount = completionCount,
    )
}

fun Habit.toEntity(fieldCipher: FieldCipher): HabitEntity {
    return HabitEntity(
        id = id,
        name = fieldCipher.encrypt(name),
        category = category.name,
        frequency = frequency.name,
        reminderHour = reminderHour,
        reminderMinute = reminderMinute,
        colorArgb = colorArgb,
        iconKey = iconKey,
        priority = priority,
        emoji = emoji,
        dailyGoal = dailyGoal,
        weeklyGoal = weeklyGoal,
        customRepeat = fieldCipher.encrypt(customRepeat),
        createdAtMillis = createdAtMillis,
    )
}

fun HabitCompletionEntity.toDomain() = com.bloom.app.domain.model.HabitCompletion(
    id = id,
    habitId = habitId,
    completedAtMillis = completedAtMillis,
    dayStartMillis = dayStartMillis,
)

fun com.bloom.app.domain.model.HabitCompletion.toEntity() = HabitCompletionEntity(
    id = id,
    habitId = habitId,
    completedAtMillis = completedAtMillis,
    dayStartMillis = dayStartMillis,
)

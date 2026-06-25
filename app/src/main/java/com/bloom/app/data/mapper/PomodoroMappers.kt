package com.bloom.app.data.mapper

import com.bloom.app.data.entity.PomodoroSessionEntity
import com.bloom.app.domain.model.PomodoroMode
import com.bloom.app.domain.model.PomodoroSession

fun PomodoroSessionEntity.toDomain() = PomodoroSession(
    id = id,
    mode = PomodoroMode.valueOf(mode),
    durationMinutes = durationMinutes,
    startedAtMillis = startedAtMillis,
    finishedAtMillis = finishedAtMillis,
    completed = completed,
)

fun PomodoroSession.toEntity() = PomodoroSessionEntity(
    id = id,
    mode = mode.name,
    durationMinutes = durationMinutes,
    startedAtMillis = startedAtMillis,
    finishedAtMillis = finishedAtMillis,
    completed = completed,
)

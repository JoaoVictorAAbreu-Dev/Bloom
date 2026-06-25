package com.bloom.app.domain.usecase

import com.bloom.app.domain.repository.PomodoroRepository

class ObservePomodoroSessionsUseCase(private val pomodoroRepository: PomodoroRepository) {
    operator fun invoke() = pomodoroRepository.observeSessions()
}

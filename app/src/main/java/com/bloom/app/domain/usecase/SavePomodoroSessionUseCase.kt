package com.bloom.app.domain.usecase

import com.bloom.app.domain.model.PomodoroSession
import com.bloom.app.domain.repository.PomodoroRepository

class SavePomodoroSessionUseCase(private val pomodoroRepository: PomodoroRepository) {
    suspend operator fun invoke(session: PomodoroSession) = pomodoroRepository.saveSession(session)
}

package com.bloom.app.domain.usecase

import com.bloom.app.domain.repository.PreferencesRepository

class CompleteOnboardingUseCase(private val preferencesRepository: PreferencesRepository) {
    suspend operator fun invoke() = preferencesRepository.completeOnboarding()
}

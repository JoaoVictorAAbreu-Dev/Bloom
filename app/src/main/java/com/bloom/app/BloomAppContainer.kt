package com.bloom.app

import android.content.Context
import com.bloom.app.data.local.BloomDatabase
import com.bloom.app.data.remote.GroqAiGateway
import com.bloom.app.data.remote.GroqAiService
import com.bloom.app.data.remote.RemoteBackendAiGateway
import com.bloom.app.data.repository.AiCoachRepositoryImpl
import com.bloom.app.data.repository.HabitRepositoryImpl
import com.bloom.app.data.repository.KeystoreSecurePreferencesRepository
import com.bloom.app.data.repository.PomodoroRepositoryImpl
import com.bloom.app.data.repository.PreferencesRepositoryImpl
import com.bloom.app.data.repository.RewardRepositoryImpl
import com.bloom.app.data.repository.StatisticsRepositoryImpl
import com.bloom.app.data.security.KeystoreFieldCipher
import com.bloom.app.domain.repository.AiCoachRepository
import com.bloom.app.domain.repository.HabitRepository
import com.bloom.app.domain.repository.PomodoroRepository
import com.bloom.app.domain.repository.PreferencesRepository
import com.bloom.app.domain.repository.RewardRepository
import com.bloom.app.domain.repository.SecurePreferencesRepository
import com.bloom.app.domain.repository.StatisticsRepository
import com.bloom.app.domain.usecase.BuildAiCoachPromptUseCase
import com.bloom.app.domain.usecase.CalculateHabitStreakUseCase
import com.bloom.app.domain.usecase.DeleteHabitUseCase
import com.bloom.app.domain.usecase.GenerateAiCoachReplyUseCase
import com.bloom.app.domain.usecase.ObserveHabitsByCategoryUseCase
import com.bloom.app.domain.usecase.ObserveHabitsUseCase
import com.bloom.app.domain.usecase.ObserveHabitUseCase
import com.bloom.app.domain.usecase.ObservePomodoroSessionsUseCase
import com.bloom.app.domain.usecase.ObservePreferencesUseCase
import com.bloom.app.domain.usecase.ObserveRewardsUseCase
import com.bloom.app.domain.usecase.ObserveStatisticsUseCase
import com.bloom.app.domain.usecase.ResetAllDataUseCase
import com.bloom.app.domain.usecase.SavePomodoroSessionUseCase
import com.bloom.app.domain.usecase.SeedDemoContentUseCase
import com.bloom.app.domain.usecase.ToggleHabitCompletionUseCase
import com.bloom.app.domain.usecase.UpdateThemeModeUseCase
import com.bloom.app.domain.usecase.UpdatePreferencesUseCase
import com.bloom.app.domain.usecase.UpsertHabitUseCase

class BloomAppContainer(context: Context) {
    private val database = BloomDatabase.getInstance(context)

    private val calculateHabitStreakUseCase = CalculateHabitStreakUseCase()

    val habitRepository: HabitRepository = HabitRepositoryImpl(
        habitDao = database.habitDao(),
        habitCompletionDao = database.habitCompletionDao(),
        calculateHabitStreakUseCase = calculateHabitStreakUseCase,
        fieldCipher = KeystoreFieldCipher(),
    )
    val pomodoroRepository: PomodoroRepository = PomodoroRepositoryImpl(database.pomodoroSessionDao())
    val preferencesRepository: PreferencesRepository = PreferencesRepositoryImpl(context)
    val securePreferencesRepository: SecurePreferencesRepository = KeystoreSecurePreferencesRepository(context)
    val statisticsRepository: StatisticsRepository = StatisticsRepositoryImpl(
        habitRepository = habitRepository,
        pomodoroRepository = pomodoroRepository,
        habitCompletionDao = database.habitCompletionDao(),
    )
    val rewardRepository: RewardRepository = RewardRepositoryImpl()
    val aiCoachRepository: AiCoachRepository = AiCoachRepositoryImpl(
        aiGateway = if (BuildConfig.AI_BACKEND_BASE_URL.trim().startsWith("https://")) {
            RemoteBackendAiGateway(BuildConfig.AI_BACKEND_BASE_URL.trim())
        } else {
            GroqAiGateway(GroqAiService())
        },
        buildAiCoachPromptUseCase = BuildAiCoachPromptUseCase(),
    )

    val observeHabitsUseCase = ObserveHabitsUseCase(habitRepository)
    val observeHabitsByCategoryUseCase = ObserveHabitsByCategoryUseCase(habitRepository)
    val observeHabitUseCase = ObserveHabitUseCase(habitRepository)
    val upsertHabitUseCase = UpsertHabitUseCase(habitRepository)
    val deleteHabitUseCase = DeleteHabitUseCase(habitRepository)
    val toggleHabitCompletionUseCase = ToggleHabitCompletionUseCase(habitRepository)
    val observePreferencesUseCase = ObservePreferencesUseCase(preferencesRepository)
    val updatePreferencesUseCase = UpdatePreferencesUseCase(preferencesRepository)
    val updateThemeModeUseCase = UpdateThemeModeUseCase(preferencesRepository)
    val observePomodoroSessionsUseCase = ObservePomodoroSessionsUseCase(pomodoroRepository)
    val savePomodoroSessionUseCase = SavePomodoroSessionUseCase(pomodoroRepository)
    val observeStatisticsUseCase = ObserveStatisticsUseCase(statisticsRepository)
    val observeRewardsUseCase = ObserveRewardsUseCase(rewardRepository)
    val resetAllDataUseCase = ResetAllDataUseCase(habitRepository, pomodoroRepository, preferencesRepository)
    val seedDemoContentUseCase = SeedDemoContentUseCase(habitRepository, preferencesRepository)
    val generateAiCoachReplyUseCase = GenerateAiCoachReplyUseCase(aiCoachRepository)
}

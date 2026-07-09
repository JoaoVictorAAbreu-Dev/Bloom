package com.bloom.app.ui.navigation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bloom.app.BloomAppContainer
import com.bloom.app.domain.model.ThemeMode
import com.bloom.app.ui.components.BloomBottomBar
import com.bloom.app.ui.components.bloomBottomBarItems
import com.bloom.app.ui.screens.auth.AuthScreen
import com.bloom.app.ui.screens.coach.CoachScreen
import com.bloom.app.ui.screens.focus.FocusScreen
import com.bloom.app.ui.screens.garden.GardenScreen
import com.bloom.app.ui.screens.home.HomeScreen
import com.bloom.app.ui.screens.home.RoutineScreen
import com.bloom.app.ui.screens.habits.HabitEditorScreen
import com.bloom.app.ui.screens.habits.HabitsScreen
import com.bloom.app.ui.screens.onboarding.OnboardingScreen
import com.bloom.app.ui.screens.settings.ProfileScreen
import com.bloom.app.ui.screens.settings.SettingsScreen
import com.bloom.app.ui.screens.splash.SplashScreen
import com.bloom.app.ui.screens.statistics.StatisticsScreen
import com.bloom.app.ui.state.focusViewModelFactory
import com.bloom.app.ui.state.coachViewModelFactory
import com.bloom.app.ui.state.gardenViewModelFactory
import com.bloom.app.ui.state.habitEditorViewModelFactory
import com.bloom.app.ui.state.habitsViewModelFactory
import com.bloom.app.ui.state.homeViewModelFactory
import com.bloom.app.ui.state.rootViewModelFactory
import com.bloom.app.ui.state.routineViewModelFactory
import com.bloom.app.ui.state.settingsViewModelFactory
import com.bloom.app.ui.state.statisticsViewModelFactory
import com.bloom.app.ui.state.RootViewModel
import com.bloom.app.ui.theme.BloomTheme
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

@Composable
fun BloomApp(container: BloomAppContainer) {
    val rootViewModel: RootViewModel = viewModel(factory = rootViewModelFactory(container))
    val rootState by rootViewModel.uiState.collectAsStateWithLifecycle()

    val darkTheme = when (rootState.preferences.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    BloomTheme(darkTheme = darkTheme) {
        val navController = rememberNavController()
        BloomNavigation(
            navController = navController,
            container = container,
            rootViewModel = rootViewModel,
        )
    }
}

@Composable
private fun BloomNavigation(
    navController: NavHostController,
    container: BloomAppContainer,
    rootViewModel: RootViewModel,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: BloomDestination.SPLASH
    val bottomBarRoutes = setOf(
        BloomDestination.HOME,
        BloomDestination.HABITS,
        BloomDestination.FOCUS,
        BloomDestination.COACH,
        BloomDestination.STATS,
        BloomDestination.PROFILE,
    )
    val openSettings: () -> Unit = { navController.navigate(BloomDestination.SETTINGS) }

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                BloomBottomBar(
                    items = bloomBottomBarItems(),
                    currentRoute = currentRoute,
                    onItemSelected = { route ->
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(BloomDestination.HOME) { saveState = true }
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BloomDestination.SPLASH,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            composable(BloomDestination.SPLASH) {
                SplashScreen(
                    onFinished = {
                        val preferences = rootViewModel.uiState.value.preferences
                        val destination = when {
                            !preferences.onboardingCompleted -> BloomDestination.ONBOARDING
                            !preferences.authCompleted -> BloomDestination.AUTH
                            else -> BloomDestination.HOME
                        }
                        navController.navigate(destination) {
                            popUpTo(BloomDestination.SPLASH) { inclusive = true }
                        }
                    },
                )
            }
            composable(BloomDestination.ONBOARDING) {
                OnboardingScreen(
                    onStart = { setup ->
                        rootViewModel.completeOnboarding(setup)
                        navController.navigate(BloomDestination.AUTH) {
                            popUpTo(BloomDestination.ONBOARDING) { inclusive = true }
                        }
                    },
                )
            }
            composable(BloomDestination.AUTH) {
                val preferences = rootViewModel.uiState.value.preferences
                AuthScreen(
                    defaultName = preferences.userName,
                    onAuthenticated = { name, email ->
                        rootViewModel.completeAuth(name, email)
                        navController.navigate(BloomDestination.HOME) {
                            popUpTo(BloomDestination.AUTH) { inclusive = true }
                        }
                    },
                )
            }
            composable(BloomDestination.HOME) {
                val viewModel: com.bloom.app.ui.state.HomeViewModel = viewModel(factory = homeViewModelFactory(container))
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                HomeScreen(
                    uiState = uiState,
                    onHabitToggle = viewModel::toggleHabitCompletion,
                    onOpenHabits = { navController.navigate(BloomDestination.HABITS) },
                    onOpenFocus = { navController.navigate(BloomDestination.FOCUS) },
                    onOpenRoutine = { navController.navigate(BloomDestination.ROUTINE) },
                    onOpenGarden = { navController.navigate(BloomDestination.GARDEN) },
                    onOpenCoach = { navController.navigate(BloomDestination.COACH) },
                    onNotificationsClick = openSettings,
                )
            }
            composable(BloomDestination.HABITS) {
                val viewModel: com.bloom.app.ui.state.HabitsViewModel = viewModel(factory = habitsViewModelFactory(container))
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                HabitsScreen(
                    uiState = uiState,
                    onCategorySelected = viewModel::selectCategory,
                    onHabitToggle = viewModel::toggleCompletion,
                    onHabitClick = { habitId -> navController.navigate(BloomDestination.habitEditorRoute(habitId)) },
                    onAddHabit = { navController.navigate(BloomDestination.habitEditorRoute()) },
                    onNotificationsClick = openSettings,
                )
            }
            composable(BloomDestination.FOCUS) {
                val viewModel: com.bloom.app.ui.state.FocusViewModel = viewModel(factory = focusViewModelFactory(container))
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                FocusScreen(
                    uiState = uiState,
                    onStart = viewModel::start,
                    onPause = viewModel::pause,
                    onResume = viewModel::resume,
                    onStop = viewModel::stop,
                    onDeepFocusToggle = viewModel::toggleDeepFocus,
                    onNotificationsClick = openSettings,
                )
            }
            composable(BloomDestination.STATS) {
                val viewModel: com.bloom.app.ui.state.StatisticsViewModel = viewModel(factory = statisticsViewModelFactory(container))
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                StatisticsScreen(
                    uiState = uiState,
                    onNotificationsClick = openSettings,
                )
            }
            composable(BloomDestination.PROFILE) {
                val viewModel: com.bloom.app.ui.state.SettingsViewModel = viewModel(factory = settingsViewModelFactory(container))
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                ProfileScreen(
                    uiState = uiState,
                    onOpenGarden = { navController.navigate(BloomDestination.GARDEN) },
                    onOpenSettings = { navController.navigate(BloomDestination.SETTINGS) },
                    onNotificationsClick = openSettings,
                )
            }
            composable(BloomDestination.ROUTINE) {
                val viewModel: com.bloom.app.ui.state.RoutineViewModel = viewModel(factory = routineViewModelFactory(container))
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                RoutineScreen(
                    uiState = uiState,
                    onNotificationsClick = openSettings,
                )
            }
            composable(BloomDestination.GARDEN) {
                val viewModel: com.bloom.app.ui.state.GardenViewModel = viewModel(factory = gardenViewModelFactory(container))
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                GardenScreen(
                    uiState = uiState,
                    onNotificationsClick = openSettings,
                )
            }
            composable(BloomDestination.SETTINGS) {
                val viewModel: com.bloom.app.ui.state.SettingsViewModel = viewModel(factory = settingsViewModelFactory(container))
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val context = LocalContext.current
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                ) { granted ->
                    viewModel.toggleNotifications(granted)
                }
                val importExportLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocument(),
                ) { uri ->
                    if (uri != null) {
                        context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { reader ->
                            reader.readText()
                        }?.let { snapshot ->
                            viewModel.importData(snapshot)
                        }
                    }
                }
                val saveExportLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.CreateDocument("application/json"),
                ) { uri ->
                    val snapshot = uiState.exportSnapshot
                    if (uri != null && snapshot.isNotBlank()) {
                        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                            OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use { writer ->
                                writer.write(snapshot)
                            }
                        }
                    }
                }
                SettingsScreen(
                    uiState = uiState,
                    onNameChange = viewModel::updateName,
                    onThemeChange = viewModel::updateThemeMode,
                    onFocusMinutesChange = viewModel::updateFocusMinutes,
                    onShortBreakMinutesChange = viewModel::updateShortBreakMinutes,
                    onLongBreakMinutesChange = viewModel::updateLongBreakMinutes,
                    onNotificationsToggle = { enabled ->
                        if (!enabled) {
                            viewModel.toggleNotifications(false)
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val granted = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS,
                            ) == PackageManager.PERMISSION_GRANTED
                            if (granted) {
                                viewModel.toggleNotifications(true)
                            } else {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        } else {
                            viewModel.toggleNotifications(true)
                        }
                    },
                    onAutoStartToggle = viewModel::toggleAutoStart,
                    onBloomCoachToggle = viewModel::toggleBloomCoach,
                    onHabitContextForAiToggle = viewModel::toggleHabitContextForAi,
                    onExportData = viewModel::exportData,
                    onSaveExport = { snapshot ->
                        if (snapshot.isNotBlank()) {
                            saveExportLauncher.launch("bloom-export.json")
                        }
                    },
                    onImportExport = {
                        importExportLauncher.launch(arrayOf("application/json", "text/json", "text/plain"))
                    },
                    onShareExport = { snapshot ->
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/json"
                            putExtra(Intent.EXTRA_SUBJECT, "Bloom export")
                            putExtra(Intent.EXTRA_TEXT, snapshot)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Bloom export"))
                    },
                    onClearExport = viewModel::clearExport,
                    onResetData = viewModel::resetData,
                    onOpenCoach = { navController.navigate(BloomDestination.COACH) },
                    onNotificationsClick = openSettings,
                )
            }
            composable(BloomDestination.COACH) {
                val viewModel: com.bloom.app.ui.state.CoachViewModel = viewModel(factory = coachViewModelFactory(container))
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                CoachScreen(
                    uiState = uiState,
                    onInputChange = viewModel::updateInput,
                    onSend = viewModel::send,
                    onQuickAction = { prompt ->
                        viewModel.updateInput(prompt)
                        viewModel.send(prompt)
                    },
                    onNotificationsClick = openSettings,
                )
            }
            composable(BloomDestination.HABIT_EDITOR) {
                val viewModel: com.bloom.app.ui.state.HabitEditorViewModel = viewModel(factory = habitEditorViewModelFactory(container, null))
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                HabitEditorScreen(
                    uiState = uiState,
                    onBack = { navController.popBackStack() },
                    onNameChange = viewModel::updateName,
                    onCategoryChange = viewModel::updateCategory,
                    onFrequencyChange = viewModel::updateFrequency,
                    onReminderHourChange = { viewModel.updateReminder(it, uiState.reminderMinute) },
                    onReminderMinuteChange = { viewModel.updateReminder(uiState.reminderHour, it) },
                    onColorChange = viewModel::updateColor,
                    onIconChange = viewModel::updateIcon,
                    onPriorityChange = viewModel::updatePriority,
                    onEmojiChange = viewModel::updateEmoji,
                    onDailyGoalChange = viewModel::updateDailyGoal,
                    onWeeklyGoalChange = viewModel::updateWeeklyGoal,
                    onCustomRepeatChange = viewModel::updateCustomRepeat,
                    onSave = {
                        viewModel.save { navController.popBackStack() }
                    },
                    onDelete = {
                        viewModel.delete { navController.popBackStack() }
                    },
                )
            }
            composable(
                route = "${BloomDestination.HABIT_EDITOR}/{habitId}",
                arguments = listOf(navArgument("habitId") { type = NavType.StringType }),
            ) { entry ->
                val habitId = entry.arguments?.getString("habitId")
                val viewModel: com.bloom.app.ui.state.HabitEditorViewModel = viewModel(factory = habitEditorViewModelFactory(container, habitId))
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                HabitEditorScreen(
                    uiState = uiState,
                    onBack = { navController.popBackStack() },
                    onNameChange = viewModel::updateName,
                    onCategoryChange = viewModel::updateCategory,
                    onFrequencyChange = viewModel::updateFrequency,
                    onReminderHourChange = { viewModel.updateReminder(it, uiState.reminderMinute) },
                    onReminderMinuteChange = { viewModel.updateReminder(uiState.reminderHour, it) },
                    onColorChange = viewModel::updateColor,
                    onIconChange = viewModel::updateIcon,
                    onPriorityChange = viewModel::updatePriority,
                    onEmojiChange = viewModel::updateEmoji,
                    onDailyGoalChange = viewModel::updateDailyGoal,
                    onWeeklyGoalChange = viewModel::updateWeeklyGoal,
                    onCustomRepeatChange = viewModel::updateCustomRepeat,
                    onSave = {
                        viewModel.save { navController.popBackStack() }
                    },
                    onDelete = {
                        viewModel.delete { navController.popBackStack() }
                    },
                )
            }
        }
    }
}

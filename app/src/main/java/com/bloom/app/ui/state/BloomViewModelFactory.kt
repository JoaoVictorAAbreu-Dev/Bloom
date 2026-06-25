package com.bloom.app.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bloom.app.BloomAppContainer

fun rootViewModelFactory(container: BloomAppContainer): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        RootViewModel(container)
    }
}

fun homeViewModelFactory(container: BloomAppContainer): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        HomeViewModel(container)
    }
}

fun habitsViewModelFactory(container: BloomAppContainer): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        HabitsViewModel(container)
    }
}

fun habitEditorViewModelFactory(
    container: BloomAppContainer,
    habitId: String? = null,
): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        HabitEditorViewModel(container, habitId)
    }
}

fun focusViewModelFactory(container: BloomAppContainer): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        FocusViewModel(container)
    }
}

fun routineViewModelFactory(container: BloomAppContainer): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        RoutineViewModel(container)
    }
}

fun statisticsViewModelFactory(container: BloomAppContainer): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        StatisticsViewModel(container)
    }
}

fun gardenViewModelFactory(container: BloomAppContainer): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        GardenViewModel(container)
    }
}

fun settingsViewModelFactory(container: BloomAppContainer): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        SettingsViewModel(container)
    }
}

fun coachViewModelFactory(container: BloomAppContainer): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        CoachViewModel(container)
    }
}

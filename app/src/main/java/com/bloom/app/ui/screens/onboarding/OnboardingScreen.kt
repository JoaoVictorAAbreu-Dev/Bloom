package com.bloom.app.ui.screens.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import android.content.pm.PackageManager
import com.bloom.app.ui.components.BloomButton
import com.bloom.app.ui.components.BloomCard
import com.bloom.app.ui.components.BloomLogoMark
import com.bloom.app.ui.components.BloomOutlinedButton
import com.bloom.app.ui.components.BloomPixelFlower
import com.bloom.app.ui.components.BloomPixelMascot
import com.bloom.app.ui.components.BloomPixelPlant
import com.bloom.app.ui.components.BloomPixelTree
import com.bloom.app.ui.theme.BloomColors
import com.bloom.app.ui.theme.BloomRadius
import com.bloom.app.ui.theme.BloomSpacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onStart: () -> Unit,
) {
    val pages = remember {
        listOf(
            OnboardingPage.Intro,
            OnboardingPage.Concept,
            OnboardingPage.Notifications,
            OnboardingPage.Goal,
            OnboardingPage.StarterHabits,
            OnboardingPage.Pomodoro,
        )
    }
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var notificationsStatus by remember { mutableStateOf("Optional") }
    var selectedGoal by remember { mutableStateOf("Build consistency") }
    val selectedHabits = remember { mutableStateListOf("Drink water", "Read 10 pages") }
    var focusMinutes by remember { mutableIntStateOf(25) }
    var shortBreakMinutes by remember { mutableIntStateOf(5) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        notificationsStatus = if (granted) "Enabled" else "Skipped"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BloomColors.Background)
            .padding(horizontal = BloomSpacing.screenPadding, vertical = BloomSpacing.xxl),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        OnboardingHeader(
            currentPage = pagerState.currentPage,
            pageCount = pages.size,
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = true,
        ) { page ->
            BloomCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = BloomSpacing.xl),
            ) {
                when (pages[page]) {
                    OnboardingPage.Intro -> IntroStep()
                    OnboardingPage.Concept -> ConceptStep()
                    OnboardingPage.Notifications -> NotificationsStep(
                        status = notificationsStatus,
                        onEnable = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val granted = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS,
                                ) == PackageManager.PERMISSION_GRANTED
                                if (granted) {
                                    notificationsStatus = "Enabled"
                                } else {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            } else {
                                notificationsStatus = "Enabled"
                            }
                        },
                        onSkip = { notificationsStatus = "Skipped" },
                    )
                    OnboardingPage.Goal -> GoalStep(
                        selectedGoal = selectedGoal,
                        onGoalSelected = { selectedGoal = it },
                    )
                    OnboardingPage.StarterHabits -> StarterHabitsStep(
                        selectedHabits = selectedHabits,
                        onHabitToggle = { habit ->
                            if (selectedHabits.contains(habit)) {
                                selectedHabits.remove(habit)
                            } else {
                                selectedHabits.add(habit)
                            }
                        },
                    )
                    OnboardingPage.Pomodoro -> PomodoroSetupStep(
                        focusMinutes = focusMinutes,
                        shortBreakMinutes = shortBreakMinutes,
                        onFocusChange = { focusMinutes = it },
                        onShortBreakChange = { shortBreakMinutes = it },
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
            PageIndicators(
                currentPage = pagerState.currentPage,
                pageCount = pages.size,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.sm)) {
                if (pagerState.currentPage > 0) {
                    BloomOutlinedButton(
                        modifier = Modifier.weight(1f),
                        text = "Back",
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                        },
                    )
                }
                BloomButton(
                    modifier = Modifier.weight(1f),
                    text = if (pagerState.currentPage < pages.lastIndex) "Next" else "Start Bloom",
                    onClick = {
                        if (pagerState.currentPage < pages.lastIndex) {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            onStart()
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun OnboardingHeader(
    currentPage: Int,
    pageCount: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(BloomSpacing.sm),
        ) {
            BloomLogoMark(size = 48.dp)
            Column {
                Text(
                    text = "Bloom",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Grow a little every day.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Text(
            text = "${currentPage + 1}/$pageCount",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun IntroStep() {
    OnboardingScaffold(
        art = { BloomPixelPlant(size = 132.dp) },
        title = "Welcome to Bloom",
        description = "A calmer place to organize habits, routines, focus sessions, and personal growth.",
        helper = "Your progress becomes visible through a garden that grows with consistency.",
    )
}

@Composable
private fun ConceptStep() {
    OnboardingScaffold(
        art = { BloomPixelFlower(size = 120.dp) },
        title = "Small actions compound",
        description = "Bloom is built around one simple idea: grow a little every day.",
        helper = "Complete habits, protect focus time, and review your rhythm without pressure.",
    )
}

@Composable
private fun NotificationsStep(
    status: String,
    onEnable: () -> Unit,
    onSkip: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(BloomSpacing.md),
    ) {
        BloomPixelMascot(size = 120.dp)
        StepTitle(
            title = "Gentle reminders",
            description = "Bloom can remind you when it is time for a habit, a routine block, or a focus session.",
            helper = "You can change this later in Settings.",
        )
        StatusPill(text = status)
        Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.sm)) {
            BloomOutlinedButton(
                modifier = Modifier.weight(1f),
                text = "Skip",
                onClick = onSkip,
            )
            BloomButton(
                modifier = Modifier.weight(1f),
                text = "Enable",
                onClick = onEnable,
            )
        }
    }
}

@Composable
private fun GoalStep(
    selectedGoal: String,
    onGoalSelected: (String) -> Unit,
) {
    val goals = listOf(
        "Build consistency",
        "Focus deeper",
        "Create routine",
        "Grow healthier",
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(BloomSpacing.md),
    ) {
        BloomPixelTree(size = 118.dp)
        StepTitle(
            title = "Choose your main goal",
            description = "This sets the tone for your first routine.",
            helper = "You can still use every part of Bloom.",
        )
        ChipGrid(
            items = goals,
            selectedItems = listOf(selectedGoal),
            onClick = onGoalSelected,
        )
    }
}

@Composable
private fun StarterHabitsStep(
    selectedHabits: List<String>,
    onHabitToggle: (String) -> Unit,
) {
    val habits = listOf(
        "Drink water",
        "Read 10 pages",
        "Meditate",
        "Exercise",
        "Plan tomorrow",
        "Study",
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(BloomSpacing.md),
    ) {
        BloomPixelPlant(size = 124.dp)
        StepTitle(
            title = "Pick starter habits",
            description = "Select a few habits to shape your first Bloom routine.",
            helper = "${selectedHabits.size} selected",
        )
        ChipGrid(
            items = habits,
            selectedItems = selectedHabits,
            onClick = onHabitToggle,
        )
    }
}

@Composable
private fun PomodoroSetupStep(
    focusMinutes: Int,
    shortBreakMinutes: Int,
    onFocusChange: (Int) -> Unit,
    onShortBreakChange: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(BloomSpacing.md),
    ) {
        BloomPixelMascot(size = 116.dp)
        StepTitle(
            title = "Set your first Pomodoro",
            description = "Start with a focus rhythm that feels sustainable.",
            helper = "Default: 25 minutes of focus and 5 minutes of break.",
        )
        SetupSlider(
            label = "Focus",
            value = focusMinutes,
            range = 15f..60f,
            onValueChange = onFocusChange,
        )
        SetupSlider(
            label = "Short break",
            value = shortBreakMinutes,
            range = 3f..15f,
            onValueChange = onShortBreakChange,
        )
    }
}

@Composable
private fun OnboardingScaffold(
    art: @Composable () -> Unit,
    title: String,
    description: String,
    helper: String,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        art()
    }
    StepTitle(
        title = title,
        description = description,
        helper = helper,
    )
}

@Composable
private fun StepTitle(
    title: String,
    description: String,
    helper: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(BloomSpacing.sm),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Text(
            text = helper,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ChipGrid(
    items: List<String>,
    selectedItems: List<String>,
    onClick: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.xs)) {
        items.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.xs)) {
                rowItems.forEach { item ->
                    SelectableChip(
                        label = item,
                        selected = selectedItems.contains(item),
                        onClick = { onClick(item) },
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.width(1.dp))
                }
            }
        }
    }
}

@Composable
private fun SelectableChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            labelColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )
}

@Composable
private fun SetupSlider(
    label: String,
    value: Int,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(BloomSpacing.xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "$value min",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range,
        )
    }
}

@Composable
private fun StatusPill(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = BloomRadius.pill,
            )
            .padding(horizontal = BloomSpacing.md, vertical = BloomSpacing.xs),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun PageIndicators(
    currentPage: Int,
    pageCount: Int,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(BloomSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val selected = index == currentPage
            Box(
                modifier = Modifier
                    .width(if (selected) 22.dp else 10.dp)
                    .height(10.dp)
                    .background(
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = BloomRadius.pill,
                    ),
            )
        }
    }
}

private enum class OnboardingPage {
    Intro,
    Concept,
    Notifications,
    Goal,
    StarterHabits,
    Pomodoro,
}

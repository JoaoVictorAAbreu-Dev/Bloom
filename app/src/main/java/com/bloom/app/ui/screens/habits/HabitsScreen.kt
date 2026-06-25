package com.bloom.app.ui.screens.habits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bloom.app.domain.model.HabitCategory
import com.bloom.app.ui.components.BloomCard
import com.bloom.app.ui.components.BloomHabitCard
import com.bloom.app.ui.components.BloomHeader
import com.bloom.app.ui.state.HabitsUiState
import com.bloom.app.ui.theme.BloomSpacing

@Composable
fun HabitsScreen(
    uiState: HabitsUiState,
    onCategorySelected: (HabitCategory?) -> Unit,
    onHabitToggle: (String) -> Unit,
    onHabitClick: (String) -> Unit,
    onAddHabit: () -> Unit,
    onNotificationsClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = BloomSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(BloomSpacing.cardGap),
        ) {
            item {
                BloomHeader(
                    title = "Bloom",
                    subtitle = "My Habits",
                    onNotificationsClick = onNotificationsClick,
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = BloomSpacing.screenPadding),
                    verticalArrangement = Arrangement.spacedBy(BloomSpacing.xs),
                ) {
                    Text(
                        text = "My Habits",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "Cultivate your daily routines.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            item {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
                    horizontalArrangement = Arrangement.spacedBy(BloomSpacing.xs),
                ) {
                    HabitFilters(
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = onCategorySelected,
                    )
                }
            }
            if (uiState.habits.isEmpty()) {
                item {
                    BloomCardEmptyState(
                        modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
                    )
                }
            } else {
                items(uiState.habits, key = { it.id }) { habit ->
                    BloomHabitCard(
                        habit = habit,
                        modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
                        onClick = { onHabitClick(habit.id) },
                        onToggle = { onHabitToggle(habit.id) },
                    )
                }
            }
            item {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(88.dp))
            }
        }

        FloatingActionButton(
            onClick = onAddHabit,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = BloomSpacing.screenPadding, bottom = BloomSpacing.xxl),
        ) {
            Icon(imageVector = Icons.Rounded.Add, contentDescription = "Create habit")
        }
    }
}

@Composable
private fun HabitFilters(
    selectedCategory: HabitCategory?,
    onCategorySelected: (HabitCategory?) -> Unit,
) {
    val options = listOf(null) + HabitCategory.defaultCategories
    androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.xs)) {
        options.forEach { category ->
            val label = category?.label ?: "All"
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        }
    }
}

@Composable
private fun BloomCardEmptyState(
    modifier: Modifier = Modifier,
) {
    BloomCard(modifier = modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No habits yet",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Add your first habit to start cultivating the garden.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

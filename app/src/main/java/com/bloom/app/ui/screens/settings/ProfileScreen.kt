package com.bloom.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bloom.app.ui.components.BloomButton
import com.bloom.app.ui.components.BloomCard
import com.bloom.app.ui.components.BloomHeader
import com.bloom.app.ui.components.BloomPixelMascot
import com.bloom.app.ui.components.BloomOutlinedButton
import com.bloom.app.ui.state.SettingsUiState
import com.bloom.app.ui.theme.BloomSpacing

@Composable
fun ProfileScreen(
    uiState: SettingsUiState,
    onOpenGarden: () -> Unit,
    onOpenSettings: () -> Unit,
    onNotificationsClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = BloomSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(BloomSpacing.cardGap),
    ) {
        BloomHeader(
            title = "Bloom",
            subtitle = "Profile",
            onNotificationsClick = onNotificationsClick,
        )

        Column(
            modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(BloomSpacing.xs),
        ) {
            Text(
                text = uiState.preferences.userName,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "A calm, growing routine.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        BloomCard(modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(BloomSpacing.lg),
            ) {
                BloomPixelMascot(size = 112.dp)
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(BloomSpacing.xs)) {
                    Text(text = "Grow a little every day.", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = "Theme: ${uiState.preferences.themeMode.name.lowercase().replaceFirstChar(Char::uppercaseChar)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Pomodoro: ${uiState.preferences.focusMinutes}/${uiState.preferences.shortBreakMinutes}/${uiState.preferences.longBreakMinutes}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(BloomSpacing.sm),
        ) {
            BloomButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Open Garden",
                onClick = onOpenGarden,
            )
            BloomOutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Settings",
                onClick = onOpenSettings,
            )
        }
    }
}

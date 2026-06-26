package com.bloom.app.ui.screens.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.bloom.app.domain.model.AiCoachSource
import com.bloom.app.ui.components.BloomAiBadge
import com.bloom.app.ui.components.BloomButton
import com.bloom.app.ui.components.BloomCard
import com.bloom.app.ui.components.BloomHeader
import com.bloom.app.ui.components.BloomOutlinedButton
import com.bloom.app.ui.state.CoachMessageRole
import com.bloom.app.ui.state.CoachUiState
import com.bloom.app.ui.theme.BloomSpacing

@Composable
fun CoachScreen(
    uiState: CoachUiState,
    onInputChange: (String) -> Unit,
    onSend: (String?) -> Unit,
    onQuickAction: (String) -> Unit,
    onNotificationsClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(BloomSpacing.cardGap),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = BloomSpacing.xl),
    ) {
        item {
            BloomHeader(
                title = "Bloom",
                subtitle = "Coach",
                onNotificationsClick = onNotificationsClick,
            )
        }

        item {
            CoachStatusCard(uiState = uiState)
        }

        item {
            CoachContextCard(uiState = uiState)
        }

        item {
            CoachAnalysisCard(uiState = uiState)
        }

        item {
            QuickActionsRow(
                uiState = uiState,
                onQuickAction = onQuickAction,
            )
        }

        items(uiState.messages, key = { it.id }) { message ->
            BloomCard(
                modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (message.role == CoachMessageRole.USER) Alignment.End else Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(BloomSpacing.xs),
                ) {
                    Text(
                        text = if (message.role == CoachMessageRole.USER) "You" else "Bloom Coach",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (message.source != null) {
                        Text(
                            text = message.source.label(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }

        item {
            CoachInputCard(
                uiState = uiState,
                onInputChange = onInputChange,
                onSend = onSend,
            )
        }
    }
}

private fun AiCoachSource.label(): String {
    return when (this) {
        AiCoachSource.REMOTE -> "Reply via secure backend"
        AiCoachSource.GROQ -> "Reply via Groq"
        AiCoachSource.LOCAL -> "Local reply"
    }
}

@Composable
private fun CoachStatusCard(uiState: CoachUiState) {
    BloomCard(
        modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(BloomSpacing.md),
        ) {
            BloomAiBadge()
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bloom Coach",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = if (uiState.integration.configured) {
                        "Groq connected. Model ${uiState.integration.modelId}."
                    } else {
                        "Local mode active. Configure a secure AI backend or a debug Groq key to use remote coaching."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun CoachContextCard(uiState: CoachUiState) {
    BloomCard(
        modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.sm)) {
            Text(
                text = "How it helps",
                style = MaterialTheme.typography.titleLarge,
            )
            uiState.contextSummary.forEach { summary ->
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun CoachAnalysisCard(uiState: CoachUiState) {
    BloomCard(
        modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
            Text(
                text = "Actionable Analysis",
                style = MaterialTheme.typography.titleLarge,
            )
            AnalysisRow(title = "This week", value = uiState.weeklySummary)
            AnalysisRow(title = "Last 28 days", value = uiState.monthlySummary)
            AnalysisRow(title = "Next best action", value = uiState.nextBestAction)
            Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.xs)) {
                Text(
                    text = "Recommendations",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                uiState.recommendations.forEach { recommendation ->
                    Text(
                        text = "- $recommendation",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalysisRow(
    title: String,
    value: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.xxs)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun QuickActionsRow(
    uiState: CoachUiState,
    onQuickAction: (String) -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(BloomSpacing.sm),
    ) {
        Text(
            text = "Quick actions",
            style = MaterialTheme.typography.titleLarge,
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(BloomSpacing.sm),
        ) {
            uiState.quickActions.forEach { action ->
                AssistChip(
                    onClick = { onQuickAction(action.prompt) },
                    label = { Text(action.label) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        }
    }
}

@Composable
private fun CoachInputCard(
    uiState: CoachUiState,
    onInputChange: (String) -> Unit,
    onSend: (String?) -> Unit,
) {
    BloomCard(
        modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
            Text(
                text = "Ask Bloom Coach",
                style = MaterialTheme.typography.titleLarge,
            )
            OutlinedTextField(
                value = uiState.input,
                onValueChange = onInputChange,
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                label = { Text("Plan, review, or ask for guidance") },
            )
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.sm)) {
                BloomButton(
                    modifier = Modifier.weight(1f),
                    text = if (uiState.sending) "Sending..." else "Send",
                    enabled = !uiState.sending,
                    onClick = { onSend(null) },
                )
                BloomOutlinedButton(
                    modifier = Modifier.weight(1f),
                    text = "Clear",
                    onClick = { onInputChange("") },
                )
            }
        }
    }
}

package com.bloom.app.ui.screens.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
                                "Groq conectado. Modelo ${uiState.integration.modelId}."
                            } else {
                                "Modo local ativo. Adicione groqApiKey em local.properties para usar Groq."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        item {
            BloomCard(
                modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.sm)) {
                    Text(
                        text = "Como ela ajuda",
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

        item {
            Column(
                modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
                verticalArrangement = Arrangement.spacedBy(BloomSpacing.sm),
            ) {
                Text(
                    text = "Ações rápidas",
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
                        text = if (message.role == CoachMessageRole.USER) "Você" else "Bloom Coach",
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
                            text = if (message.source == com.bloom.app.domain.model.AiCoachSource.GROQ) {
                                "Resposta via Groq"
                            } else {
                                "Resposta local"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }

        item {
            BloomCard(
                modifier = Modifier.padding(horizontal = BloomSpacing.screenPadding),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
                    Text(
                        text = "Pergunte à Bloom Coach",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    OutlinedTextField(
                        value = uiState.input,
                        onValueChange = onInputChange,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        label = { Text("Planeje, revise ou peça orientação") },
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
                            text = if (uiState.sending) "Enviando..." else "Enviar",
                            enabled = !uiState.sending,
                            onClick = { onSend(null) },
                        )
                        BloomOutlinedButton(
                            modifier = Modifier.weight(1f),
                            text = "Limpar",
                            onClick = { onInputChange("") },
                        )
                    }
                }
            }
        }
    }
}

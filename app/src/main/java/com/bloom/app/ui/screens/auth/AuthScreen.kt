package com.bloom.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.bloom.app.R
import com.bloom.app.ui.components.BloomButton
import com.bloom.app.ui.components.BloomCard
import com.bloom.app.ui.components.BloomLogoMark
import com.bloom.app.ui.components.BloomOutlinedButton
import com.bloom.app.ui.theme.BloomColors
import com.bloom.app.ui.theme.BloomSpacing

@Composable
fun AuthScreen(
    defaultName: String,
    onAuthenticated: (String, String) -> Unit,
) {
    var mode by remember { mutableStateOf(AuthMode.SignIn) }
    var name by remember { mutableStateOf(defaultName) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("Everything works locally for now.") }
    val createAccountLabel = stringResource(R.string.auth_create_local_account)
    val continueLabel = stringResource(R.string.auth_continue)
    val continueWithoutAccountLabel = stringResource(R.string.auth_continue_without_account)
    val forgotPasswordLabel = stringResource(R.string.auth_forgot_password)
    val recoveryMessage = stringResource(R.string.auth_recovery_message)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BloomColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = BloomSpacing.screenPadding, vertical = BloomSpacing.xxl),
        verticalArrangement = Arrangement.spacedBy(BloomSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BloomLogoMark(size = 160.dp)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.auth_title),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = stringResource(R.string.auth_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        BloomCard {
            Column(verticalArrangement = Arrangement.spacedBy(BloomSpacing.md)) {
                Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.sm)) {
                    BloomOutlinedButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.auth_login),
                        onClick = { mode = AuthMode.SignIn },
                    )
                    BloomOutlinedButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.auth_signup),
                        onClick = { mode = AuthMode.SignUp },
                    )
                }

                if (mode == AuthMode.SignUp) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.auth_name)) },
                        singleLine = true,
                    )
                }
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.auth_email)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.auth_password)) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                BloomButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = if (mode == AuthMode.SignUp) {
                        createAccountLabel
                    } else {
                        continueLabel
                    },
                    onClick = {
                        val resolvedName = name.ifBlank { email.substringBefore("@").ifBlank { defaultName } }
                        onAuthenticated(resolvedName, email)
                    },
                )
                BloomOutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = continueWithoutAccountLabel,
                    onClick = { onAuthenticated(defaultName, "") },
                )
                BloomOutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = forgotPasswordLabel,
                    onClick = { message = recoveryMessage },
                )
            }
        }
    }
}

private enum class AuthMode {
    SignIn,
    SignUp,
}

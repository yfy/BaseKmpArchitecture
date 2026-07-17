package com.yfy.kmp.android.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.yfy.kmp.android.AndroidIcons
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yfy.kmp.android.ui.AppWebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yfy.kmp.android.R
import com.yfy.kmp.core.designsystem.components.AppButton
import com.yfy.kmp.core.designsystem.components.AppErrorText
import com.yfy.kmp.core.designsystem.components.AppPasswordField
import com.yfy.kmp.core.designsystem.components.AppTextField
import com.yfy.kmp.core.designsystem.theme.LocalAppExtraColors
import com.yfy.kmp.feature.auth.presentation.SignupUiError
import com.yfy.kmp.feature.auth.presentation.SignupViewModel

@Composable
fun SignupScreen(
    viewModel: SignupViewModel,
    onBack: () -> Unit,
    onSignedUp: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val extra = LocalAppExtraColors.current
    var legalDoc by remember { mutableStateOf<LegalDoc?>(null) }

    LaunchedEffect(state.user) { if (state.user != null) onSignedUp() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(extra.gradientTop, extra.gradientBottom))),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                IconButton(onClick = onBack) {
                    Icon(AndroidIcons.Back, contentDescription = stringResource(com.yfy.kmp.core.designsystem.R.string.a11y_back))
                }
            }
            Text(
                stringResource(R.string.signup_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )

            AppTextField(state.firstName, viewModel::onFirstNameChange, stringResource(R.string.field_first_name))
            Spacer(Modifier.height(12.dp))
            AppTextField(state.lastName, viewModel::onLastNameChange, stringResource(R.string.field_last_name))
            Spacer(Modifier.height(12.dp))
            AppTextField(state.email, viewModel::onEmailChange, stringResource(R.string.field_email), keyboardType = KeyboardType.Email)
            Spacer(Modifier.height(12.dp))
            AppPasswordField(state.password, viewModel::onPasswordChange, stringResource(R.string.field_password))
            Spacer(Modifier.height(12.dp))
            AppPasswordField(state.confirmPassword, viewModel::onConfirmPasswordChange, stringResource(R.string.field_password_confirm))

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(checked = state.termsAccepted, onCheckedChange = viewModel::onTermsAcceptedChange)
                Text(stringResource(R.string.terms_accept_prefix))
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 12.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = { legalDoc = LegalDoc.TERMS }, contentPadding = PaddingValues(horizontal = 4.dp)) {
                    Text(
                        stringResource(R.string.terms_of_service),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Text(stringResource(R.string.terms_and))
                TextButton(onClick = { legalDoc = LegalDoc.PRIVACY }, contentPadding = PaddingValues(horizontal = 4.dp)) {
                    Text(
                        stringResource(R.string.privacy_policy),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            state.error?.let { AppErrorText(it.toMessage(), modifier = Modifier.padding(bottom = 4.dp)) }

            AppButton(
                text = stringResource(R.string.signup_submit),
                onClick = viewModel::signup,
                enabled = state.termsAccepted,
                loading = state.isLoading,
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.signup_have_account))
                TextButton(onClick = onBack) {
                    Text(stringResource(R.string.login_link), color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        legalDoc?.let { doc ->
            val titleRes = if (doc == LegalDoc.TERMS) R.string.terms_of_service else R.string.privacy_policy
            // TODO(template): replace before release — legal document URLs.
            val url = if (doc == LegalDoc.TERMS) "https://example.com/terms" else "https://example.com/privacy"
            Dialog(
                onDismissRequest = { legalDoc = null },
                properties = DialogProperties(usePlatformDefaultWidth = false),
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.systemBars),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                stringResource(titleRes),
                                modifier = Modifier.weight(1f).padding(start = 8.dp),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            TextButton(onClick = { legalDoc = null }) {
                                Text(stringResource(R.string.common_ok))
                            }
                        }
                        AppWebView(url = url, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

private enum class LegalDoc { TERMS, PRIVACY }

@Composable
private fun SignupUiError.toMessage(): String = when (this) {
    SignupUiError.NAME_REQUIRED -> stringResource(R.string.error_name_required)
    SignupUiError.EMAIL_FORMAT -> stringResource(R.string.error_email_format)
    SignupUiError.PASSWORD_TOO_SHORT -> stringResource(R.string.error_password_too_short)
    SignupUiError.PASSWORD_MISMATCH -> stringResource(R.string.error_password_mismatch)
    SignupUiError.EMAIL_TAKEN -> stringResource(R.string.error_email_taken)
    SignupUiError.NETWORK -> stringResource(R.string.error_network)
    SignupUiError.SERVER -> stringResource(R.string.error_server)
    SignupUiError.UNKNOWN -> stringResource(R.string.error_unknown)
}

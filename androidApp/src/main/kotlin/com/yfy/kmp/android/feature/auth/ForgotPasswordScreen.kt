package com.yfy.kmp.android.feature.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yfy.kmp.android.R
import com.yfy.kmp.core.designsystem.components.AppButton
import com.yfy.kmp.core.designsystem.components.AppErrorText
import com.yfy.kmp.core.designsystem.components.AppScreenScaffold
import com.yfy.kmp.core.designsystem.components.AppSuccessText
import com.yfy.kmp.core.designsystem.components.AppTextField
import com.yfy.kmp.feature.auth.presentation.ForgotPasswordUiError
import com.yfy.kmp.feature.auth.presentation.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AppScreenScaffold(title = stringResource(R.string.forgot_title), onBack = onBack) {
        AppTextField(state.email, viewModel::onEmailChange, stringResource(R.string.field_email), keyboardType = KeyboardType.Email)
        AppButton(text = stringResource(R.string.forgot_submit), onClick = viewModel::submit, loading = state.isLoading)
        state.error?.let { AppErrorText(it.toMessage()) }
        if (state.sent) AppSuccessText(stringResource(R.string.forgot_sent))
    }
}

@Composable
private fun ForgotPasswordUiError.toMessage(): String = when (this) {
    ForgotPasswordUiError.EMAIL_FORMAT -> stringResource(R.string.error_email_format)
    ForgotPasswordUiError.NETWORK -> stringResource(R.string.error_network)
    ForgotPasswordUiError.SERVER -> stringResource(R.string.error_server)
    ForgotPasswordUiError.UNKNOWN -> stringResource(R.string.error_unknown)
}

package com.yfy.kmp.android.feature.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yfy.kmp.android.R
import com.yfy.kmp.core.designsystem.components.AppButton
import com.yfy.kmp.core.designsystem.components.AppErrorText
import com.yfy.kmp.core.designsystem.components.AppPasswordField
import com.yfy.kmp.core.designsystem.components.AppScreenScaffold
import com.yfy.kmp.core.designsystem.components.AppSuccessText
import com.yfy.kmp.feature.auth.presentation.ResetPasswordUiError
import com.yfy.kmp.feature.auth.presentation.ResetPasswordViewModel

@Composable
fun ResetPasswordScreen(
    viewModel: ResetPasswordViewModel,
    token: String,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(token) { viewModel.setToken(token) }

    AppScreenScaffold(title = stringResource(R.string.reset_password_title), onBack = onBack) {
        AppPasswordField(state.newPassword, viewModel::onNewPasswordChange, stringResource(R.string.field_new_password))
        AppPasswordField(state.confirmPassword, viewModel::onConfirmPasswordChange, stringResource(R.string.field_password_confirm))
        state.error?.let { AppErrorText(it.toMessage()) }
        if (state.done) AppSuccessText(stringResource(R.string.reset_password_success))
        AppButton(
            text = stringResource(R.string.reset_password_submit),
            onClick = viewModel::submit,
            loading = state.isLoading,
        )
    }
}

@Composable
private fun ResetPasswordUiError.toMessage(): String = when (this) {
    ResetPasswordUiError.PASSWORD_TOO_SHORT -> stringResource(R.string.error_password_too_short)
    ResetPasswordUiError.PASSWORD_MISMATCH -> stringResource(R.string.error_password_mismatch)
    ResetPasswordUiError.INVALID_TOKEN -> stringResource(R.string.error_invalid_token)
    ResetPasswordUiError.NETWORK -> stringResource(R.string.error_network)
    ResetPasswordUiError.SERVER -> stringResource(R.string.error_server)
    ResetPasswordUiError.UNKNOWN -> stringResource(R.string.error_unknown)
}

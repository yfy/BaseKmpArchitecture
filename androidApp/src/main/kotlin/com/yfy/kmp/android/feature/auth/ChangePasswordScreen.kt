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
import com.yfy.kmp.feature.auth.presentation.ChangePasswordUiError
import com.yfy.kmp.feature.auth.presentation.ChangePasswordViewModel

@Composable
fun ChangePasswordScreen(
    viewModel: ChangePasswordViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AppScreenScaffold(title = stringResource(R.string.change_password_title), onBack = onBack) {
        AppPasswordField(state.currentPassword, viewModel::onCurrentPasswordChange, stringResource(R.string.field_current_password))
        AppPasswordField(state.newPassword, viewModel::onNewPasswordChange, stringResource(R.string.field_new_password))
        AppPasswordField(state.confirmPassword, viewModel::onConfirmPasswordChange, stringResource(R.string.field_password_confirm))
        state.error?.let { AppErrorText(it.toMessage()) }
        if (state.done) AppSuccessText(stringResource(R.string.change_password_success))
        AppButton(
            text = stringResource(R.string.change_password_submit),
            onClick = viewModel::submit,
            loading = state.isLoading,
        )
    }
}

@Composable
private fun ChangePasswordUiError.toMessage(): String = when (this) {
    ChangePasswordUiError.PASSWORD_TOO_SHORT -> stringResource(R.string.error_password_too_short)
    ChangePasswordUiError.PASSWORD_MISMATCH -> stringResource(R.string.error_password_mismatch)
    ChangePasswordUiError.WRONG_CURRENT -> stringResource(R.string.error_wrong_current)
    ChangePasswordUiError.NETWORK -> stringResource(R.string.error_network)
    ChangePasswordUiError.SERVER -> stringResource(R.string.error_server)
    ChangePasswordUiError.UNKNOWN -> stringResource(R.string.error_unknown)
}

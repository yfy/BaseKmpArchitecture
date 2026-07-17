package com.yfy.kmp.android.feature.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yfy.kmp.android.R
import com.yfy.kmp.core.designsystem.components.AppButton
import com.yfy.kmp.core.designsystem.components.AppErrorText
import com.yfy.kmp.core.designsystem.components.AppScreenScaffold
import com.yfy.kmp.core.designsystem.components.AppTextField
import com.yfy.kmp.feature.auth.presentation.EmailVerifyUiError
import com.yfy.kmp.feature.auth.presentation.EmailVerifyViewModel

@Composable
fun EmailVerifyScreen(
    viewModel: EmailVerifyViewModel,
    email: String,
    onVerified: () -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(email) { viewModel.setEmail(email) }
    LaunchedEffect(state.verified) { if (state.verified) onVerified() }

    AppScreenScaffold(title = stringResource(R.string.verify_title), onBack = onBack) {
        AppTextField(
            value = state.code,
            onValueChange = viewModel::onCodeChange,
            label = stringResource(R.string.verify_code_label),
            keyboardType = KeyboardType.Number,
        )
        AppButton(text = stringResource(R.string.verify_submit), onClick = viewModel::verify, loading = state.isLoading)
        state.error?.let { AppErrorText(it.toMessage()) }
    }
}

@Composable
private fun EmailVerifyUiError.toMessage(): String = when (this) {
    EmailVerifyUiError.CODE_FORMAT -> stringResource(R.string.error_code_format)
    EmailVerifyUiError.INVALID_CODE -> stringResource(R.string.error_invalid_code)
    EmailVerifyUiError.NETWORK -> stringResource(R.string.error_network)
    EmailVerifyUiError.SERVER -> stringResource(R.string.error_server)
    EmailVerifyUiError.UNKNOWN -> stringResource(R.string.error_unknown)
}

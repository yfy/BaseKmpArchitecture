package com.yfy.kmp.android.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yfy.kmp.android.R
import com.yfy.kmp.core.designsystem.components.AppButton
import com.yfy.kmp.core.designsystem.components.AppErrorText
import com.yfy.kmp.core.designsystem.components.AppOutlinedButton
import com.yfy.kmp.core.designsystem.components.AppScreenScaffold
import com.yfy.kmp.core.designsystem.components.AppTextField
import com.yfy.kmp.feature.auth.presentation.TwoFactorUiError
import com.yfy.kmp.feature.auth.presentation.TwoFactorViewModel

@Composable
fun TwoFactorScreen(
    viewModel: TwoFactorViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AppScreenScaffold(title = stringResource(R.string.two_factor_title), onBack = onBack) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(stringResource(R.string.two_factor_title), fontWeight = FontWeight.Bold)
            Text(
                stringResource(if (state.enabled) R.string.two_factor_status_on else R.string.two_factor_status_off),
                color = if (state.enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(stringResource(R.string.two_factor_desc), color = MaterialTheme.colorScheme.onSurfaceVariant)

        state.error?.let { AppErrorText(it.toMessage()) }

        when {
            state.enabled -> {
                AppOutlinedButton(text = stringResource(R.string.two_factor_disable), onClick = viewModel::disable)
            }
            state.secret != null -> {
                Text("${stringResource(R.string.two_factor_secret_label)}: ${state.secret}", fontWeight = FontWeight.Medium)
                AppTextField(
                    value = state.code,
                    onValueChange = viewModel::onCodeChange,
                    label = stringResource(R.string.two_factor_code_hint),
                )
                AppButton(
                    text = stringResource(R.string.two_factor_verify),
                    onClick = viewModel::verify,
                    loading = state.isLoading,
                )
            }
            else -> {
                AppButton(
                    text = stringResource(R.string.two_factor_enable),
                    onClick = viewModel::startEnable,
                    loading = state.isLoading,
                )
            }
        }
    }
}

@Composable
private fun TwoFactorUiError.toMessage(): String = when (this) {
    TwoFactorUiError.CODE_FORMAT -> stringResource(R.string.error_code_format)
    TwoFactorUiError.INVALID_CODE -> stringResource(R.string.error_invalid_code)
    TwoFactorUiError.NETWORK -> stringResource(R.string.error_network)
    TwoFactorUiError.SERVER -> stringResource(R.string.error_server)
    TwoFactorUiError.UNKNOWN -> stringResource(R.string.error_unknown)
}

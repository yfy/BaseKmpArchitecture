package com.yfy.kmp.android.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yfy.kmp.android.R
import com.yfy.kmp.core.common.result.AppError

@Composable
fun AppError.toMessage(): String = when (this) {
    AppError.NoConnectivity -> stringResource(R.string.error_offline)
    AppError.Timeout -> stringResource(R.string.error_timeout)
    is AppError.Network -> stringResource(R.string.error_network)
    is AppError.Server -> stringResource(R.string.error_server)
    AppError.Unauthorized -> stringResource(R.string.error_unauthorized)
    is AppError.Unknown -> stringResource(R.string.error_unknown)
}

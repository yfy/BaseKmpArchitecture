package com.yfy.kmp.android.feature.devtools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.yfy.kmp.android.R
import com.yfy.kmp.core.designsystem.components.AppButton
import com.yfy.kmp.core.designsystem.components.AppDialogData
import com.yfy.kmp.core.designsystem.components.AppMessageType
import com.yfy.kmp.core.designsystem.components.AppScreenScaffold
import com.yfy.kmp.core.designsystem.components.LocalAppUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DevToolsScreen(onBack: () -> Unit) {
    val ui = LocalAppUi.current
    val scope = rememberCoroutineScope()

    val infoMsg = stringResource(R.string.devtools_info_msg)
    val successMsg = stringResource(R.string.devtools_success_msg)
    val errorMsg = stringResource(R.string.devtools_error_msg)
    val dialogTitle = stringResource(R.string.devtools_dialog_title)
    val dialogMessage = stringResource(R.string.devtools_dialog_message)
    val okText = stringResource(R.string.common_ok)

    AppScreenScaffold(title = stringResource(R.string.devtools_title), onBack = onBack) {
        AppButton(
            text = stringResource(R.string.devtools_show_info),
            onClick = { ui.showMessage(infoMsg, AppMessageType.INFO) },
        )
        AppButton(
            text = stringResource(R.string.devtools_show_success),
            onClick = { ui.showMessage(successMsg, AppMessageType.SUCCESS) },
        )
        AppButton(
            text = stringResource(R.string.devtools_show_error),
            onClick = { ui.showMessage(errorMsg, AppMessageType.ERROR) },
        )
        AppButton(
            text = stringResource(R.string.devtools_show_dialog),
            onClick = { ui.showDialog(AppDialogData(title = dialogTitle, message = dialogMessage, confirmText = okText)) },
        )
        AppButton(
            text = stringResource(R.string.devtools_show_loader),
            onClick = {
                ui.showLoader()
                scope.launch {
                    delay(3000)
                    ui.hideLoader()
                }
            },
        )
    }
}

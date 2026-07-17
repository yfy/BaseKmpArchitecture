package com.yfy.kmp.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.yfy.kmp.core.designsystem.theme.AppPrimary
import kotlinx.coroutines.launch

enum class AppMessageType { INFO, SUCCESS, ERROR }

data class AppDialogData(
    val title: String,
    val message: String,
    val confirmText: String,
    val dismissText: String? = null,
    val onConfirm: () -> Unit = {},
    val onDismiss: () -> Unit = {},
)

@Stable
interface AppUiController {
    fun showLoader()
    fun hideLoader()
    fun showMessage(text: String, type: AppMessageType = AppMessageType.INFO)
    fun showDialog(data: AppDialogData)
    fun dismissDialog()
}

val LocalAppUi = staticCompositionLocalOf<AppUiController> {
    error("AppUiController yok — içeriği AppUiHost ile sarın")
}

@Composable
fun AppUiHost(content: @Composable () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var loaderVisible by remember { mutableStateOf(false) }
    var dialog by remember { mutableStateOf<AppDialogData?>(null) }
    var messageType by remember { mutableStateOf(AppMessageType.INFO) }

    val controller = remember {
        object : AppUiController {
            override fun showLoader() { loaderVisible = true }
            override fun hideLoader() { loaderVisible = false }
            override fun showMessage(text: String, type: AppMessageType) {
                messageType = type
                scope.launch { snackbarHostState.showSnackbar(text) }
            }
            override fun showDialog(data: AppDialogData) { dialog = data }
            override fun dismissDialog() { dialog = null }
        }
    }

    CompositionLocalProvider(LocalAppUi provides controller) {
        Box(Modifier.fillMaxSize()) {
            content()

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding(),
            ) { data ->
                val container = when (messageType) {
                    AppMessageType.SUCCESS -> AppPrimary
                    AppMessageType.ERROR -> MaterialTheme.colorScheme.error
                    AppMessageType.INFO -> MaterialTheme.colorScheme.inverseSurface
                }
                Snackbar(snackbarData = data, containerColor = container)
            }

            if (loaderVisible) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {},
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }
            }
        }

        dialog?.let { d ->
            AlertDialog(
                onDismissRequest = { d.onDismiss(); dialog = null },
                title = { Text(d.title) },
                text = { Text(d.message) },
                confirmButton = {
                    TextButton(onClick = { d.onConfirm(); dialog = null }) { Text(d.confirmText) }
                },
                dismissButton = d.dismissText?.let { label ->
                    { TextButton(onClick = { d.onDismiss(); dialog = null }) { Text(label) } }
                },
            )
        }
    }
}

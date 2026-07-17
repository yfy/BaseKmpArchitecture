package com.yfy.kmp.android.feature.home

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.yfy.kmp.android.R
import com.yfy.kmp.android.ui.AdBanner
import com.yfy.kmp.core.ads.AdManager
import com.yfy.kmp.core.designsystem.components.AppButton
import com.yfy.kmp.core.designsystem.components.AppDialogData
import com.yfy.kmp.core.designsystem.components.AppMessageType
import com.yfy.kmp.core.designsystem.components.AppScreenScaffold
import com.yfy.kmp.core.designsystem.components.LocalAppUi
import org.koin.compose.getKoin

@Composable
fun HomeScreen(
    onOpenProfile: () -> Unit,
    onOpenDevTools: () -> Unit,
    onLogout: () -> Unit,
) {
    val ui = LocalAppUi.current
    val context = LocalContext.current
    val koin = getKoin()
    val adManager = remember(koin) { koin.get<AdManager>() }

    val logoutTitle = stringResource(R.string.logout_confirm_title)
    val logoutMessage = stringResource(R.string.logout_confirm_message)
    val confirmText = stringResource(R.string.action_confirm)
    val cancelText = stringResource(R.string.action_cancel)

    LaunchedEffect(adManager) {
        adManager.loadInterstitial()
        adManager.loadRewarded()
    }

    AppScreenScaffold(title = stringResource(R.string.home_title)) {
        Text(
            stringResource(R.string.home_success),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        AppButton(text = stringResource(R.string.home_profile), onClick = onOpenProfile)
        AppButton(text = stringResource(R.string.home_devtools), onClick = onOpenDevTools)
        AppButton(
            text = stringResource(R.string.ads_show_interstitial),
            onClick = { adManager.showInterstitial { adManager.loadInterstitial() } },
        )
        AppButton(
            text = stringResource(R.string.ads_show_rewarded),
            onClick = {
                adManager.showRewarded(
                    onReward = { reward ->
                        ui.showMessage(
                            context.getString(R.string.ads_reward_earned, reward.amount),
                            AppMessageType.SUCCESS,
                        )
                    },
                    onClosed = { adManager.loadRewarded() },
                )
            },
        )
        TextButton(
            onClick = {
                ui.showDialog(
                    AppDialogData(
                        title = logoutTitle,
                        message = logoutMessage,
                        confirmText = confirmText,
                        dismissText = cancelText,
                        onConfirm = onLogout,
                    ),
                )
            },
        ) { Text(stringResource(R.string.home_logout)) }
        AdBanner()
    }
}

package com.yfy.kmp.android.feature.paywall

import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yfy.kmp.android.navigation.appViewModel
import com.yfy.kmp.feature.paywall.presentation.PaywallViewModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("paywall")
internal data object PaywallRoute

internal fun NavGraphBuilder.paywallGraph(nav: NavController) {
    composable<PaywallRoute> {
        PaywallScreen(
            viewModel = appViewModel<PaywallViewModel>(),
            onBack = dropUnlessResumed { nav.popBackStack() },
        )
    }
}

package com.yfy.kmp.android.feature.profile

import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yfy.kmp.android.navigation.appViewModel
import com.yfy.kmp.android.feature.paywall.PaywallRoute
import com.yfy.kmp.core.model.AppRoute
import com.yfy.kmp.feature.profile.presentation.ProfileViewModel

internal fun NavGraphBuilder.profileGraph(nav: NavController) {
    composable<AppRoute.Profile> {
        ProfileScreen(
            viewModel = appViewModel<ProfileViewModel>(),
            onBack = dropUnlessResumed { nav.popBackStack() },
            onOpenSettings = dropUnlessResumed { nav.navigate(AppRoute.Settings) },
            onGoPremium = dropUnlessResumed { nav.navigate(PaywallRoute) },
        )
    }
}

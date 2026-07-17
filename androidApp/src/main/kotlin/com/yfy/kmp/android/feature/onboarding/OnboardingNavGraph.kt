package com.yfy.kmp.android.feature.onboarding

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yfy.kmp.android.navigation.appViewModel
import com.yfy.kmp.core.model.AppRoute
import com.yfy.kmp.feature.onboarding.presentation.OnboardingViewModel

internal fun NavGraphBuilder.onboardingGraph(nav: NavController) {
    composable<AppRoute.Onboarding> {
        OnboardingScreen(
            viewModel = appViewModel<OnboardingViewModel>(),
            onFinished = {
                nav.navigate(AppRoute.Login) { popUpTo<AppRoute.Onboarding> { inclusive = true } }
            },
        )
    }
}

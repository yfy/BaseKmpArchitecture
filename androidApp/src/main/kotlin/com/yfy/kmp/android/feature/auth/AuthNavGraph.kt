package com.yfy.kmp.android.feature.auth

import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.yfy.kmp.android.navigation.appViewModel
import com.yfy.kmp.core.model.AppRoute
import com.yfy.kmp.feature.auth.presentation.ChangePasswordViewModel
import com.yfy.kmp.feature.auth.presentation.EmailVerifyViewModel
import com.yfy.kmp.feature.auth.presentation.ForgotPasswordViewModel
import com.yfy.kmp.feature.auth.presentation.LoginViewModel
import com.yfy.kmp.feature.auth.presentation.ResetPasswordViewModel
import com.yfy.kmp.feature.auth.presentation.SignupViewModel
import com.yfy.kmp.feature.auth.presentation.TwoFactorViewModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("change_password")
internal data object ChangePasswordRoute

@Serializable
@SerialName("two_factor")
internal data object TwoFactorRoute

internal fun NavGraphBuilder.authGraph(nav: NavController) {
    composable<AppRoute.Login> {
        LoginScreen(
            viewModel = appViewModel<LoginViewModel>(),
            onLoggedIn = {
                nav.navigate(AppRoute.Home) { popUpTo<AppRoute.Login> { inclusive = true } }
            },
            onGoSignup = dropUnlessResumed { nav.navigate(AppRoute.Signup) },
            onGoForgot = dropUnlessResumed { nav.navigate(AppRoute.ForgotPassword) },
        )
    }
    composable<AppRoute.Signup> {
        SignupScreen(
            viewModel = appViewModel<SignupViewModel>(),
            onBack = dropUnlessResumed { nav.popBackStack() },
            onSignedUp = {
                nav.navigate(AppRoute.Home) { popUpTo<AppRoute.Login> { inclusive = true } }
            },
        )
    }
    composable<AppRoute.ForgotPassword> {
        ForgotPasswordScreen(
            viewModel = appViewModel<ForgotPasswordViewModel>(),
            onBack = dropUnlessResumed { nav.popBackStack() },
        )
    }
    composable<AppRoute.EmailVerify> { entry ->
        val route = entry.toRoute<AppRoute.EmailVerify>()
        EmailVerifyScreen(
            viewModel = appViewModel<EmailVerifyViewModel>(),
            email = route.email,
            onVerified = {
                nav.navigate(AppRoute.Home) { popUpTo<AppRoute.Login> { inclusive = true } }
            },
            onBack = dropUnlessResumed { nav.popBackStack() },
        )
    }
    composable<AppRoute.ResetPassword> { entry ->
        val route = entry.toRoute<AppRoute.ResetPassword>()
        ResetPasswordScreen(
            viewModel = appViewModel<ResetPasswordViewModel>(),
            token = route.token,
            onBack = dropUnlessResumed { nav.popBackStack() },
        )
    }
    composable<ChangePasswordRoute> {
        ChangePasswordScreen(
            viewModel = appViewModel<ChangePasswordViewModel>(),
            onBack = dropUnlessResumed { nav.popBackStack() },
        )
    }
    composable<TwoFactorRoute> {
        TwoFactorScreen(
            viewModel = appViewModel<TwoFactorViewModel>(),
            onBack = dropUnlessResumed { nav.popBackStack() },
        )
    }
}

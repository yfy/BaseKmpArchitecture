package com.yfy.kmp.android.feature.settings

import androidx.lifecycle.compose.dropUnlessResumed
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yfy.kmp.android.navigation.appViewModel
import com.yfy.kmp.android.feature.auth.ChangePasswordRoute
import com.yfy.kmp.android.feature.auth.TwoFactorRoute
import com.yfy.kmp.core.model.AppRoute
import com.yfy.kmp.feature.settings.presentation.LanguageViewModel
import com.yfy.kmp.feature.settings.presentation.SettingsViewModel
import com.yfy.kmp.feature.settings.presentation.ThemeViewModel
import org.koin.compose.getKoin

internal fun NavGraphBuilder.settingsGraph(nav: NavController) {
    composable<AppRoute.Settings> {
        val koin = getKoin()
        SettingsScreen(
            viewModel = appViewModel<SettingsViewModel>(),
            themeViewModel = remember(koin) { koin.get<ThemeViewModel>() },
            languageViewModel = remember(koin) { koin.get<LanguageViewModel>() },
            onBack = dropUnlessResumed { nav.popBackStack() },
            onChangePassword = dropUnlessResumed { nav.navigate(ChangePasswordRoute) },
            onTwoFactor = dropUnlessResumed { nav.navigate(TwoFactorRoute) },
        )
    }
}

package com.yfy.kmp.android.feature.home

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yfy.kmp.android.feature.devtools.DevToolsRoute
import com.yfy.kmp.core.common.session.SessionManager
import com.yfy.kmp.core.model.AppRoute
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.getKoin

internal fun NavGraphBuilder.homeGraph(nav: NavController) {
    composable<AppRoute.Home> {
        val koin = getKoin()
        val scope = rememberCoroutineScope()
        val sessionManager = remember(koin) { koin.get<SessionManager>() }
        HomeScreen(
            onOpenProfile = dropUnlessResumed { nav.navigate(AppRoute.Profile("current")) },
            onOpenDevTools = dropUnlessResumed { nav.navigate(DevToolsRoute) },
            onLogout = { scope.launch { withContext(NonCancellable) { sessionManager.logout() } } },
        )
    }
}

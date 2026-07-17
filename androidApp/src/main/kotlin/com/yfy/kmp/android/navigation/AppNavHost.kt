package com.yfy.kmp.android.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.yfy.kmp.android.feature.auth.authGraph
import com.yfy.kmp.android.feature.devtools.devToolsGraph
import com.yfy.kmp.android.feature.home.homeGraph
import com.yfy.kmp.android.feature.onboarding.onboardingGraph
import com.yfy.kmp.android.feature.paywall.paywallGraph
import com.yfy.kmp.android.feature.profile.profileGraph
import com.yfy.kmp.android.feature.settings.settingsGraph
import com.yfy.kmp.core.analytics.AnalyticsTracker
import com.yfy.kmp.core.common.event.AppEventBus
import com.yfy.kmp.core.common.session.SessionManager
import com.yfy.kmp.core.model.AppRoute
import com.yfy.kmp.shared.isOnboardingCompleted
import org.koin.compose.getKoin

@Composable
fun AppNavHost(
    deepLink: AppRoute? = null,
    onDeepLinkHandled: () -> Unit = {},
) {
    val nav = rememberNavController()
    val koin = getKoin()
    val appEventBus = koin.get<AppEventBus>()
    val analytics = koin.get<AnalyticsTracker>()

    DisposableEffect(nav) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            screenNameOf(destination.route)?.let { analytics.logScreen(it) }
        }
        nav.addOnDestinationChangedListener(listener)
        onDispose { nav.removeOnDestinationChangedListener(listener) }
    }

    LaunchedEffect(Unit) {
        appEventBus.events.collect {
            nav.navigate(AppRoute.Login) {
                popUpTo(nav.graph.findStartDestination().id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val start by produceState<AppRoute?>(initialValue = null) {
        value = when {
            !isOnboardingCompleted() -> AppRoute.Onboarding
            koin.get<SessionManager>().currentUserIdOrNull() != null -> AppRoute.Home
            else -> AppRoute.Login
        }
    }

    val startRoute = start
    if (startRoute == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    LaunchedEffect(startRoute, deepLink) {
        if (deepLink != null) {
            nav.navigate(deepLink)
            onDeepLinkHandled()
        }
    }

    NavHost(navController = nav, startDestination = startRoute) {
        onboardingGraph(nav)
        authGraph(nav)
        homeGraph(nav)
        profileGraph(nav)
        settingsGraph(nav)
        devToolsGraph(nav)
        paywallGraph(nav)
    }
}

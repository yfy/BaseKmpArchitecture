package com.yfy.kmp.android.feature.devtools

import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("dev_tools")
internal data object DevToolsRoute

internal fun NavGraphBuilder.devToolsGraph(nav: NavController) {
    composable<DevToolsRoute> {
        DevToolsScreen(onBack = dropUnlessResumed { nav.popBackStack() })
    }
}

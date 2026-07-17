package com.yfy.kmp.core.analytics

import co.touchlab.kermit.Logger
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSClassFromString
import platform.Foundation.NSSelectorFromString
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class, kotlinx.cinterop.BetaInteropApi::class)
internal class DynamicFirebaseAnalyticsTracker : AnalyticsTracker {

    private val analyticsClass: NSObject? = NSClassFromString("FIRAnalytics") as? NSObject

    init {
        if (analyticsClass == null) {
            Logger.w { "FIRAnalytics not found — analytics no-op (Firebase SDK not linked into app)" }
        }
    }

    override fun logEvent(name: String, params: Map<String, String>) {
        analyticsClass?.performSelector(
            NSSelectorFromString("logEventWithName:parameters:"),
            name,
            params,
        )
    }

    override fun logScreen(name: String) {
        logEvent("screen_view", mapOf("screen_name" to name))
    }

    override fun setUserId(id: String?) {
        analyticsClass?.performSelector(
            NSSelectorFromString("setUserID:"),
            id,
        )
    }

    override fun setUserProperty(name: String, value: String) {
        analyticsClass?.performSelector(
            NSSelectorFromString("setUserPropertyString:forName:"),
            value,
            name,
        )
    }

    override fun reset() {
        analyticsClass?.performSelector(NSSelectorFromString("resetAnalyticsData"))
    }
}

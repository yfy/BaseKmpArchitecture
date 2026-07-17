package com.yfy.kmp.core.analytics

import co.touchlab.kermit.Logger

internal class LoggingAnalyticsTracker : AnalyticsTracker {

    override fun logEvent(name: String, params: Map<String, String>) {
        Logger.i { "Analytics: event=$name params=$params" }
    }

    override fun logScreen(name: String) {
        Logger.i { "Analytics: screen=$name" }
    }

    override fun setUserId(id: String?) {
        Logger.i { "Analytics: userId=$id" }
    }

    override fun setUserProperty(name: String, value: String) {
        Logger.i { "Analytics: userProperty $name=$value" }
    }

    override fun reset() {
        Logger.i { "Analytics: reset" }
    }
}

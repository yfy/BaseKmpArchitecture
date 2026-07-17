package com.yfy.kmp.core.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

internal class FirebaseAnalyticsTracker(context: Context) : AnalyticsTracker {

    private val firebase = FirebaseAnalytics.getInstance(context)

    override fun logEvent(name: String, params: Map<String, String>) {
        firebase.logEvent(name, params.toBundle())
    }

    override fun logScreen(name: String) {
        firebase.logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            mapOf(FirebaseAnalytics.Param.SCREEN_NAME to name).toBundle(),
        )
    }

    override fun setUserId(id: String?) {
        firebase.setUserId(id)
    }

    override fun setUserProperty(name: String, value: String) {
        firebase.setUserProperty(name, value)
    }

    override fun reset() {
        firebase.resetAnalyticsData()
    }

    private fun Map<String, String>.toBundle(): Bundle = Bundle().apply {
        forEach { (key, value) -> putString(key, value) }
    }
}

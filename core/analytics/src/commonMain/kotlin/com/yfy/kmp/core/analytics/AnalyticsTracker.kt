package com.yfy.kmp.core.analytics

public interface AnalyticsTracker {
    public fun logEvent(name: String, params: Map<String, String> = emptyMap())
    public fun logScreen(name: String)
    public fun setUserId(id: String?)
    public fun setUserProperty(name: String, value: String)
    public fun reset()
}

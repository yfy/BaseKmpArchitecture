package com.yfy.kmp.shared

import co.touchlab.crashkios.crashlytics.enableCrashlytics
import co.touchlab.crashkios.crashlytics.setCrashlyticsUnhandledExceptionHook

fun setupCrashReporting() {
    enableCrashlytics()
    setCrashlyticsUnhandledExceptionHook()
}

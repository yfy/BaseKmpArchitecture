package com.yfy.kmp.shared

import co.touchlab.kermit.Logger
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import co.touchlab.kermit.platformLogWriter

fun initLogging() {
    Logger.setLogWriters(platformLogWriter(), CrashlyticsLogWriter())
}

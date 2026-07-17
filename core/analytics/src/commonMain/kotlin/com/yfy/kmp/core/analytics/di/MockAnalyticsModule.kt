package com.yfy.kmp.core.analytics.di

import com.yfy.kmp.core.analytics.AnalyticsTracker
import com.yfy.kmp.core.analytics.LoggingAnalyticsTracker
import org.koin.core.module.Module
import org.koin.dsl.module

public val mockAnalyticsModule: Module = module {
    single<AnalyticsTracker> { LoggingAnalyticsTracker() }
}

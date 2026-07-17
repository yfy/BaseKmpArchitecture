package com.yfy.kmp.core.analytics.di

import com.yfy.kmp.core.analytics.AnalyticsTracker
import com.yfy.kmp.core.analytics.FirebaseAnalyticsTracker
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformAnalyticsModule: Module = module {
    single<AnalyticsTracker> { FirebaseAnalyticsTracker(get()) }
}

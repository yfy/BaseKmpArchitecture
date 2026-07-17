package com.yfy.kmp.core.analytics.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val platformAnalyticsModule: Module

public val analyticsModule: Module = module {
    includes(platformAnalyticsModule)
}

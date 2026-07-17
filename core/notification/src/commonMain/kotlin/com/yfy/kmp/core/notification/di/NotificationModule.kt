package com.yfy.kmp.core.notification.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val platformNotificationModule: Module

public val notificationModule: Module = module {
    includes(platformNotificationModule)
}

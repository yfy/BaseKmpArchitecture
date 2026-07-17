package com.yfy.kmp.core.notification.di

import com.yfy.kmp.core.notification.IosNotificationPresenter
import com.yfy.kmp.core.notification.NotificationPresenter
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformNotificationModule: Module = module {
    single<NotificationPresenter> { IosNotificationPresenter() }
}

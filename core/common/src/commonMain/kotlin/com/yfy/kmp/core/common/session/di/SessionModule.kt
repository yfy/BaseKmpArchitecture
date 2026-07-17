package com.yfy.kmp.core.common.session.di

import com.yfy.kmp.core.common.event.AppEventBus
import com.yfy.kmp.core.common.session.SessionManager
import org.koin.core.module.Module
import org.koin.dsl.module

public val sessionModule: Module = module {
    single { AppEventBus() }
    single {
        SessionManager(
            preferences = get(),
            userCache = get(),
            tokenStore = get(),
            analytics = get(),
            eventBus = get(),
        )
    }
}

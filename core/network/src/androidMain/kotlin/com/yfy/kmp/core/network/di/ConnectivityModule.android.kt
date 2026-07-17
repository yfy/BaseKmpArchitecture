package com.yfy.kmp.core.network.di

import com.yfy.kmp.core.network.AndroidConnectivityChecker
import com.yfy.kmp.core.network.ConnectivityChecker
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformConnectivityModule: Module = module {
    single<ConnectivityChecker> { AndroidConnectivityChecker(get()) }
}

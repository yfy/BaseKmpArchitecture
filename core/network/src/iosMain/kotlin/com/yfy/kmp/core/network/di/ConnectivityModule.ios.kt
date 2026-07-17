package com.yfy.kmp.core.network.di

import com.yfy.kmp.core.network.ConnectivityChecker
import com.yfy.kmp.core.network.IosConnectivityChecker
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformConnectivityModule: Module = module {
    single<ConnectivityChecker> { IosConnectivityChecker() }
}

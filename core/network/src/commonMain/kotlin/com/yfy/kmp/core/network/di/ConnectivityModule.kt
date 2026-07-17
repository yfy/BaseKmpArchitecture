package com.yfy.kmp.core.network.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val platformConnectivityModule: Module

public val connectivityModule: Module = module {
    includes(platformConnectivityModule)
}

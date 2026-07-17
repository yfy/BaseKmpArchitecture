package com.yfy.kmp.core.common.auth.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val platformTokenModule: Module

public val tokenModule: Module = module {
    includes(platformTokenModule)
}

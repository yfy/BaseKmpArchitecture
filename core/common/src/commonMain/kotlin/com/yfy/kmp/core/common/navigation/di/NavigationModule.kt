package com.yfy.kmp.core.common.navigation.di

import com.yfy.kmp.core.common.navigation.NavigationResultBus
import org.koin.core.module.Module
import org.koin.dsl.module

public val navigationModule: Module = module {
    single { NavigationResultBus() }
}

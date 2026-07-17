package com.yfy.kmp.core.ads.di

import com.yfy.kmp.core.ads.AdManager
import com.yfy.kmp.core.ads.MockAdManager
import org.koin.core.module.Module
import org.koin.dsl.module

public val adsModule: Module = module {
    single<AdManager> { MockAdManager() }
}

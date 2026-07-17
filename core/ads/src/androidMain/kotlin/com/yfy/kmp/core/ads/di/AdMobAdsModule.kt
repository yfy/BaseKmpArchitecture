package com.yfy.kmp.core.ads.di

import com.yfy.kmp.core.ads.AdManager
import com.yfy.kmp.core.ads.AdMobAdManager
import org.koin.core.module.Module
import org.koin.dsl.module

public val admobAdsModule: Module = module {
    single<AdManager>(createdAtStart = false) { AdMobAdManager(get()) }
}

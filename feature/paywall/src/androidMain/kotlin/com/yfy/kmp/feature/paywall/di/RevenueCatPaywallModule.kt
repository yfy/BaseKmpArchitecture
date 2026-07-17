package com.yfy.kmp.feature.paywall.di

import com.yfy.kmp.feature.paywall.data.RevenueCatBillingClient
import com.yfy.kmp.feature.paywall.domain.BillingClient
import org.koin.core.module.Module
import org.koin.dsl.module

public val revenueCatPaywallModule: Module = module {
    single<BillingClient>(createdAtStart = false) { RevenueCatBillingClient(get()) }
}

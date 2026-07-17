package com.yfy.kmp.feature.paywall.di

import com.yfy.kmp.feature.paywall.data.MockBillingClient
import com.yfy.kmp.feature.paywall.domain.BillingClient
import com.yfy.kmp.feature.paywall.presentation.PaywallViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

public val paywallModule: Module = module {
    single<BillingClient> { MockBillingClient(delayMillis = 400) }
    factory { PaywallViewModel(billingClient = get()) }
}

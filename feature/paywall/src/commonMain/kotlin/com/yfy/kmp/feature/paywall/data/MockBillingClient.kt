package com.yfy.kmp.feature.paywall.data

import com.yfy.kmp.feature.paywall.domain.BillingClient
import com.yfy.kmp.feature.paywall.domain.PaywallProduct
import com.yfy.kmp.feature.paywall.domain.PurchaseOutcome
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class MockBillingClient(
    private val delayMillis: Long = 0,
) : BillingClient {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private fun <T> respond(value: T, onResult: (T) -> Unit) {
        if (delayMillis == 0L) {
            onResult(value)
        } else {
            scope.launch {
                delay(delayMillis)
                onResult(value)
            }
        }
    }

    override fun fetchOfferings(onResult: (List<PaywallProduct>) -> Unit) {
        respond(
            listOf(
                PaywallProduct("premium_monthly", "Premium — Monthly", "$4.99 / month", "All premium features, billed monthly."),
                PaywallProduct("premium_yearly", "Premium — Yearly", "$39.99 / year", "Two months free, billed yearly."),
            ),
            onResult,
        )
    }

    override fun purchase(productId: String, onResult: (PurchaseOutcome) -> Unit) {
        respond(PurchaseOutcome.Success(productId), onResult)
    }

    override fun restore(onResult: (PurchaseOutcome) -> Unit) {
        respond(PurchaseOutcome.Failed, onResult)
    }
}

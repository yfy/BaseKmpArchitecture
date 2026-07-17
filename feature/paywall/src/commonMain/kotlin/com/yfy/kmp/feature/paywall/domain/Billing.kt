package com.yfy.kmp.feature.paywall.domain

public data class PaywallProduct(
    val id: String,
    val title: String,
    val priceLabel: String,
    val description: String,
)

public sealed interface PurchaseOutcome {
    public data class Success(val productId: String) : PurchaseOutcome
    public data object Cancelled : PurchaseOutcome
    public data object AlreadyOwned : PurchaseOutcome
    public data object Failed : PurchaseOutcome
}

// Callback-based, not Flow-based: Swift cannot implement a Kotlin Flow interface, and the iOS
// RevenueCat client is written in Swift. Implementations must invoke callbacks on the main thread.
public interface BillingClient {
    public fun fetchOfferings(onResult: (List<PaywallProduct>) -> Unit)
    public fun purchase(productId: String, onResult: (PurchaseOutcome) -> Unit)
    public fun restore(onResult: (PurchaseOutcome) -> Unit)
}

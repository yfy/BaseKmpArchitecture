package com.yfy.kmp.feature.paywall.presentation

import com.yfy.kmp.core.common.base.BaseViewModel
import com.yfy.kmp.feature.paywall.domain.BillingClient
import com.yfy.kmp.feature.paywall.domain.PaywallProduct
import com.yfy.kmp.feature.paywall.domain.PurchaseOutcome

public enum class PaywallUiError { FAILED }

public data class PaywallUiState(
    val products: List<PaywallProduct> = emptyList(),
    val isLoading: Boolean = true,
    val purchased: Boolean = false,
    val error: PaywallUiError? = null,
)

public class PaywallViewModel(
    private val billingClient: BillingClient,
) : BaseViewModel<PaywallUiState, Nothing>(PaywallUiState()) {

    init {
        billingClient.fetchOfferings { products ->
            setState { copy(products = products, isLoading = false) }
        }
    }

    public fun purchase(productId: String) {
        if (currentState.isLoading) return
        setState { copy(isLoading = true, error = null) }
        billingClient.purchase(productId) { outcome -> applyOutcome(outcome) }
    }

    public fun restore() {
        if (currentState.isLoading) return
        setState { copy(isLoading = true, error = null) }
        billingClient.restore { outcome -> applyOutcome(outcome) }
    }

    private fun applyOutcome(outcome: PurchaseOutcome) {
        setState {
            when (outcome) {
                is PurchaseOutcome.Success -> copy(isLoading = false, purchased = true, error = null)
                is PurchaseOutcome.AlreadyOwned -> copy(isLoading = false, purchased = true, error = null)
                is PurchaseOutcome.Cancelled -> copy(isLoading = false, error = null)
                is PurchaseOutcome.Failed -> copy(isLoading = false, error = PaywallUiError.FAILED)
            }
        }
    }
}

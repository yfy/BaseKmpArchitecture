package com.yfy.kmp.feature.paywall.data

import android.content.Context
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import com.revenuecat.purchases.interfaces.ReceiveOfferingsCallback
import com.revenuecat.purchases.models.StoreTransaction
import com.yfy.kmp.core.common.CurrentActivityHolder
import com.yfy.kmp.feature.paywall.domain.BillingClient
import com.yfy.kmp.feature.paywall.domain.PaywallProduct
import com.yfy.kmp.feature.paywall.domain.PurchaseOutcome

public object RevenueCatConfig {
    // TODO(template): replace before release — RevenueCat Android public SDK key.
    public const val ANDROID_API_KEY: String = "REVENUECAT_ANDROID_PUBLIC_SDK_KEY"
}

internal class RevenueCatBillingClient(context: Context) : BillingClient {

    private var packagesById: Map<String, Package> = emptyMap()

    init {
        if (!Purchases.isConfigured) {
            Purchases.configure(
                PurchasesConfiguration.Builder(context, RevenueCatConfig.ANDROID_API_KEY).build(),
            )
        }
    }

    override fun fetchOfferings(onResult: (List<PaywallProduct>) -> Unit) {
        Purchases.sharedInstance.getOfferings(object : ReceiveOfferingsCallback {
            override fun onReceived(offerings: Offerings) {
                val packages = offerings.current?.availablePackages.orEmpty()
                packagesById = packages.associateBy { it.identifier }
                onResult(
                    packages.map { pkg ->
                        PaywallProduct(
                            id = pkg.identifier,
                            title = pkg.product.title,
                            priceLabel = pkg.product.price.formatted,
                            description = pkg.product.description,
                        )
                    },
                )
            }

            override fun onError(error: PurchasesError) {
                onResult(emptyList())
            }
        })
    }

    override fun purchase(productId: String, onResult: (PurchaseOutcome) -> Unit) {
        val activity = CurrentActivityHolder.activity
        val pkg = packagesById[productId]
        if (activity == null || pkg == null) {
            onResult(PurchaseOutcome.Failed)
            return
        }
        Purchases.sharedInstance.purchase(
            PurchaseParams.Builder(activity, pkg).build(),
            object : PurchaseCallback {
                override fun onCompleted(storeTransaction: StoreTransaction, customerInfo: CustomerInfo) {
                    onResult(PurchaseOutcome.Success(productId))
                }

                override fun onError(error: PurchasesError, userCancelled: Boolean) {
                    onResult(if (userCancelled) PurchaseOutcome.Cancelled else PurchaseOutcome.Failed)
                }
            },
        )
    }

    override fun restore(onResult: (PurchaseOutcome) -> Unit) {
        Purchases.sharedInstance.restorePurchases(object : ReceiveCustomerInfoCallback {
            override fun onReceived(customerInfo: CustomerInfo) {
                val hasActive = customerInfo.entitlements.active.isNotEmpty()
                onResult(if (hasActive) PurchaseOutcome.Success("restored") else PurchaseOutcome.Failed)
            }

            override fun onError(error: PurchasesError) {
                onResult(PurchaseOutcome.Failed)
            }
        })
    }
}

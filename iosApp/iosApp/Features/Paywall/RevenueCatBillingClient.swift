import Foundation
import Shared
import RevenueCat

// TODO(template): replace before release — RevenueCat iOS public SDK key.
// While empty the paywall falls back to the mock billing client.
enum RevenueCatConfig {
    static let apiKey = ""
    static var isConfigured: Bool { !apiKey.isEmpty }
}

final class RevenueCatBillingClient: BillingClient {

    private var packagesById: [String: Package] = [:]

    func fetchOfferings(onResult: @escaping ([PaywallProduct]) -> Void) {
        Purchases.shared.getOfferings { offerings, _ in
            let packages = offerings?.current?.availablePackages ?? []
            self.packagesById = Dictionary(packages.map { ($0.identifier, $0) }, uniquingKeysWith: { a, _ in a })
            let products = packages.map { pkg in
                PaywallProduct(
                    id: pkg.identifier,
                    title: pkg.storeProduct.localizedTitle,
                    priceLabel: pkg.storeProduct.localizedPriceString,
                    description: pkg.storeProduct.localizedDescription
                )
            }
            onResult(products)
        }
    }

    func purchase(productId: String, onResult: @escaping (PurchaseOutcome) -> Void) {
        guard let pkg = packagesById[productId] else {
            onResult(PurchaseOutcomeFailed())
            return
        }
        Purchases.shared.purchase(package: pkg) { _, _, error, userCancelled in
            if userCancelled {
                onResult(PurchaseOutcomeCancelled())
            } else if error != nil {
                onResult(PurchaseOutcomeFailed())
            } else {
                onResult(PurchaseOutcomeSuccess(productId: productId))
            }
        }
    }

    func restore(onResult: @escaping (PurchaseOutcome) -> Void) {
        Purchases.shared.restorePurchases { customerInfo, _ in
            let active = !(customerInfo?.entitlements.active.isEmpty ?? true)
            onResult(active ? PurchaseOutcomeSuccess(productId: "restored") : PurchaseOutcomeFailed())
        }
    }
}

import SwiftUI
import DesignSystem
import Shared

@MainActor
final class PaywallModel: StateScreenModel<PaywallViewModel, PaywallUiState> {
    init() {
        let vm = KoinKt.getPaywallViewModel()
        super.init(store: vm, clear: { $0.clear() }, state: paywallState(viewModel: vm))
    }

    func purchase(_ productId: String) { store.purchase(productId: productId) }
    func restore() { store.restore() }
}

struct PaywallView: View {
    @StateObject private var model = PaywallModel()

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                Text(L("paywall_subtitle")).foregroundColor(.appTextSecondary)

                if model.state.purchased { AppSuccessText(message: L("paywall_purchased")) }
                if let e = model.state.error { AppErrorText(message: message(for: e)) }

                ForEach(model.state.products, id: \.id) { product in
                    VStack(alignment: .leading, spacing: 8) {
                        Text(product.title).font(.headline)
                        Text(product.priceLabel).foregroundColor(.appPrimary).fontWeight(.semibold)
                        Text(product.description_).foregroundColor(.appTextSecondary)
                        AppButton(title: L("paywall_purchase")) { model.purchase(product.id) }
                    }
                    .padding(16)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color.appFieldBg)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                }

                AppOutlinedButton(title: L("paywall_restore"), action: { model.restore() }) { EmptyView() }
            }
            .padding(24)
        }
        .background(Color.appBackground.ignoresSafeArea())
        .navigationTitle(L("paywall_title"))
    }

    private func message(for error: PaywallUiError) -> String {
        switch error {
        case .failed: return L("paywall_failed")
        default: return L("paywall_failed")
        }
    }
}

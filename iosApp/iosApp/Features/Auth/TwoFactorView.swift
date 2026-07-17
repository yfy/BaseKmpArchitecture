import SwiftUI
import DesignSystem
import Shared

@MainActor
final class TwoFactorModel: StateScreenModel<TwoFactorViewModel, TwoFactorUiState> {
    init() {
        let vm = KoinKt.getTwoFactorViewModel()
        super.init(store: vm, clear: { $0.clear() }, state: twoFactorState(viewModel: vm))
    }

    func onCodeChange(_ v: String) { store.onCodeChange(value: v) }
    func startEnable() { store.startEnable() }
    func verify() { store.verify() }
    func disable() { store.disable() }
}

struct TwoFactorView: View {
    @StateObject private var model = TwoFactorModel()
    @State private var code = ""

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                HStack {
                    Text(L("two_factor_title")).bold()
                    Spacer()
                    Text(L(model.state.enabled ? "two_factor_status_on" : "two_factor_status_off"))
                        .foregroundColor(model.state.enabled ? .appPrimary : .appTextSecondary)
                }
                Text(L("two_factor_desc")).foregroundColor(.appTextSecondary)

                if let e = model.state.error { AppErrorText(message: message(for: e)) }

                if model.state.enabled {
                    AppOutlinedButton(title: L("two_factor_disable"), action: { model.disable() }) { EmptyView() }
                } else if let secret = model.state.secret {
                    Text("\(L("two_factor_secret_label")): \(secret)").fontWeight(.medium)
                    AppTextField(label: L("two_factor_code_hint"), text: $code, keyboard: .numberPad)
                        .onChange(of: code) { model.onCodeChange($0) }
                    AppButton(title: L("two_factor_verify"), loading: model.state.isLoading) { model.verify() }
                } else {
                    AppButton(title: L("two_factor_enable"), loading: model.state.isLoading) { model.startEnable() }
                }
            }
            .padding(24)
        }
        .navigationTitle(L("two_factor_title"))
        .appScreenBackground()
    }

    private func message(for error: TwoFactorUiError) -> String {
        switch error {
        case .codeFormat: return L("error_code_format")
        case .invalidCode: return L("error_invalid_code")
        case .network: return L("error_network")
        case .server: return L("error_server")
        default: return L("error_unknown")
        }
    }
}

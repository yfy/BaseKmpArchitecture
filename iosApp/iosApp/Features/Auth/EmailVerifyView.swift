import SwiftUI
import DesignSystem
import Shared

@MainActor
final class EmailVerifyModel: StateScreenModel<EmailVerifyViewModel, EmailVerifyUiState> {
    init(email: String) {
        let vm = KoinKt.getEmailVerifyViewModel()
        vm.setEmail(email: email)
        super.init(store: vm, clear: { $0.clear() }, state: emailVerifyState(viewModel: vm))
    }

    func onCodeChange(_ v: String) { store.onCodeChange(value: v) }
    func verify() { store.verify() }
}

struct EmailVerifyView: View {
    @StateObject private var model: EmailVerifyModel
    @State private var code = ""
    let onVerified: () -> Void

    init(email: String, onVerified: @escaping () -> Void) {
        _model = StateObject(wrappedValue: EmailVerifyModel(email: email))
        self.onVerified = onVerified
    }

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                AppTextField(label: L("verify_code_label"), text: $code, keyboard: .numberPad)
                    .onChange(of: code) { model.onCodeChange($0) }
                AppButton(title: L("verify_submit"), loading: model.state.isLoading) { model.verify() }
                if let e = model.state.error { AppErrorText(message: message(for: e)) }
            }
            .padding(24)
        }
        .navigationTitle(L("verify_title"))
        .appScreenBackground()
        .onChange(of: model.state.verified) { if $0 { onVerified() } }
    }

    private func message(for error: EmailVerifyUiError) -> String {
        switch error {
        case .codeFormat: return L("error_code_format")
        case .invalidCode: return L("error_invalid_code")
        case .network: return L("error_network")
        case .server: return L("error_server")
        default: return L("error_unknown")
        }
    }
}

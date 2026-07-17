import SwiftUI
import DesignSystem
import Shared

@MainActor
final class ForgotPasswordModel: StateScreenModel<ForgotPasswordViewModel, ForgotPasswordUiState> {
    init() {
        let vm = KoinKt.getForgotPasswordViewModel()
        super.init(store: vm, clear: { $0.clear() }, state: forgotPasswordState(viewModel: vm))
    }

    func onEmailChange(_ v: String) { store.onEmailChange(value: v) }
    func submit() { store.submit() }
}

struct ForgotPasswordView: View {
    @StateObject private var model = ForgotPasswordModel()
    @State private var email = ""

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                AppTextField(label: L("field_email"), text: $email, keyboard: .emailAddress)
                    .onChange(of: email) { model.onEmailChange($0) }
                AppButton(title: L("forgot_submit"), loading: model.state.isLoading) { model.submit() }
                if let e = model.state.error { AppErrorText(message: message(for: e)) }
                if model.state.sent { AppSuccessText(message: L("forgot_sent")) }
            }
            .padding(24)
        }
        .navigationTitle(L("forgot_title"))
        .appScreenBackground()
    }

    private func message(for error: ForgotPasswordUiError) -> String {
        switch error {
        case .emailFormat: return L("error_email_format")
        case .network: return L("error_network")
        case .server: return L("error_server")
        default: return L("error_unknown")
        }
    }
}

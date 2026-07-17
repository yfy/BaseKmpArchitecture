import SwiftUI
import DesignSystem
import Shared

@MainActor
final class ResetPasswordModel: StateScreenModel<ResetPasswordViewModel, ResetPasswordUiState> {
    init(token: String) {
        let vm = KoinKt.getResetPasswordViewModel()
        vm.setToken(token: token)
        super.init(store: vm, clear: { $0.clear() }, state: resetPasswordState(viewModel: vm))
    }

    func onNewPasswordChange(_ v: String) { store.onNewPasswordChange(value: v) }
    func onConfirmPasswordChange(_ v: String) { store.onConfirmPasswordChange(value: v) }
    func submit() { store.submit() }
}

struct ResetPasswordView: View {
    @StateObject private var model: ResetPasswordModel
    @State private var newPassword = ""
    @State private var confirmPassword = ""

    init(token: String) {
        _model = StateObject(wrappedValue: ResetPasswordModel(token: token))
    }

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                AppPasswordField(label: L("field_new_password"), text: $newPassword)
                    .onChange(of: newPassword) { model.onNewPasswordChange($0) }
                AppPasswordField(label: L("field_password_confirm"), text: $confirmPassword)
                    .onChange(of: confirmPassword) { model.onConfirmPasswordChange($0) }
                if let e = model.state.error { AppErrorText(message: message(for: e)) }
                if model.state.done { AppSuccessText(message: L("reset_password_success")) }
                AppButton(title: L("reset_password_submit"), loading: model.state.isLoading) { model.submit() }
            }
            .padding(24)
        }
        .navigationTitle(L("reset_password_title"))
        .appScreenBackground()
    }

    private func message(for error: ResetPasswordUiError) -> String {
        switch error {
        case .passwordTooShort: return L("error_password_too_short")
        case .passwordMismatch: return L("error_password_mismatch")
        case .invalidToken: return L("error_invalid_token")
        case .network: return L("error_network")
        case .server: return L("error_server")
        default: return L("error_unknown")
        }
    }
}

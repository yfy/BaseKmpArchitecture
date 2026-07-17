import SwiftUI
import DesignSystem
import Shared

@MainActor
final class ChangePasswordModel: StateScreenModel<ChangePasswordViewModel, ChangePasswordUiState> {
    init() {
        let vm = KoinKt.getChangePasswordViewModel()
        super.init(store: vm, clear: { $0.clear() }, state: changePasswordState(viewModel: vm))
    }

    func onCurrentPasswordChange(_ v: String) { store.onCurrentPasswordChange(value: v) }
    func onNewPasswordChange(_ v: String) { store.onNewPasswordChange(value: v) }
    func onConfirmPasswordChange(_ v: String) { store.onConfirmPasswordChange(value: v) }
    func submit() { store.submit() }
}

struct ChangePasswordView: View {
    @StateObject private var model = ChangePasswordModel()
    @State private var current = ""
    @State private var newPassword = ""
    @State private var confirmPassword = ""

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                AppPasswordField(label: L("field_current_password"), text: $current)
                    .onChange(of: current) { model.onCurrentPasswordChange($0) }
                AppPasswordField(label: L("field_new_password"), text: $newPassword)
                    .onChange(of: newPassword) { model.onNewPasswordChange($0) }
                AppPasswordField(label: L("field_password_confirm"), text: $confirmPassword)
                    .onChange(of: confirmPassword) { model.onConfirmPasswordChange($0) }
                if let e = model.state.error { AppErrorText(message: message(for: e)) }
                if model.state.done { AppSuccessText(message: L("change_password_success")) }
                AppButton(title: L("change_password_submit"), loading: model.state.isLoading) { model.submit() }
            }
            .padding(24)
        }
        .navigationTitle(L("change_password_title"))
        .appScreenBackground()
    }

    private func message(for error: ChangePasswordUiError) -> String {
        switch error {
        case .passwordTooShort: return L("error_password_too_short")
        case .passwordMismatch: return L("error_password_mismatch")
        case .wrongCurrent: return L("error_wrong_current")
        case .network: return L("error_network")
        case .server: return L("error_server")
        default: return L("error_unknown")
        }
    }
}

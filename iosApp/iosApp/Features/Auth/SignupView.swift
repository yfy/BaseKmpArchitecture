import SwiftUI
import DesignSystem
import Shared

@MainActor
final class SignupModel: StateScreenModel<SignupViewModel, SignupUiState> {
    init() {
        let vm = KoinKt.getSignupViewModel()
        super.init(store: vm, clear: { $0.clear() }, state: signupState(viewModel: vm))
    }

    func onFirstNameChange(_ v: String) { store.onFirstNameChange(value: v) }
    func onLastNameChange(_ v: String) { store.onLastNameChange(value: v) }
    func onEmailChange(_ v: String) { store.onEmailChange(value: v) }
    func onPasswordChange(_ v: String) { store.onPasswordChange(value: v) }
    func onConfirmPasswordChange(_ v: String) { store.onConfirmPasswordChange(value: v) }
    func onTermsAcceptedChange(_ v: Bool) { store.onTermsAcceptedChange(value: v) }
    func signup() { store.signup() }
}

struct SignupView: View {
    @StateObject private var model = SignupModel()
    @Environment(\.dismiss) private var dismiss
    @State private var firstName = ""
    @State private var lastName = ""
    @State private var email = ""
    @State private var password = ""
    @State private var confirmPassword = ""
    @State private var legalDoc: LegalDoc?
    let onSignedUp: () -> Void

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                Text(L("signup_title")).font(.largeTitle).bold()
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 16)

                AppTextField(label: L("field_first_name"), text: $firstName)
                    .accessibilityIdentifier("signupFirstName")
                    .onChange(of: firstName) { model.onFirstNameChange($0) }
                    .padding(.bottom, 12)
                AppTextField(label: L("field_last_name"), text: $lastName)
                    .accessibilityIdentifier("signupLastName")
                    .onChange(of: lastName) { model.onLastNameChange($0) }
                    .padding(.bottom, 12)
                AppTextField(label: L("field_email"), text: $email, keyboard: .emailAddress)
                    .accessibilityIdentifier("signupEmail")
                    .onChange(of: email) { model.onEmailChange($0) }
                    .padding(.bottom, 12)
                AppPasswordField(label: L("field_password"), text: $password)
                    .accessibilityIdentifier("signupPassword")
                    .onChange(of: password) { model.onPasswordChange($0) }
                    .padding(.bottom, 12)
                AppPasswordField(label: L("field_password_confirm"), text: $confirmPassword)
                    .accessibilityIdentifier("signupConfirmPassword")
                    .onChange(of: confirmPassword) { model.onConfirmPasswordChange($0) }

                VStack(alignment: .leading, spacing: 4) {
                    HStack(spacing: 8) {
                        Button { model.onTermsAcceptedChange(!model.state.termsAccepted) } label: {
                            Image(systemName: model.state.termsAccepted ? "checkmark.square.fill" : "square")
                                .foregroundColor(model.state.termsAccepted ? .appPrimary : .appTextSecondary)
                        }
                        .accessibilityIdentifier("signupTermsCheckbox")
                        Text(L("terms_accept_prefix"))
                        Spacer(minLength: 0)
                    }
                    HStack(spacing: 4) {
                        Button(L("terms_of_service")) { legalDoc = .terms }
                            .foregroundColor(.appPrimary).fontWeight(.semibold)
                        Text(L("terms_and"))
                        Button(L("privacy_policy")) { legalDoc = .privacy }
                            .foregroundColor(.appPrimary).fontWeight(.semibold)
                        Spacer(minLength: 0)
                    }
                    .padding(.leading, 28)
                }
                .padding(.vertical, 12)

                if let e = model.state.error {
                    AppErrorText(message: message(for: e)).padding(.bottom, 4)
                }

                AppButton(
                    title: L("signup_submit"),
                    loading: model.state.isLoading,
                    enabled: model.state.termsAccepted
                ) { model.signup() }

                HStack {
                    Text(L("signup_have_account"))
                    Button(L("login_link")) { dismiss() }.foregroundColor(.appPrimary)
                }
                .padding(.top, 16)
            }
            .padding(.horizontal, 24)
            .padding(.bottom, 24)
        }
        .background(
            LinearGradient(colors: [.appGradientTop, .appGradientBottom], startPoint: .top, endPoint: .bottom)
                .ignoresSafeArea()
        )
        .navigationBarTitleDisplayMode(.inline)
        .onChange(of: model.state.user != nil) { if $0 { onSignedUp() } }
        .sheet(item: $legalDoc) { WebViewScreen(title: L($0.title), url: $0.url) }
    }

    private func message(for error: SignupUiError) -> String {
        switch error {
        case .nameRequired: return L("error_name_required")
        case .emailFormat: return L("error_email_format")
        case .passwordTooShort: return L("error_password_too_short")
        case .passwordMismatch: return L("error_password_mismatch")
        case .emailTaken: return L("error_email_taken")
        case .network: return L("error_network")
        case .server: return L("error_server")
        default: return L("error_unknown")
        }
    }
}

enum LegalDoc: Identifiable {
    case terms, privacy
    var id: Int { self == .terms ? 0 : 1 }
    var title: String { self == .terms ? "terms_of_service" : "privacy_policy" }
    // TODO(template): replace before release — legal document URLs.
    var url: URL {
        URL(string: self == .terms ? "https://example.com/terms" : "https://example.com/privacy")!
    }
}

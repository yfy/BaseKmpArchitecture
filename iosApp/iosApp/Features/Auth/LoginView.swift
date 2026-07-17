import SwiftUI
import DesignSystem
import Shared

@MainActor
final class LoginModel: StateScreenModel<LoginViewModel, LoginUiState> {
    init() {
        let vm = KoinKt.getLoginViewModel()
        super.init(store: vm, clear: { $0.clear() }, state: loginState(viewModel: vm))
    }

    func onEmailChange(_ v: String) { store.onEmailChange(value: v) }
    func onPasswordChange(_ v: String) { store.onPasswordChange(value: v) }
    func onRememberMeChange(_ v: Bool) { store.onRememberMeChange(value: v) }
    func login() { store.login() }
    func socialLoginWithToken(_ provider: SocialProvider, _ token: String) {
        store.socialLoginWithToken(provider: provider, token: token)
    }
}

struct LoginView: View {
    @StateObject private var model = LoginModel()
    @StateObject private var social = SocialAuthService()
    @State private var email = ""
    @State private var password = ""
    let onLoggedIn: () -> Void
    let onGoSignup: () -> Void
    let onGoForgot: () -> Void

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                Spacer().frame(height: 56)
                Image("AppLogo").resizable().scaledToFit().frame(width: 88, height: 88)
                Text(L("brand_name")).font(.headline).bold().padding(.top, 8)
                Text(L("login_welcome_title")).font(.largeTitle).bold()
                Text(L("login_welcome_subtitle"))
                    .foregroundColor(.appTextSecondary)
                    .multilineTextAlignment(.center)
                    .padding(.top, 4)
                Spacer().frame(height: 28)

                AppTextField(label: L("field_email"), text: $email, keyboard: .emailAddress)
                    .onChange(of: email) { model.onEmailChange($0) }
                    .padding(.bottom, 12)
                AppPasswordField(label: L("field_password"), text: $password)
                    .onChange(of: password) { model.onPasswordChange($0) }

                HStack {
                    Button { model.onRememberMeChange(!model.state.rememberMe) } label: {
                        HStack(spacing: 8) {
                            Image(systemName: model.state.rememberMe ? "checkmark.square.fill" : "square")
                                .foregroundColor(model.state.rememberMe ? .appPrimary : .appTextSecondary)
                            Text(L("remember_me")).foregroundColor(.appTextPrimary)
                        }
                    }
                    Spacer()
                    Button(L("login_go_forgot")) { onGoForgot() }
                        .foregroundColor(.appPrimary)
                }
                .padding(.vertical, 8)

                if let e = model.state.error {
                    AppErrorText(message: message(for: e)).padding(.bottom, 4)
                }

                AppButton(title: L("login_submit"), loading: model.state.isLoading) { model.login() }

                AppOrDivider(text: L("or_divider")).padding(.vertical, 16)

                BrandSocialButton(
                    title: L("continue_with_google"),
                    background: .appSocialGoogleBg,
                    foreground: .appSocialGoogleText,
                    border: .appSocialGoogleBorder,
                    enabled: !model.state.isLoading,
                    action: { signIn(.google) }
                ) {
                    Image("google").resizable().frame(width: 20, height: 20)
                }
                .padding(.bottom, 12)
                BrandSocialButton(
                    title: L("continue_with_apple"),
                    background: .appSocialAppleBg,
                    foreground: .appSocialAppleText,
                    enabled: !model.state.isLoading,
                    action: { signIn(.apple) }
                ) {
                    Image(systemName: "apple.logo").font(.system(size: 18, weight: .medium))
                }
                .padding(.bottom, 12)
                AppOutlinedButton(title: L("continue_with_instagram"), action: { signIn(.instagram) }) {
                    Image("instagram").resizable().frame(width: 20, height: 20).foregroundColor(.appTextPrimary)
                }
                .disabled(model.state.isLoading)

                HStack {
                    Text(L("login_no_account"))
                    Button(L("signup_link")) { onGoSignup() }.foregroundColor(.appPrimary)
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
        .navigationBarHidden(true)
        .onChange(of: model.state.user != nil) { if $0 { onLoggedIn() } }
    }

    private func signIn(_ provider: SocialProvider) {
        guard !model.state.isLoading else { return }
        Task {
            if let token = await social.signIn(provider) {
                model.socialLoginWithToken(provider, token)
            }
        }
    }

    private func message(for error: LoginUiError) -> String {
        switch error {
        case .emailFormat: return L("error_email_format")
        case .passwordTooShort: return L("error_password_too_short")
        case .invalidCredentials: return L("error_invalid_credentials")
        case .network: return L("error_network")
        case .server: return L("error_server")
        default: return L("error_unknown")
        }
    }
}

private struct BrandSocialButton<Leading: View>: View {
    let title: String
    let background: Color
    let foreground: Color
    var border: Color? = nil
    var enabled: Bool = true
    let action: () -> Void
    @ViewBuilder var leading: () -> Leading

    var body: some View {
        Button(action: action) {
            HStack(spacing: 10) {
                leading()
                Text(title).fontWeight(.semibold)
            }
            .foregroundColor(foreground)
            .frame(maxWidth: .infinity)
            .frame(height: 54)
            .background(RoundedRectangle(cornerRadius: 27).fill(background))
            .overlay {
                if let border {
                    RoundedRectangle(cornerRadius: 27).stroke(border, lineWidth: 1)
                }
            }
            .opacity(enabled ? 1 : 0.6)
        }
        .disabled(!enabled)
    }
}

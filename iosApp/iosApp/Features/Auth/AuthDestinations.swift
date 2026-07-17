import SwiftUI

struct AuthDestinationView: View {
    let destination: AppDestination
    let goHome: () -> Void

    var body: some View {
        switch destination {
        case .signup:
            SignupView(onSignedUp: goHome)
        case .forgotPassword:
            ForgotPasswordView()
        case .emailVerify(let email):
            EmailVerifyView(email: email, onVerified: goHome)
        case .resetPassword(let token):
            ResetPasswordView(token: token)
        case .changePassword:
            ChangePasswordView()
        case .twoFactor:
            TwoFactorView()
        case .home, .profile, .settings, .devTools, .paywall:
            EmptyView()
        }
    }
}

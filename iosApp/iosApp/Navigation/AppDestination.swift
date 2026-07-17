import Shared

enum AppDestination: Hashable {
    case signup
    case forgotPassword
    case emailVerify(email: String)
    case resetPassword(token: String)
    case changePassword
    case twoFactor
    case devTools
    case paywall
    case home
    case profile(userId: String)
    case settings

    init?(route: AppRoute) {
        switch route {
        case is AppRouteSignup: self = .signup
        case is AppRouteForgotPassword: self = .forgotPassword
        case let r as AppRouteEmailVerify: self = .emailVerify(email: r.email)
        case let r as AppRouteResetPassword: self = .resetPassword(token: r.token)
        case is AppRouteHome: self = .home
        case let r as AppRouteProfile: self = .profile(userId: r.userId)
        case is AppRouteSettings: self = .settings
        default: return nil
        }
    }
}

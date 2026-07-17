import SwiftUI
import Shared

struct ContentView: View {
    enum Start { case onboarding, login, home }

    @State private var path: [AppDestination] = []
    @State private var start: Start?
    @State private var sessionID = UUID()
    @StateObject private var ui = AppUiController()
    @StateObject private var theme = ThemeController()
    @StateObject private var language = LanguageController()

    private var contentID: String { "\(sessionID.uuidString)#\(language.refreshID)" }

    var body: some View {
        Group {
            if let start {
                NavigationStack(path: $path) {
                    root(for: start)
                        .navigationDestination(for: AppDestination.self) { destination in
                            view(for: destination)
                        }
                        .id(contentID)
                }
            } else {
                ProgressView()
            }
        }
        .environmentObject(ui)
        .environmentObject(theme)
        .environmentObject(language)
        .appUiHost(ui)
        .preferredColorScheme(theme.colorScheme)
        .environment(\.locale, language.locale)
        .task {
            if start == nil {
                let completed = (try? await KoinKt.isOnboardingCompleted())?.boolValue ?? false
                if !completed {
                    start = .onboarding
                } else {
                    let loggedIn = (try? await KoinKt.hasActiveSession())?.boolValue ?? false
                    start = loggedIn ? .home : .login
                }
            }
            for await _ in KoinKt.getAppEventBus().events {
                path = []
                sessionID = UUID()
                start = .login
            }
        }
        .onChange(of: path) { _ in logCurrentScreen() }
        .onChange(of: start) { _ in logCurrentScreen() }
        .onOpenURL { url in handleDeepLink(uri: url.absoluteString) }
        // Universal links only fire if the app ships an `applinks:` associated-domains entitlement.
        .onContinueUserActivity(NSUserActivityTypeBrowsingWeb) { activity in
            if let url = activity.webpageURL { handleDeepLink(uri: url.absoluteString) }
        }
        .onReceive(NotificationCenter.default.publisher(for: .appDeepLink)) { note in
            if let uri = note.object as? String { handleDeepLink(uri: uri) }
        }
    }

    private func logCurrentScreen() {
        guard let name = currentScreenName() else { return }
        KoinKt.getAnalyticsTracker().logScreen(name: name)
    }

    private func currentScreenName() -> String? {
        if let destination = path.last { return screenName(for: destination) }
        guard let start else { return nil }
        switch start {
        case .onboarding: return "onboarding"
        case .login: return "login"
        case .home: return "home"
        }
    }

    private func screenName(for destination: AppDestination) -> String {
        switch destination {
        case .signup: return "signup"
        case .forgotPassword: return "forgot_password"
        case .emailVerify: return "email_verify"
        case .resetPassword: return "reset_password"
        case .changePassword: return "change_password"
        case .twoFactor: return "two_factor"
        case .devTools: return "dev_tools"
        case .paywall: return "paywall"
        case .home: return "home"
        case .profile: return "profile"
        case .settings: return "settings"
        }
    }

    private func goHome() {
        path = []
        start = .home
    }

    private func handleDeepLink(uri: String) {
        guard let route = AppRouteKt.parseAppRoute(uri: uri, payload: [:]),
              let destination = AppDestination(route: route) else { return }
        if destination == .home {
            goHome()
            return
        }
        if start == nil { start = .login }
        path.append(destination)
    }

    @ViewBuilder
    private func root(for start: Start) -> some View {
        switch start {
        case .onboarding:
            OnboardingView(onFinished: { self.start = .login })
        case .login:
            LoginView(
                onLoggedIn: { goHome() },
                onGoSignup: { path.append(.signup) },
                onGoForgot: { path.append(.forgotPassword) }
            )
        case .home:
            HomeDestinationView(path: $path)
        }
    }

    @ViewBuilder
    private func view(for destination: AppDestination) -> some View {
        switch destination {
        case .signup, .forgotPassword, .emailVerify, .resetPassword, .changePassword, .twoFactor:
            AuthDestinationView(destination: destination, goHome: goHome)
        case .home:
            HomeDestinationView(path: $path)
        case .profile:
            ProfileDestinationView(path: $path)
        case .settings:
            SettingsDestinationView(path: $path)
        case .devTools:
            DevToolsView()
        case .paywall:
            PaywallView()
        }
    }
}

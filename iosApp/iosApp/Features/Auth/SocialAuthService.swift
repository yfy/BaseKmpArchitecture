import SwiftUI
import AuthenticationServices
import Shared

@MainActor
final class SocialAuthService: NSObject, ObservableObject {

    // TODO(template): replace before release — OAuth client ids (and the "yfy" callback scheme).
    // While empty, sign-in returns a dummy token so the mock backend still works.
    private struct OAuthConfig {
        let authURL: String
        let clientId: String
        let callbackScheme: String
    }
    private let google = OAuthConfig(authURL: "https://accounts.google.com/o/oauth2/v2/auth", clientId: "", callbackScheme: "yfy")
    private let instagram = OAuthConfig(authURL: "https://api.instagram.com/oauth/authorize", clientId: "", callbackScheme: "yfy")

    private var appleContinuation: CheckedContinuation<String?, Never>?
    private var webSession: ASWebAuthenticationSession?

    func signIn(_ provider: SocialProvider) async -> String? {
        if provider == SocialProvider.apple { return await signInApple() }
        if provider == SocialProvider.google { return await signInWeb(google, provider: "google") }
        if provider == SocialProvider.instagram { return await signInWeb(instagram, provider: "instagram") }
        return "dummy-\(provider.name.lowercased())-token"
    }

    private func signInApple() async -> String? {
        if appleContinuation != nil { return nil }
        return await withCheckedContinuation { continuation in
            appleContinuation = continuation
            let request = ASAuthorizationAppleIDProvider().createRequest()
            request.requestedScopes = [.fullName, .email]
            let controller = ASAuthorizationController(authorizationRequests: [request])
            controller.delegate = self
            controller.presentationContextProvider = self
            controller.performRequests()
        }
    }

    private func signInWeb(_ config: OAuthConfig, provider: String) async -> String? {
        guard !config.clientId.isEmpty else { return "dummy-\(provider)-token" }
        let url = "\(config.authURL)?client_id=\(config.clientId)&response_type=token&redirect_uri=\(config.callbackScheme)://oauth"
        guard let authURL = URL(string: url) else { return nil }
        webSession?.cancel()
        return await withCheckedContinuation { continuation in
            let session = ASWebAuthenticationSession(url: authURL, callbackURLScheme: config.callbackScheme) { callback, _ in
                let token = callback?.absoluteString
                continuation.resume(returning: token)
            }
            session.presentationContextProvider = self
            session.prefersEphemeralWebBrowserSession = true
            webSession = session
            session.start()
        }
    }
}

extension SocialAuthService: ASAuthorizationControllerDelegate {
    func authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization authorization: ASAuthorization) {
        if let credential = authorization.credential as? ASAuthorizationAppleIDCredential,
           let tokenData = credential.identityToken,
           let token = String(data: tokenData, encoding: .utf8) {
            appleContinuation?.resume(returning: token)
        } else {
            appleContinuation?.resume(returning: "dummy-apple-token")
        }
        appleContinuation = nil
    }

    func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: Error) {
        appleContinuation?.resume(returning: nil)
        appleContinuation = nil
    }
}

extension SocialAuthService: ASAuthorizationControllerPresentationContextProviding, ASWebAuthenticationPresentationContextProviding {
    func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
        anchor()
    }
    func presentationAnchor(for session: ASWebAuthenticationSession) -> ASPresentationAnchor {
        anchor()
    }
    private func anchor() -> ASPresentationAnchor {
        UIApplication.shared.connectedScenes
            .compactMap { ($0 as? UIWindowScene)?.keyWindow }
            .first ?? ASPresentationAnchor()
    }
}

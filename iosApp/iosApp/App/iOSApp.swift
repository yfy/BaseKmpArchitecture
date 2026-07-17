import SwiftUI
import Shared
import FirebaseCore
import RevenueCat
import UserNotifications

extension Notification.Name {
    static let appDeepLink = Notification.Name("appDeepLink")
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) private var appDelegate

    var body: some Scene {
        WindowGroup {
            RootView()
        }
    }
}

final class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        FirebaseApp.configure()
        CrashReporting_iosKt.setupCrashReporting()
        var billing: BillingClient? = nil
        if NativeIntegrations.shared.revenueCat, RevenueCatConfig.isConfigured {
            Purchases.configure(withAPIKey: RevenueCatConfig.apiKey)
            billing = RevenueCatBillingClient()
        }
        var ads: AdManager? = nil
        if NativeIntegrations.shared.admob, AdMobConfig.isConfigured {
            ads = AdMobAdManager()
        }
        KoinKt.startAppKoin(environment: Self.resolveEnvironment(), billingClient: billing, adManager: ads, platformDeclaration: nil)
        ads?.initialize { }
        UNUserNotificationCenter.current().delegate = self
        return true
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.banner, .list, .sound])
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        let userInfo = response.notification.request.content.userInfo
        if let route = userInfo["app_route"] as? String {
            NotificationCenter.default.post(name: .appDeepLink, object: route)
        }
        completionHandler()
    }

    private static func resolveEnvironment() -> AppEnvironment {
        let raw = (Bundle.main.object(forInfoDictionaryKey: "APP_ENVIRONMENT") as? String) ?? ""
        switch raw.uppercased() {
        case "PROD": return .prod
        case "DEBUG", "DEV": return .debug
        default: return .mock
        }
    }
}

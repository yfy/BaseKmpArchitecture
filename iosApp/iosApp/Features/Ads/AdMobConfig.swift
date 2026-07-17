import Foundation

// TODO(template): replace before release — AdMob app + ad unit ids (Google's TEST ids; safe for development).
// Keep the app id in sync with GADApplicationIdentifier in Info.plist.
enum AdMobConfig {
    static let appId = "ca-app-pub-3940256099942544~1458002511"
    static let bannerUnitId = "ca-app-pub-3940256099942544/2934735716"
    static let interstitialUnitId = "ca-app-pub-3940256099942544/4411468910"
    static let rewardedUnitId = "ca-app-pub-3940256099942544/1712485313"

    static var isConfigured: Bool { !appId.isEmpty }
}

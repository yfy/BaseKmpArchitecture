import Foundation
import UIKit
import Shared
import GoogleMobileAds
import UserMessagingPlatform
import AppTrackingTransparency

final class AdMobAdManager: NSObject, AdManager {

    private var interstitial: InterstitialAd?
    private var rewarded: RewardedAd?
    private var onInterstitialClosed: (() -> Void)?
    private var onRewardedClosed: (() -> Void)?

    func initialize(onComplete: @escaping () -> Void) {
        requestTrackingAuthorization { [weak self] in
            self?.gatherConsentThenStart(onComplete)
        }
    }

    private func requestTrackingAuthorization(_ done: @escaping () -> Void) {
        ATTrackingManager.requestTrackingAuthorization { _ in
            DispatchQueue.main.async { done() }
        }
    }

    private func gatherConsentThenStart(_ onComplete: @escaping () -> Void) {
        let parameters = RequestParameters()
        ConsentInformation.shared.requestConsentInfoUpdate(with: parameters) { [weak self] _ in
            guard let root = self?.topViewController() else {
                MobileAds.shared.start { _ in onComplete() }
                return
            }
            ConsentForm.loadAndPresentIfRequired(from: root) { _ in
                MobileAds.shared.start { _ in onComplete() }
            }
        }
    }

    func loadInterstitial() {
        InterstitialAd.load(with: AdMobConfig.interstitialUnitId, request: Request()) { [weak self] ad, _ in
            self?.interstitial = ad
            ad?.fullScreenContentDelegate = self
        }
    }

    func showInterstitial(onClosed: @escaping () -> Void) {
        guard let ad = interstitial, let root = topViewController() else {
            onClosed()
            return
        }
        onInterstitialClosed = onClosed
        ad.present(from: root)
    }

    func loadRewarded() {
        RewardedAd.load(with: AdMobConfig.rewardedUnitId, request: Request()) { [weak self] ad, _ in
            self?.rewarded = ad
            ad?.fullScreenContentDelegate = self
        }
    }

    func showRewarded(onReward: @escaping (Shared.AdReward) -> Void, onClosed: @escaping () -> Void) {
        guard let ad = rewarded, let root = topViewController() else {
            onClosed()
            return
        }
        onRewardedClosed = onClosed
        ad.present(from: root) {
            let item = ad.adReward
            onReward(Shared.AdReward(type: item.type, amount: item.amount.int32Value))
        }
    }

    private func topViewController() -> UIViewController? {
        let scene = UIApplication.shared.connectedScenes.first { $0.activationState == .foregroundActive } as? UIWindowScene
        var top = scene?.windows.first { $0.isKeyWindow }?.rootViewController
        while let presented = top?.presentedViewController {
            top = presented
        }
        return top
    }
}

extension AdMobAdManager: FullScreenContentDelegate {
    func adDidDismissFullScreenContent(_ ad: FullScreenPresentingAd) {
        if ad is InterstitialAd {
            interstitial = nil
            onInterstitialClosed?()
            onInterstitialClosed = nil
        } else if ad is RewardedAd {
            rewarded = nil
            onRewardedClosed?()
            onRewardedClosed = nil
        }
    }

    func ad(_ ad: FullScreenPresentingAd, didFailToPresentFullScreenContentWithError error: Error) {
        adDidDismissFullScreenContent(ad)
    }
}

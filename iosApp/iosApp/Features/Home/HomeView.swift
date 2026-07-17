import SwiftUI
import DesignSystem
import Shared

struct HomeView: View {
    @EnvironmentObject private var ui: AppUiController
    let onOpenProfile: () -> Void
    let onOpenDevTools: () -> Void
    let onLogout: () -> Void

    private let adManager = KoinKt.getAdManager()

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                Text(L("home_success")).font(.title2).bold()
                AppButton(title: L("home_profile")) { onOpenProfile() }
                AppButton(title: L("home_devtools")) { onOpenDevTools() }
                AppButton(title: L("ads_show_interstitial")) {
                    adManager.showInterstitial { adManager.loadInterstitial() }
                }
                AppButton(title: L("ads_show_rewarded")) { showRewarded() }
                Button(L("home_logout")) { confirmLogout() }
                AdBannerView()
                    .frame(height: 50)
                    .frame(maxWidth: .infinity)
                    .background(Color.appFieldBg)
            }
            .padding(24)
        }
        .background(Color.appBackground.ignoresSafeArea())
        .navigationTitle(L("home_title"))
        .onAppear {
            adManager.loadInterstitial()
            adManager.loadRewarded()
        }
    }

    private func showRewarded() {
        adManager.showRewarded(
            onReward: { reward in
                ui.showDialog(
                    AppDialogData(
                        title: L("home_title"),
                        message: String(format: L("ads_reward_earned"), Int(reward.amount)),
                        confirmText: L("common_ok"),
                        dismissText: nil,
                        onConfirm: {}
                    )
                )
            },
            onClosed: { adManager.loadRewarded() }
        )
    }

    private func confirmLogout() {
        ui.showDialog(
            AppDialogData(
                title: L("logout_confirm_title"),
                message: L("logout_confirm_message"),
                confirmText: L("action_confirm"),
                dismissText: L("action_cancel"),
                onConfirm: onLogout
            )
        )
    }
}

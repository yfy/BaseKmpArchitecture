import SwiftUI
import DesignSystem
import Shared

@MainActor
final class ProfileModel: StateScreenModel<ProfileViewModel, ProfileUiState> {
    init() {
        let vm = KoinKt.getProfileViewModel()
        super.init(store: vm, clear: { $0.clear() }, state: profileState(viewModel: vm))
    }
}

struct ProfileView: View {
    @StateObject private var model = ProfileModel()
    let onOpenSettings: () -> Void
    let onGoPremium: () -> Void

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                if model.state.isLoading {
                    ProgressView()
                } else if let user = model.state.user {
                    profileContent(user)
                } else {
                    Text(L("profile_not_found"))
                }
                AppButton(title: L("profile_go_premium")) { onGoPremium() }
                AppButton(title: L("profile_settings")) { onOpenSettings() }
            }
            .padding(24)
        }
        .background(Color.appBackground.ignoresSafeArea())
        .navigationTitle(L("profile_title"))
    }

    @ViewBuilder
    private func profileContent(_ user: AuthUser) -> some View {
        let name = user.displayName ?? user.username
        VStack(spacing: 8) {
            avatar(name)
            Text(name).font(.title).bold()
            Text(user.email).foregroundColor(.appTextSecondary)
            HStack(spacing: 8) {
                badge("crown.fill", user.isPremium ? "profile_premium" : "profile_member")
                if user.isVerified {
                    badge("checkmark.seal.fill", "profile_verified")
                }
            }
        }
        accountCard(user)
    }

    private func avatar(_ name: String) -> some View {
        Text(String(name.prefix(1)).uppercased())
            .font(.system(size: 36, weight: .bold))
            .foregroundColor(.white)
            .frame(width: 88, height: 88)
            .background(Color.appPrimary)
            .clipShape(Circle())
    }

    private func badge(_ systemImage: String, _ text: String) -> some View {
        HStack(spacing: 4) {
            Image(systemName: systemImage)
            Text(L(text)).font(.footnote)
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 6)
        .background(Color.appFieldBg)
        .clipShape(Capsule())
    }

    private func accountCard(_ user: AuthUser) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(L("profile_account")).font(.subheadline).bold()
            infoRow(L("profile_username"), user.username)
            Divider()
            infoRow(L("field_email"), user.email)
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.appFieldBg)
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }

    private func infoRow(_ label: String, _ value: String) -> some View {
        HStack {
            Text(label).foregroundColor(.appTextSecondary)
            Spacer()
            Text(value).fontWeight(.medium)
        }
    }
}

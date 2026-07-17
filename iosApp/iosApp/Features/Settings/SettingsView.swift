import SwiftUI
import DesignSystem
import Shared

@MainActor
final class SettingsModel: StateScreenModel<SettingsViewModel, SettingsUiState> {
    init() {
        let vm = KoinKt.getSettingsViewModel()
        super.init(store: vm, clear: { $0.clear() }, state: settingsState(viewModel: vm))
    }

    func setNotifications(_ v: Bool) { store.setNotificationsEnabled(enabled: v) }
}

struct SettingsView: View {
    @EnvironmentObject private var theme: ThemeController
    @EnvironmentObject private var language: LanguageController
    @StateObject private var model = SettingsModel()
    @State private var notifications = true
    let onChangePassword: () -> Void
    let onTwoFactor: () -> Void

    var body: some View {
        Form {
            Section(L("settings_theme")) {
                HStack(spacing: 8) {
                    themeChip("theme_system", AppThemeMode.system)
                    themeChip("theme_light", AppThemeMode.light)
                    themeChip("theme_dark", AppThemeMode.dark)
                }
            }
            Section(L("settings_language")) {
                HStack(spacing: 8) {
                    languageChip("language_system", AppLanguage.system)
                    languageChip("language_turkish", AppLanguage.tr)
                    languageChip("language_english", AppLanguage.en)
                }
            }
            Section {
                Toggle(L("settings_notifications"), isOn: $notifications)
                    .onChange(of: notifications) { if model.state.notificationsEnabled != $0 { model.setNotifications($0) } }
            }
            Section {
                Button(L("change_password_title")) { onChangePassword() }
                Button(L("two_factor_title")) { onTwoFactor() }
            }
            Section {
                HStack {
                    Text(L("settings_version"))
                    Spacer()
                    Text("1.0").foregroundColor(.appTextSecondary)
                }
            }
        }
        .scrollContentBackground(.hidden)
        .background(Color.appBackground.ignoresSafeArea())
        .navigationTitle(L("settings_title"))
        .onChange(of: model.state.notificationsEnabled) { if notifications != $0 { notifications = $0 } }
    }

    private func themeChip(_ key: String, _ value: AppThemeMode) -> some View {
        let selected = theme.mode == value
        return Button {
            theme.setMode(value)
        } label: {
            chipLabel(key, selected: selected)
        }
        .buttonStyle(.plain)
    }

    private func languageChip(_ key: String, _ value: AppLanguage) -> some View {
        let selected = language.language == value
        return Button {
            language.setLanguage(value)
        } label: {
            chipLabel(key, selected: selected)
        }
        .buttonStyle(.plain)
    }

    private func chipLabel(_ key: String, selected: Bool) -> some View {
        Text(L(key))
            .font(.subheadline)
            .fontWeight(selected ? .semibold : .regular)
            .frame(maxWidth: .infinity)
            .padding(.vertical, 8)
            .background(selected ? Color.appPrimary : Color.appFieldBg)
            .foregroundColor(selected ? .white : .appTextPrimary)
            .clipShape(RoundedRectangle(cornerRadius: 8))
    }
}

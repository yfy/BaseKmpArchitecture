import SwiftUI

struct SettingsDestinationView: View {
    @Binding var path: [AppDestination]

    var body: some View {
        SettingsView(
            onChangePassword: { path.append(.changePassword) },
            onTwoFactor: { path.append(.twoFactor) }
        )
    }
}

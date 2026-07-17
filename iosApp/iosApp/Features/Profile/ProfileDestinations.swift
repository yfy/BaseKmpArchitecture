import SwiftUI

struct ProfileDestinationView: View {
    @Binding var path: [AppDestination]

    var body: some View {
        ProfileView(
            onOpenSettings: { path.append(.settings) },
            onGoPremium: { path.append(.paywall) }
        )
    }
}

import SwiftUI
import Shared

struct HomeDestinationView: View {
    @Binding var path: [AppDestination]

    var body: some View {
        HomeView(
            onOpenProfile: { path.append(.profile(userId: "current")) },
            onOpenDevTools: { path.append(.devTools) },
            onLogout: { Task { try? await KoinKt.getSessionManager().logout(reason: AppEventLoggedOut.shared) } }
        )
    }
}

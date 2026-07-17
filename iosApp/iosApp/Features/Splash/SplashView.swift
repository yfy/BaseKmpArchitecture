import SwiftUI
import UIKit
import DesignSystem

struct SplashView: View {
    var body: some View {
        ZStack {
            Color.appBackground.ignoresSafeArea()
            Image("AppLogo")
                .resizable()
                .scaledToFit()
                .frame(width: 110, height: 110)
        }
        .environment(\.colorScheme, Self.systemColorScheme)
    }

    private static var systemColorScheme: ColorScheme {
        let style = UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .first?
            .screen.traitCollection.userInterfaceStyle
        return style == .dark ? .dark : .light
    }
}

struct RootView: View {
    @State private var showSplash = true

    var body: some View {
        ContentView()
            .overlay {
                if showSplash {
                    SplashView().transition(.opacity)
                }
            }
            .task {
                try? await Task.sleep(nanoseconds: 1_300_000_000)
                withAnimation(.easeOut(duration: 0.3)) { showSplash = false }
            }
    }
}

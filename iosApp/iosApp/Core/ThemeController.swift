import SwiftUI
import Shared

@MainActor
final class ThemeController: ObservableObject {
    @Published var mode: AppThemeMode = AppThemeMode.system

    private let store = KoinKt.getThemeViewModel()
    private var task: Task<Void, Never>?

    init() {
        let flow = themeState(viewModel: store)
        mode = flow.value
        task = Task { [weak self] in
            for await newMode in flow { self?.mode = newMode }
        }
    }

    func setMode(_ newMode: AppThemeMode) {
        store.setMode(mode: newMode)
    }

    var colorScheme: ColorScheme? {
        if mode == AppThemeMode.light { return .light }
        if mode == AppThemeMode.dark { return .dark }
        return nil
    }

    deinit { task?.cancel() }
}

import SwiftUI
import Shared

enum AppL {
    static var bundle: Bundle = .main
}

// Every call site uses L() rather than String(localized:), which resolves against the system
// language and so ignores the in-app language setting.
func L(_ key: String) -> String {
    AppL.bundle.localizedString(forKey: key, value: key, table: nil)
}

// The Kotlin view model is a Koin single, so no VMOwner/clear here — clearing it would cancel a
// scope that must live as long as the app.
@MainActor
final class LanguageController: ObservableObject {
    @Published var language: AppLanguage = AppLanguage.system
    @Published private(set) var refreshID = 0

    private let store = KoinKt.getLanguageViewModel()
    private var task: Task<Void, Never>?

    init() {
        let flow = languageState(viewModel: store)
        // Applied synchronously so the right bundle is in place before the first render.
        let initial = flow.value
        LanguageController.applyBundle(initial)
        language = initial
        task = Task { [weak self] in
            for await newLanguage in flow {
                guard let self else { return }
                LanguageController.applyBundle(newLanguage)
                if self.language != newLanguage {
                    self.language = newLanguage
                    self.refreshID += 1
                }
            }
        }
    }

    func setLanguage(_ newLanguage: AppLanguage) {
        store.setLanguage(language: newLanguage)
    }

    var locale: Locale {
        if let code = language.code { return Locale(identifier: code) }
        return Locale.current
    }

    private static func applyBundle(_ language: AppLanguage) {
        if let code = language.code,
           let path = Bundle.main.path(forResource: code, ofType: "lproj"),
           let bundle = Bundle(path: path) {
            AppL.bundle = bundle
        } else {
            AppL.bundle = .main
        }
    }

    deinit { task?.cancel() }
}

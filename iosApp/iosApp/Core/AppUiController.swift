import SwiftUI
import DesignSystem
import Shared

enum AppMessageType {
    case info, success, error

    var color: Color {
        switch self {
        case .info: return Color(red: 0.13, green: 0.15, blue: 0.18)
        case .success: return .appPrimary
        case .error: return .red
        }
    }
}

struct AppDialogData: Identifiable {
    let id = UUID()
    let title: String
    let message: String
    let confirmText: String
    let dismissText: String?
    let onConfirm: () -> Void
    let onDismiss: () -> Void

    init(
        title: String,
        message: String,
        confirmText: String,
        dismissText: String? = nil,
        onConfirm: @escaping () -> Void = {},
        onDismiss: @escaping () -> Void = {}
    ) {
        self.title = title
        self.message = message
        self.confirmText = confirmText
        self.dismissText = dismissText
        self.onConfirm = onConfirm
        self.onDismiss = onDismiss
    }
}

private struct AppToast: Identifiable {
    let id = UUID()
    let text: String
    let type: AppMessageType
}

@MainActor
final class AppUiController: ObservableObject {
    @Published var loaderVisible = false
    @Published var dialog: AppDialogData?
    @Published fileprivate var toast: AppToast?

    func showLoader() { loaderVisible = true }
    func hideLoader() { loaderVisible = false }

    func showMessage(_ text: String, type: AppMessageType = .info) {
        let item = AppToast(text: text, type: type)
        toast = item
        DispatchQueue.main.asyncAfter(deadline: .now() + 2.5) { [weak self] in
            if self?.toast?.id == item.id { self?.toast = nil }
        }
    }

    func showDialog(_ data: AppDialogData) { dialog = data }
    func dismissDialog() { dialog = nil }
}

private struct AppUiHostModifier: ViewModifier {
    @ObservedObject var controller: AppUiController

    func body(content: Content) -> some View {
        content
            .overlay {
                if controller.loaderVisible {
                    ZStack {
                        Color.black.opacity(0.3).ignoresSafeArea()
                        ProgressView().tint(.white).scaleEffect(1.4)
                    }
                    .allowsHitTesting(true)
                }
            }
            .overlay(alignment: .bottom) {
                if let toast = controller.toast {
                    Text(toast.text)
                        .foregroundColor(.white)
                        .padding(.horizontal, 20)
                        .padding(.vertical, 12)
                        .background(toast.type.color)
                        .clipShape(Capsule())
                        .padding(.bottom, 32)
                        .transition(.move(edge: .bottom).combined(with: .opacity))
                }
            }
            .animation(.easeInOut, value: controller.toast?.id)
            .alert(
                controller.dialog?.title ?? "",
                isPresented: Binding(
                    get: { controller.dialog != nil },
                    set: { if !$0 { controller.dialog = nil } }
                ),
                presenting: controller.dialog
            ) { data in
                Button(data.confirmText) { data.onConfirm() }
                if let dismiss = data.dismissText {
                    Button(dismiss, role: .cancel) { data.onDismiss() }
                }
            } message: { data in
                Text(data.message)
            }
    }
}

extension View {
    func appUiHost(_ controller: AppUiController) -> some View {
        modifier(AppUiHostModifier(controller: controller))
    }
}

extension AppError {
    var localizedMessage: String {
        switch self {
        case is AppError.Network: return L("error_network")
        case is AppError.Server: return L("error_server")
        case is AppError.Unauthorized: return L("error_unauthorized")
        default: return L("error_unknown")
        }
    }
}

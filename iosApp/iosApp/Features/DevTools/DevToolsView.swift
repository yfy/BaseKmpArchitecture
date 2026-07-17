import SwiftUI
import DesignSystem
import Shared

struct DevToolsView: View {
    @EnvironmentObject private var ui: AppUiController

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                AppButton(title: L("devtools_show_info")) {
                    ui.showMessage(L("devtools_info_msg"), type: .info)
                }
                AppButton(title: L("devtools_show_success")) {
                    ui.showMessage(L("devtools_success_msg"), type: .success)
                }
                AppButton(title: L("devtools_show_error")) {
                    ui.showMessage(L("devtools_error_msg"), type: .error)
                }
                AppButton(title: L("devtools_show_dialog")) {
                    ui.showDialog(AppDialogData(
                        title: L("devtools_dialog_title"),
                        message: L("devtools_dialog_message"),
                        confirmText: L("common_ok")
                    ))
                }
                AppButton(title: L("devtools_show_loader")) {
                    ui.showLoader()
                    DispatchQueue.main.asyncAfter(deadline: .now() + 3) { ui.hideLoader() }
                }
            }
            .padding(24)
        }
        .background(Color.appBackground.ignoresSafeArea())
        .navigationTitle(L("devtools_title"))
    }
}

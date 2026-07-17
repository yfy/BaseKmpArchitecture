import SwiftUI
import DesignSystem
import Shared

@MainActor
final class OnboardingModel: StateScreenModel<OnboardingViewModel, OnboardingUiState> {
    var pageCount: Int { Int(store.pageCount) }

    init() {
        let vm = KoinKt.getOnboardingViewModel()
        super.init(store: vm, clear: { $0.clear() }, state: onboardingState(viewModel: vm))
    }

    func next() { store.next() }
    func skip() { store.skip() }
}

struct OnboardingView: View {
    @StateObject private var model = OnboardingModel()
    let onFinished: () -> Void

    private let pages = [
        ("onboarding_page1_title", "onboarding_page1_desc", "sparkles"),
        ("onboarding_page2_title", "onboarding_page2_desc", "bolt.fill"),
        ("onboarding_page3_title", "onboarding_page3_desc", "checkmark.seal.fill"),
    ]

    var body: some View {
        GeometryReader { geo in
            let index = min(max(Int(model.state.pageIndex), 0), pages.count - 1)
            let isLast = index == model.pageCount - 1
            VStack(spacing: 0) {
                ZStack(alignment: .topTrailing) {
                    Color.appOnboardingBg
                    Image(systemName: pages[index].2)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 140, height: 140)
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                    Button(L("onboarding_skip")) { model.skip() }
                        .foregroundColor(.white)
                        .fontWeight(.semibold)
                        .padding(.top, 56)
                        .padding(.trailing, 20)
                }
                .frame(height: geo.size.height * 0.58)

                VStack(spacing: 0) {
                    Spacer()
                    Text(L(pages[index].0))
                        .font(.title).bold().multilineTextAlignment(.center)
                    Text(L(pages[index].1))
                        .foregroundColor(.appTextSecondary)
                        .multilineTextAlignment(.center)
                        .padding(.top, 12)
                    AppPageIndicator(count: model.pageCount, selected: index).padding(.top, 24)
                    Spacer()
                    HStack {
                        Spacer()
                        Button { model.next() } label: {
                            HStack(spacing: 8) {
                                Text(isLast ? L("onboarding_start") : L("onboarding_next"))
                                Image(systemName: "arrow.right")
                            }
                            .foregroundColor(.white)
                            .padding(.horizontal, 24)
                            .frame(height: 52)
                        }
                        .background(Color.appPrimary)
                        .clipShape(Capsule())
                    }
                }
                .padding(.horizontal, 32)
                .frame(maxHeight: .infinity)
            }
            .ignoresSafeArea(edges: .top)
        }
        .onChange(of: model.state.completed) { if $0 { onFinished() } }
    }
}

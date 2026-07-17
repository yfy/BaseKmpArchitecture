import SwiftUI

public extension View {
    func appScreenBackground() -> some View {
        background(
            LinearGradient(colors: [.appGradientTop, .appGradientBottom], startPoint: .top, endPoint: .bottom)
                .ignoresSafeArea()
        )
    }
}

private struct RoundedFieldBackground: ViewModifier {
    func body(content: Content) -> some View {
        content
            .padding(14)
            .background(Color.appFieldBg)
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.appFieldBorder, lineWidth: 1))
    }
}

public struct AppTextField: View {
    let label: String
    @Binding var text: String
    var keyboard: UIKeyboardType

    public init(label: String, text: Binding<String>, keyboard: UIKeyboardType = .default) {
        self.label = label
        self._text = text
        self.keyboard = keyboard
    }

    public var body: some View {
        TextField("", text: $text, prompt: Text(label).foregroundColor(.appTextSecondary))
            .keyboardType(keyboard)
            .autocapitalization(.none)
            .autocorrectionDisabled()
            .modifier(RoundedFieldBackground())
    }
}

public struct AppPasswordField: View {
    let label: String
    @Binding var text: String
    @State private var visible = false

    public init(label: String, text: Binding<String>) {
        self.label = label
        self._text = text
    }

    public var body: some View {
        HStack {
            Group {
                if visible {
                    TextField("", text: $text, prompt: Text(label).foregroundColor(.appTextSecondary))
                } else {
                    SecureField("", text: $text, prompt: Text(label).foregroundColor(.appTextSecondary))
                }
            }
            .autocapitalization(.none)
            .autocorrectionDisabled()
            Button { visible.toggle() } label: {
                Image(systemName: visible ? "eye.slash" : "eye").foregroundColor(.appTextSecondary)
            }
        }
        .modifier(RoundedFieldBackground())
    }
}

public struct AppButton: View {
    let title: String
    var loading: Bool
    var enabled: Bool
    let action: () -> Void

    public init(title: String, loading: Bool = false, enabled: Bool = true, action: @escaping () -> Void) {
        self.title = title
        self.loading = loading
        self.enabled = enabled
        self.action = action
    }

    public var body: some View {
        Button(action: action) {
            ZStack {
                if loading {
                    ProgressView().tint(.white)
                } else {
                    Text(title).fontWeight(.semibold).foregroundColor(.white)
                }
            }
            .frame(maxWidth: .infinity).frame(height: 54)
        }
        .background(Color.appPrimary.opacity(enabled && !loading ? 1 : 0.5))
        .clipShape(RoundedRectangle(cornerRadius: 27))
        .disabled(loading || !enabled)
    }
}

public struct AppOutlinedButton<Leading: View>: View {
    let title: String
    let action: () -> Void
    @ViewBuilder var leading: () -> Leading

    public init(title: String, action: @escaping () -> Void, @ViewBuilder leading: @escaping () -> Leading) {
        self.title = title
        self.action = action
        self.leading = leading
    }

    public var body: some View {
        Button(action: action) {
            HStack(spacing: 10) {
                leading()
                Text(title).foregroundColor(.appTextPrimary)
            }
            .frame(maxWidth: .infinity).frame(height: 54)
        }
        .overlay(RoundedRectangle(cornerRadius: 27).stroke(Color.appFieldBorder, lineWidth: 1))
    }
}

public struct AppCheckboxRow<Content: View>: View {
    @Binding var checked: Bool
    @ViewBuilder var content: () -> Content

    public init(checked: Binding<Bool>, @ViewBuilder content: @escaping () -> Content) {
        self._checked = checked
        self.content = content
    }

    public var body: some View {
        HStack(alignment: .center, spacing: 8) {
            Button { checked.toggle() } label: {
                Image(systemName: checked ? "checkmark.square.fill" : "square")
                    .foregroundColor(checked ? .appPrimary : .appTextSecondary)
            }
            content()
            Spacer(minLength: 0)
        }
    }
}

public struct AppPageIndicator: View {
    let count: Int
    let selected: Int

    public init(count: Int, selected: Int) {
        self.count = count
        self.selected = selected
    }

    public var body: some View {
        HStack(spacing: 8) {
            ForEach(0..<count, id: \.self) { i in
                Capsule()
                    .fill(i == selected ? Color.appPrimary : Color.appFieldBorder)
                    .frame(width: i == selected ? 24 : 8, height: 8)
            }
        }
    }
}

public struct AppOrDivider: View {
    let text: String

    public init(text: String) {
        self.text = text
    }

    public var body: some View {
        HStack {
            Rectangle().fill(Color.appFieldBorder).frame(height: 1)
            Text(text).foregroundColor(.appTextSecondary).padding(.horizontal, 12)
            Rectangle().fill(Color.appFieldBorder).frame(height: 1)
        }
    }
}

public struct AppErrorText: View {
    let message: String

    public init(message: String) {
        self.message = message
    }

    public var body: some View { Text(message).foregroundColor(.red) }
}

public struct AppSuccessText: View {
    let message: String

    public init(message: String) {
        self.message = message
    }

    public var body: some View { Text(message).foregroundColor(.green) }
}

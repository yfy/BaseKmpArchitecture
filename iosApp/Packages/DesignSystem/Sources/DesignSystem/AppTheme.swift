import SwiftUI
import UIKit

private extension UIColor {
    convenience init(hex: UInt32) {
        self.init(
            red: CGFloat((hex >> 16) & 0xFF) / 255,
            green: CGFloat((hex >> 8) & 0xFF) / 255,
            blue: CGFloat(hex & 0xFF) / 255,
            alpha: 1
        )
    }
}

private func dyn(_ light: UInt32, _ dark: UInt32) -> Color {
    Color(UIColor { trait in
        trait.userInterfaceStyle == .dark ? UIColor(hex: dark) : UIColor(hex: light)
    })
}

public extension Color {
    static let appPrimary = dyn(DesignTokens.primaryLight, DesignTokens.primaryDark)
    static let appOnboardingBg = Color(UIColor(hex: DesignTokens.onboardingBgLight))
    static let appBackground = dyn(DesignTokens.backgroundLight, DesignTokens.backgroundDark)
    static let appGradientTop = dyn(DesignTokens.gradientTopLight, DesignTokens.gradientTopDark)
    static let appGradientBottom = dyn(DesignTokens.gradientBottomLight, DesignTokens.gradientBottomDark)
    static let appTextPrimary = dyn(DesignTokens.textPrimaryLight, DesignTokens.textPrimaryDark)
    static let appTextSecondary = dyn(DesignTokens.textSecondaryLight, DesignTokens.textSecondaryDark)
    static let appFieldBorder = dyn(DesignTokens.fieldBorderLight, DesignTokens.fieldBorderDark)
    static let appFieldBg = dyn(DesignTokens.fieldBgLight, DesignTokens.fieldBgDark)
    static let appSocialGoogleBg = dyn(DesignTokens.socialGoogleBgLight, DesignTokens.socialGoogleBgDark)
    static let appSocialGoogleText = dyn(DesignTokens.socialGoogleTextLight, DesignTokens.socialGoogleTextDark)
    static let appSocialGoogleBorder = dyn(DesignTokens.socialGoogleBorderLight, DesignTokens.socialGoogleBorderDark)
    static let appSocialAppleBg = dyn(DesignTokens.socialAppleBgLight, DesignTokens.socialAppleBgDark)
    static let appSocialAppleText = dyn(DesignTokens.socialAppleTextLight, DesignTokens.socialAppleTextDark)
}

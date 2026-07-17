package com.yfy.kmp.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// surface deliberately equals background so auth and inner screens share one base; cards and fields
// separate themselves with surfaceVariant instead.
private val AppLightColors = lightColorScheme(
    primary = AppPrimary,
    onPrimary = Color(0xFFFFFFFF),
    background = AppGradientBottom,
    onBackground = AppTextPrimary,
    surface = AppGradientBottom,
    onSurface = AppTextPrimary,
    surfaceVariant = AppFieldBg,
    onSurfaceVariant = AppTextSecondary,
    outline = AppFieldBorder,
)

private val AppDarkColors = darkColorScheme(
    primary = AppPrimaryDark,
    onPrimary = Color(0xFF06121E),
    background = AppBackgroundDark,
    onBackground = AppTextPrimaryDark,
    surface = AppBackgroundDark,
    onSurface = AppTextPrimaryDark,
    surfaceVariant = AppSurfaceDark,
    onSurfaceVariant = AppTextSecondaryDark,
    outline = AppFieldBorderDark,
)

data class AppExtraColors(
    val gradientTop: Color,
    val gradientBottom: Color,
    val onboardingBg: Color,
    val socialGoogleBg: Color,
    val socialGoogleText: Color,
    val socialGoogleBorder: Color,
    val socialAppleBg: Color,
    val socialAppleText: Color,
)

private val LightExtra = AppExtraColors(
    gradientTop = AppGradientTop,
    gradientBottom = AppGradientBottom,
    onboardingBg = AppOnboardingBg,
    socialGoogleBg = DesignTokens.socialGoogleBgLight,
    socialGoogleText = DesignTokens.socialGoogleTextLight,
    socialGoogleBorder = DesignTokens.socialGoogleBorderLight,
    socialAppleBg = DesignTokens.socialAppleBgLight,
    socialAppleText = DesignTokens.socialAppleTextLight,
)
private val DarkExtra = AppExtraColors(
    gradientTop = AppGradientTopDark,
    gradientBottom = AppGradientBottomDark,
    onboardingBg = AppOnboardingBg,
    socialGoogleBg = DesignTokens.socialGoogleBgDark,
    socialGoogleText = DesignTokens.socialGoogleTextDark,
    socialGoogleBorder = DesignTokens.socialGoogleBorderDark,
    socialAppleBg = DesignTokens.socialAppleBgDark,
    socialAppleText = DesignTokens.socialAppleTextDark,
)

val LocalAppExtraColors = staticCompositionLocalOf { LightExtra }

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) AppDarkColors else AppLightColors
    val extra = if (darkTheme) DarkExtra else LightExtra
    CompositionLocalProvider(LocalAppExtraColors provides extra) {
        MaterialTheme(
            colorScheme = colors,
            content = content,
        )
    }
}

package com.yfy.kmp.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public sealed interface AppRoute {
    @Serializable
    @SerialName("onboarding")
    public data object Onboarding : AppRoute

    @Serializable
    @SerialName("login")
    public data object Login : AppRoute

    @Serializable
    @SerialName("signup")
    public data object Signup : AppRoute

    @Serializable
    @SerialName("forgot_password")
    public data object ForgotPassword : AppRoute

    @Serializable
    @SerialName("email_verify")
    public data class EmailVerify(val email: String) : AppRoute

    @Serializable
    @SerialName("reset_password")
    public data class ResetPassword(val token: String) : AppRoute

    @Serializable
    @SerialName("home")
    public data object Home : AppRoute

    @Serializable
    @SerialName("profile")
    public data class Profile(val userId: String) : AppRoute

    @Serializable
    @SerialName("settings")
    public data object Settings : AppRoute
}

// TODO(template): replace before release — the "yfy" deep-link scheme; keep in sync with the Android
// manifest scheme and the iOS Info.plist CFBundleURLSchemes.
public fun AppRoute.toUri(): String = when (this) {
    AppRoute.Onboarding -> "yfy://onboarding"
    AppRoute.Login -> "yfy://login"
    AppRoute.Signup -> "yfy://signup"
    AppRoute.ForgotPassword -> "yfy://forgot-password"
    is AppRoute.EmailVerify -> "yfy://email-verify/$email"
    is AppRoute.ResetPassword -> "yfy://reset-password/$token"
    AppRoute.Home -> "yfy://home"
    is AppRoute.Profile -> "yfy://profile/$userId"
    AppRoute.Settings -> "yfy://settings"
}

public fun parseAppRoute(
    uri: String? = null,
    payload: Map<String, String> = emptyMap(),
): AppRoute? {
    val raw = uri ?: payload["route"] ?: return null
    val scheme = if ("://" in raw) raw.substringBefore("://").lowercase() else ""
    val afterScheme = if ("://" in raw) raw.substringAfter("://") else raw
    val pathOnly = afterScheme.substringBefore('?').trim('/')

    var segments = pathOnly.split('/').filter { it.isNotEmpty() }
    // A universal link (https://host/route) starts with the host; a custom scheme (yfy://route) does not.
    if (scheme == "http" || scheme == "https") segments = segments.drop(1)

    return when (segments.firstOrNull()?.lowercase()) {
        null -> null
        "onboarding" -> AppRoute.Onboarding
        "login" -> AppRoute.Login
        "signup" -> AppRoute.Signup
        "forgot-password" -> AppRoute.ForgotPassword
        "reset-password" -> {
            val token = segments.getOrNull(1) ?: payload["token"]
            token?.takeIf { it.isNotBlank() }?.let { AppRoute.ResetPassword(it) }
        }
        "email-verify" -> {
            val email = segments.getOrNull(1) ?: payload["email"]
            email?.takeIf { it.isNotBlank() }?.let { AppRoute.EmailVerify(it) }
        }
        "home" -> AppRoute.Home
        "profile" -> {
            val userId = segments.getOrNull(1) ?: payload["userId"]
            userId?.takeIf { it.isNotBlank() }?.let { AppRoute.Profile(it) }
        }
        "settings" -> AppRoute.Settings
        else -> null
    }
}

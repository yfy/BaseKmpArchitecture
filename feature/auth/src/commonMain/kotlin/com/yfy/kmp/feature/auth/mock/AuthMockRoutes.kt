package com.yfy.kmp.feature.auth.mock

import com.yfy.kmp.core.network.mock.MockRoute

public val authMockRoutes: List<MockRoute> = listOf(
    MockRoute(pathSuffix = "/auth/login", jsonName = "login_response"),
    MockRoute(pathSuffix = "/auth/social-login", jsonName = "login_response"),
    MockRoute(pathSuffix = "/auth/signup", jsonName = "signup_response"),
    MockRoute(pathSuffix = "/auth/forgot-password", jsonName = "ok_response"),
    MockRoute(pathSuffix = "/auth/verify-email", jsonName = "ok_response"),
    MockRoute(pathSuffix = "/auth/change-password", jsonName = "ok_response"),
    MockRoute(pathSuffix = "/auth/reset-password", jsonName = "ok_response"),
    MockRoute(pathSuffix = "/auth/2fa/enable", jsonName = "two_factor_setup"),
    MockRoute(pathSuffix = "/auth/2fa/verify", jsonName = "ok_response"),
    MockRoute(pathSuffix = "/auth/2fa/disable", jsonName = "ok_response"),
)

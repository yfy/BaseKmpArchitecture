package com.yfy.kmp.core.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AppRouteTest {

    @Test
    fun deep_link_uri_parses_to_route() {
        assertEquals(AppRoute.Login, parseAppRoute(uri = "yfy://login"))
        assertEquals(AppRoute.Signup, parseAppRoute(uri = "yfy://signup"))
        assertEquals(AppRoute.ForgotPassword, parseAppRoute(uri = "yfy://forgot-password"))
        assertEquals(AppRoute.EmailVerify("a@b.c"), parseAppRoute(uri = "yfy://email-verify/a@b.c"))
        assertEquals(AppRoute.ResetPassword("tok_9"), parseAppRoute(uri = "yfy://reset-password/tok_9"))
        assertEquals(AppRoute.Home, parseAppRoute(uri = "yfy://home"))
        assertEquals(AppRoute.Profile("u_42"), parseAppRoute(uri = "yfy://profile/u_42"))
    }

    @Test
    fun uri_with_query_and_trailing_slash_still_parses() {
        assertEquals(AppRoute.Home, parseAppRoute(uri = "yfy://home/?utm=push"))
        assertEquals(AppRoute.Profile("u_1"), parseAppRoute(uri = "yfy://profile/u_1/"))
    }

    @Test
    fun push_payload_parses_to_route() {
        assertEquals(AppRoute.Home, parseAppRoute(payload = mapOf("route" to "home")))
        assertEquals(
            AppRoute.Profile("u_7"),
            parseAppRoute(payload = mapOf("route" to "profile", "userId" to "u_7")),
        )
    }

    @Test
    fun uri_takes_precedence_over_payload() {
        assertEquals(
            AppRoute.Login,
            parseAppRoute(uri = "yfy://login", payload = mapOf("route" to "home")),
        )
    }

    @Test
    fun case_is_normalized() {
        assertEquals(AppRoute.Home, parseAppRoute(uri = "YFY://HOME"))
    }

    @Test
    fun toUri_round_trips_through_parser() {
        val routes = listOf(
            AppRoute.Onboarding, AppRoute.Login, AppRoute.Signup, AppRoute.ForgotPassword,
            AppRoute.EmailVerify("a@b.c"), AppRoute.ResetPassword("tok_1"),
            AppRoute.Home, AppRoute.Profile("u_9"), AppRoute.Settings,
        )
        routes.forEach { route ->
            assertEquals(route, parseAppRoute(uri = route.toUri()))
        }
    }

    @Test
    fun universal_link_https_parses_to_route() {
        assertEquals(AppRoute.Login, parseAppRoute(uri = "https://example.com/login"))
        assertEquals(AppRoute.Home, parseAppRoute(uri = "https://example.com/home?utm=mail"))
        assertEquals(AppRoute.Profile("u_42"), parseAppRoute(uri = "https://example.com/profile/u_42"))
        assertEquals(AppRoute.EmailVerify("a@b.c"), parseAppRoute(uri = "https://example.com/email-verify/a@b.c"))
        assertNull(parseAppRoute(uri = "https://example.com/"))
        assertNull(parseAppRoute(uri = "https://example.com/unknown"))
    }

    @Test
    fun invalid_input_returns_null() {
        assertNull(parseAppRoute())
        assertNull(parseAppRoute(uri = "yfy://"))
        assertNull(parseAppRoute(uri = "yfy://unknown"))
        assertNull(parseAppRoute(uri = "yfy://profile"))
        assertNull(parseAppRoute(payload = mapOf("route" to "profile")))
        assertNull(parseAppRoute(payload = mapOf("other" to "x")))
    }
}

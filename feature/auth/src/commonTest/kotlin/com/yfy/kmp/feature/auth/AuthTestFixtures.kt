package com.yfy.kmp.feature.auth

import com.yfy.kmp.core.common.auth.AuthTokens
import com.yfy.kmp.core.common.auth.TokenStore
import com.yfy.kmp.core.network.createHttpClient
import com.yfy.kmp.core.network.mock.MockJsonReader
import com.yfy.kmp.core.network.mock.MockRoute
import com.yfy.kmp.core.network.mock.mockEngine
import com.yfy.kmp.feature.auth.mock.authMockRoutes
import io.ktor.client.HttpClient

internal const val LOGIN_RESPONSE_JSON: String = """
    {
        "id":"u_1001",
        "username":"yfy",
        "email":"demo@yfy.dev",
        "displayName":"YFY Demo",
        "isVerified":true,
        "isPremium":false
    }
"""

private val fakeReader = object : MockJsonReader {
    override fun read(name: String): String = when (name) {
        "ok_response" -> """{ "ok": true }"""
        "two_factor_setup" -> """{ "secret": "YFY-DUMMY-2FA-SECRET-ABCD" }"""
        "error_response" -> """{ "error": true }"""
        else -> LOGIN_RESPONSE_JSON
    }
}

internal fun mockAuthClient(): HttpClient = createHttpClient(mockEngine(fakeReader, authMockRoutes))

internal fun mockAuthClient(routes: List<MockRoute>): HttpClient = createHttpClient(mockEngine(fakeReader, routes))

internal class InMemoryTokenStore : TokenStore {
    var saved: AuthTokens? = null
    override suspend fun tokens(): AuthTokens? = saved
    override suspend fun save(tokens: AuthTokens) { saved = tokens }
    override suspend fun clear() { saved = null }
}

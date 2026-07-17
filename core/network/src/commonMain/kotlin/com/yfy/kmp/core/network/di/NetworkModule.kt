package com.yfy.kmp.core.network.di

import com.yfy.kmp.core.common.auth.TokenStore
import com.yfy.kmp.core.common.event.AppEvent
import com.yfy.kmp.core.common.session.SessionManager
import com.yfy.kmp.core.network.AuthConfig
import com.yfy.kmp.core.network.NetworkConfig
import com.yfy.kmp.core.network.createHttpClient
import com.yfy.kmp.core.network.mock.MockJsonReader
import com.yfy.kmp.core.network.mock.MockRoute
import com.yfy.kmp.core.network.mock.mockEngine
import com.yfy.kmp.core.network.mock.platformMockModule
import com.yfy.kmp.core.network.platformHttpEngine
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module

public fun mockNetworkModule(routes: List<MockRoute>): Module = module {
    includes(platformMockModule)
    single { NetworkConfig(baseUrl = "https://mock.local") }
    single<HttpClient> { createHttpClient(mockEngine(get<MockJsonReader>(), routes, delayMillis = 400)) }
}

// TODO(template): replace before release — test API base URL.
public val debugNetworkModule: Module = module {
    single { NetworkConfig(baseUrl = "https://api-test.example.com") }
    single<HttpClient> { createHttpClient(platformHttpEngine(), authConfig(get(), get(), get()), get()) }
}

// TODO(template): replace before release — production API base URL.
public val prodNetworkModule: Module = module {
    single { NetworkConfig(baseUrl = "https://api.example.com") }
    single<HttpClient> { createHttpClient(platformHttpEngine(), authConfig(get(), get(), get()), get()) }
}

private fun authConfig(config: NetworkConfig, session: SessionManager, tokenStore: TokenStore): AuthConfig =
    AuthConfig(
        tokenStore = tokenStore,
        refreshUrl = "${config.baseUrl}/auth/refresh",
        onSessionExpired = { session.logout(AppEvent.SessionExpired) },
    )

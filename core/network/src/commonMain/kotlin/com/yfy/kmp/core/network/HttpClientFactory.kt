package com.yfy.kmp.core.network

import com.yfy.kmp.core.common.auth.AuthTokens
import com.yfy.kmp.core.common.auth.TokenStore
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

public expect fun platformHttpEngine(): HttpClientEngine

public class AuthConfig(
    public val tokenStore: TokenStore,
    public val refreshUrl: String,
    public val onSessionExpired: suspend () -> Unit,
)

@Serializable
private data class RefreshRequestDto(val refreshToken: String)

@Serializable
private data class RefreshResponseDto(val accessToken: String, val refreshToken: String)

public fun createHttpClient(
    engine: HttpClientEngine,
    auth: AuthConfig? = null,
    connectivity: ConnectivityChecker? = null,
): HttpClient = HttpClient(engine) {
    expectSuccess = true
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
        )
    }
    install(Logging) {
        level = LogLevel.INFO
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
    }
    install(HttpRequestRetry) {
        retryOnServerErrors(maxRetries = 3)
        retryOnException(maxRetries = 3, retryOnTimeout = true)
        exponentialDelay()
    }
    if (connectivity != null) {
        install(ConnectivityPlugin) { checker = connectivity }
    }
    if (auth != null) {
        install(Auth) {
            bearer {
                loadTokens {
                    auth.tokenStore.tokens()?.let { BearerTokens(it.accessToken, it.refreshToken) }
                }
                refreshTokens {
                    val current = auth.tokenStore.tokens()
                    if (current == null) {
                        auth.onSessionExpired()
                        return@refreshTokens null
                    }
                    try {
                        val dto: RefreshResponseDto = client.post(auth.refreshUrl) {
                            // Without this the bearer plugin re-enters refresh on this call's own 401.
                            markAsRefreshTokenRequest()
                            contentType(ContentType.Application.Json)
                            setBody(RefreshRequestDto(current.refreshToken))
                        }.body()
                        auth.tokenStore.save(AuthTokens(dto.accessToken, dto.refreshToken))
                        BearerTokens(dto.accessToken, dto.refreshToken)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        auth.onSessionExpired()
                        null
                    }
                }
            }
        }
    }
}

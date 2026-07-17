package com.yfy.kmp.core.common.auth

public data class AuthTokens(
    public val accessToken: String,
    public val refreshToken: String,
)

public interface TokenStore {
    public suspend fun tokens(): AuthTokens?
    public suspend fun save(tokens: AuthTokens)
    public suspend fun clear()
}

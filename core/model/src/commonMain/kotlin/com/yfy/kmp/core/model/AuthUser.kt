package com.yfy.kmp.core.model

import kotlinx.serialization.Serializable

@Serializable
public data class AuthUser(
    val id: String,
    val username: String,
    val email: String,
    val displayName: String? = null,
    val avatarUrl: String? = null,
    val isVerified: Boolean = false,
    val isPremium: Boolean = false,
)

@Serializable
public data class UserSession(
    val accessToken: String,
    val refreshToken: String? = null,
    val userId: String,
    val expiresAt: Long,
) {
    public fun isValid(nowEpochMillis: Long): Boolean = expiresAt > nowEpochMillis
}

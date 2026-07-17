package com.yfy.kmp.feature.auth.data

import kotlinx.serialization.Serializable

@Serializable
internal data class LoginRequestDto(val email: String, val password: String)

@Serializable
internal data class SocialLoginRequestDto(val provider: String, val token: String)

@Serializable
internal data class LoginResponseDto(
    val id: String,
    val username: String,
    val email: String,
    val displayName: String? = null,
    val avatarUrl: String? = null,
    val isVerified: Boolean = false,
    val isPremium: Boolean = false,
    val accessToken: String? = null,
    val refreshToken: String? = null,
)

@Serializable
internal data class SignupRequestDto(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
)

@Serializable
internal data class ForgotPasswordRequestDto(val email: String)

@Serializable
internal data class VerifyEmailRequestDto(val email: String, val code: String)

@Serializable
internal data class ChangePasswordRequestDto(val currentPassword: String, val newPassword: String)

@Serializable
internal data class ResetPasswordRequestDto(val token: String, val newPassword: String)

@Serializable
internal data class TwoFactorVerifyRequestDto(val code: String)

@Serializable
internal data class TwoFactorSetupDto(val secret: String)

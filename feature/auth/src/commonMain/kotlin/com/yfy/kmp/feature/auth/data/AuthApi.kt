package com.yfy.kmp.feature.auth.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class AuthApi(
    private val client: HttpClient,
    private val baseUrl: String,
) {
    private suspend inline fun <reified T> post(path: String, body: Any? = null): T =
        client.post("$baseUrl$path") {
            contentType(ContentType.Application.Json)
            body?.let { setBody(it) }
        }.body()

    suspend fun login(body: LoginRequestDto): LoginResponseDto = post("/auth/login", body)

    suspend fun socialLogin(body: SocialLoginRequestDto): LoginResponseDto = post("/auth/social-login", body)

    suspend fun signup(body: SignupRequestDto): LoginResponseDto = post("/auth/signup", body)

    suspend fun forgotPassword(body: ForgotPasswordRequestDto): Unit = post("/auth/forgot-password", body)

    suspend fun verifyEmail(body: VerifyEmailRequestDto): Unit = post("/auth/verify-email", body)

    suspend fun changePassword(body: ChangePasswordRequestDto): Unit = post("/auth/change-password", body)

    suspend fun resetPassword(body: ResetPasswordRequestDto): Unit = post("/auth/reset-password", body)

    suspend fun twoFactorEnable(): TwoFactorSetupDto = post("/auth/2fa/enable")

    suspend fun twoFactorVerify(body: TwoFactorVerifyRequestDto): Unit = post("/auth/2fa/verify", body)

    suspend fun twoFactorDisable(): Unit = post("/auth/2fa/disable")
}

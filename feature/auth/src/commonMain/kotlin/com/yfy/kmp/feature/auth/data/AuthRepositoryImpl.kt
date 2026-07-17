package com.yfy.kmp.feature.auth.data

import com.yfy.kmp.core.common.auth.AuthTokens
import com.yfy.kmp.core.common.auth.TokenStore
import com.yfy.kmp.core.database.UserCache
import com.yfy.kmp.core.model.AuthUser
import com.yfy.kmp.core.network.sendRequest
import com.yfy.kmp.feature.auth.domain.AuthRepository
import com.yfy.kmp.feature.auth.domain.ChangePasswordOutcome
import com.yfy.kmp.feature.auth.domain.EmailVerifyOutcome
import com.yfy.kmp.feature.auth.domain.EmailVerifyParams
import com.yfy.kmp.feature.auth.domain.ForgotPasswordOutcome
import com.yfy.kmp.feature.auth.domain.LoginOutcome
import com.yfy.kmp.feature.auth.domain.ResetPasswordOutcome
import com.yfy.kmp.feature.auth.domain.SignupOutcome
import com.yfy.kmp.feature.auth.domain.SignupParams
import com.yfy.kmp.feature.auth.domain.SocialProvider
import com.yfy.kmp.feature.auth.domain.TwoFactorOutcome
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow

internal class AuthRepositoryImpl(
    private val api: AuthApi,
    private val userCache: UserCache,
    private val tokenStore: TokenStore,
) : AuthRepository {

    private suspend fun authenticated(dto: LoginResponseDto): LoginOutcome.Success {
        val access = dto.accessToken
        val refresh = dto.refreshToken
        if (access != null && refresh != null) {
            tokenStore.save(AuthTokens(access, refresh))
        }
        val user = dto.toDomain()
        userCache.upsert(user)
        return LoginOutcome.Success(user)
    }

    override fun login(email: String, password: String): Flow<LoginOutcome> =
        sendRequest(recover = { if (it == HttpStatusCode.Unauthorized) LoginOutcome.InvalidCredentials else null }) {
            authenticated(api.login(LoginRequestDto(email, password)))
        }

    override fun socialLogin(provider: SocialProvider, token: String): Flow<LoginOutcome> =
        sendRequest(recover = { if (it == HttpStatusCode.Unauthorized) LoginOutcome.InvalidCredentials else null }) {
            authenticated(api.socialLogin(SocialLoginRequestDto(provider = provider.name, token = token)))
        }

    override fun signup(params: SignupParams): Flow<SignupOutcome> =
        sendRequest(recover = { if (it == HttpStatusCode.Conflict) SignupOutcome.EmailTaken else null }) {
            val success = authenticated(
                api.signup(SignupRequestDto(params.firstName, params.lastName, params.email, params.password)),
            )
            SignupOutcome.Success(success.user)
        }

    override fun forgotPassword(email: String): Flow<ForgotPasswordOutcome> =
        sendRequest {
            api.forgotPassword(ForgotPasswordRequestDto(email))
            ForgotPasswordOutcome.Success
        }

    override fun verifyEmail(params: EmailVerifyParams): Flow<EmailVerifyOutcome> =
        sendRequest(recover = { if (it == HttpStatusCode.UnprocessableEntity) EmailVerifyOutcome.InvalidCode else null }) {
            api.verifyEmail(VerifyEmailRequestDto(params.email, params.code))
            EmailVerifyOutcome.Success
        }

    override fun changePassword(currentPassword: String, newPassword: String): Flow<ChangePasswordOutcome> =
        sendRequest(recover = {
            if (it == HttpStatusCode.Unauthorized || it == HttpStatusCode.Forbidden) {
                ChangePasswordOutcome.WrongCurrent
            } else {
                null
            }
        }) {
            api.changePassword(ChangePasswordRequestDto(currentPassword, newPassword))
            ChangePasswordOutcome.Success
        }

    override fun resetPassword(token: String, newPassword: String): Flow<ResetPasswordOutcome> =
        sendRequest(recover = {
            if (it == HttpStatusCode.UnprocessableEntity || it == HttpStatusCode.Gone) {
                ResetPasswordOutcome.InvalidToken
            } else {
                null
            }
        }) {
            api.resetPassword(ResetPasswordRequestDto(token, newPassword))
            ResetPasswordOutcome.Success
        }

    override fun twoFactorEnable(): Flow<TwoFactorOutcome> =
        sendRequest { TwoFactorOutcome.SetupReady(api.twoFactorEnable().secret) }

    override fun twoFactorVerify(code: String): Flow<TwoFactorOutcome> =
        sendRequest(recover = { if (it == HttpStatusCode.UnprocessableEntity) TwoFactorOutcome.InvalidCode else null }) {
            api.twoFactorVerify(TwoFactorVerifyRequestDto(code))
            TwoFactorOutcome.Enabled
        }

    override fun twoFactorDisable(): Flow<TwoFactorOutcome> =
        sendRequest {
            api.twoFactorDisable()
            TwoFactorOutcome.Disabled
        }
}

private fun LoginResponseDto.toDomain() = AuthUser(
    id = id,
    username = username,
    email = email,
    displayName = displayName,
    avatarUrl = avatarUrl,
    isVerified = isVerified,
    isPremium = isPremium,
)

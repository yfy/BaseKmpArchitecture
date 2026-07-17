package com.yfy.kmp.feature.auth.domain

import kotlinx.coroutines.flow.Flow

public interface AuthRepository {
    public fun login(email: String, password: String): Flow<LoginOutcome>
    public fun socialLogin(provider: SocialProvider, token: String): Flow<LoginOutcome>
    public fun signup(params: SignupParams): Flow<SignupOutcome>
    public fun forgotPassword(email: String): Flow<ForgotPasswordOutcome>
    public fun verifyEmail(params: EmailVerifyParams): Flow<EmailVerifyOutcome>
    public fun changePassword(currentPassword: String, newPassword: String): Flow<ChangePasswordOutcome>
    public fun resetPassword(token: String, newPassword: String): Flow<ResetPasswordOutcome>
    public fun twoFactorEnable(): Flow<TwoFactorOutcome>
    public fun twoFactorVerify(code: String): Flow<TwoFactorOutcome>
    public fun twoFactorDisable(): Flow<TwoFactorOutcome>
}

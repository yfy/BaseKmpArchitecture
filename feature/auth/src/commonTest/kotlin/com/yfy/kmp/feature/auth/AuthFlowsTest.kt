package com.yfy.kmp.feature.auth

import com.yfy.kmp.core.database.UserCache
import com.yfy.kmp.core.model.AuthUser
import com.yfy.kmp.feature.auth.data.AuthApi
import com.yfy.kmp.feature.auth.data.AuthRepositoryImpl
import com.yfy.kmp.feature.auth.domain.EmailVerifyOutcome
import com.yfy.kmp.feature.auth.domain.EmailVerifyParams
import com.yfy.kmp.feature.auth.domain.EmailVerifyUseCase
import com.yfy.kmp.feature.auth.domain.ForgotPasswordOutcome
import com.yfy.kmp.feature.auth.domain.ForgotPasswordUseCase
import com.yfy.kmp.feature.auth.domain.InputError
import com.yfy.kmp.feature.auth.domain.SignupOutcome
import com.yfy.kmp.feature.auth.domain.SignupParams
import com.yfy.kmp.feature.auth.domain.SignupUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthFlowsTest {

    private class NoopUserCache : UserCache {
        override fun observeAll() = flowOf(emptyList<AuthUser>())
        override suspend fun get(id: String): AuthUser? = null
        override suspend fun upsert(user: AuthUser) {}
        override suspend fun delete(id: String) {}
    }

    private fun repo() = AuthRepositoryImpl(AuthApi(mockAuthClient(), "https://mock.local"), userCache = NoopUserCache(), tokenStore = InMemoryTokenStore())

    @Test
    fun signup_success_returns_user() = runTest {
        val outcome = SignupUseCase(repo())(
            SignupParams("Ada", "Lovelace", "ada@yfy.dev", "1234", "1234"),
        ).first()
        assertTrue(outcome is SignupOutcome.Success)
    }

    @Test
    fun signup_blank_name_is_invalid_input() = runTest {
        val outcome = SignupUseCase(repo())(
            SignupParams("", "Lovelace", "ada@yfy.dev", "1234", "1234"),
        ).first()
        assertEquals(SignupOutcome.InvalidInput(InputError.NAME_REQUIRED), outcome)
    }

    @Test
    fun signup_password_mismatch_is_invalid_input() = runTest {
        val outcome = SignupUseCase(repo())(
            SignupParams("Ada", "Lovelace", "ada@yfy.dev", "1234", "9999"),
        ).first()
        assertEquals(SignupOutcome.InvalidInput(InputError.PASSWORD_MISMATCH), outcome)
    }

    @Test
    fun forgot_password_success() = runTest {
        val outcome = ForgotPasswordUseCase(repo())("ada@yfy.dev").first()
        assertEquals(ForgotPasswordOutcome.Success, outcome)
    }

    @Test
    fun forgot_password_invalid_email() = runTest {
        val outcome = ForgotPasswordUseCase(repo())("bademail").first()
        assertEquals(ForgotPasswordOutcome.InvalidInput(InputError.EMAIL_FORMAT), outcome)
    }

    @Test
    fun email_verify_success_with_six_digit_code() = runTest {
        val outcome = EmailVerifyUseCase(repo())(EmailVerifyParams("ada@yfy.dev", "123456")).first()
        assertEquals(EmailVerifyOutcome.Success, outcome)
    }

    @Test
    fun email_verify_bad_code_format_is_invalid_input() = runTest {
        val outcome = EmailVerifyUseCase(repo())(EmailVerifyParams("ada@yfy.dev", "12")).first()
        assertEquals(EmailVerifyOutcome.InvalidInput(InputError.CODE_FORMAT), outcome)
    }
}

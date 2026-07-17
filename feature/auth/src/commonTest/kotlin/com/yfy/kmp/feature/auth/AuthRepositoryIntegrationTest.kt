package com.yfy.kmp.feature.auth

import com.yfy.kmp.core.database.UserCache
import com.yfy.kmp.core.model.AuthUser
import com.yfy.kmp.feature.auth.data.AuthApi
import com.yfy.kmp.feature.auth.data.AuthRepositoryImpl
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthRepositoryIntegrationTest {

    private class FakeUserCache : UserCache {
        val upserted = mutableListOf<AuthUser>()
        override fun observeAll() = kotlinx.coroutines.flow.flowOf(upserted.toList())
        override suspend fun get(id: String): AuthUser? = upserted.lastOrNull { it.id == id }
        override suspend fun upsert(user: AuthUser) { upserted += user }
        override suspend fun delete(id: String) { upserted.removeAll { it.id == id } }
    }

    private fun repo(cache: FakeUserCache = FakeUserCache()) =
        AuthRepositoryImpl(AuthApi(mockAuthClient(), "https://mock.local"), userCache = cache, tokenStore = InMemoryTokenStore())

    @Test
    fun signup_success_maps_dto_and_caches_user() = runTest {
        val cache = FakeUserCache()
        val outcome = repo(cache).signup(
            SignupParams("YFY", "Demo", "demo@yfy.dev", "secret12", "secret12"),
        ).first()
        assertTrue(outcome is SignupOutcome.Success)
        assertEquals("u_1001", outcome.user.id)
        assertEquals("demo@yfy.dev", outcome.user.email)
        assertEquals(listOf("u_1001"), cache.upserted.map { it.id })
    }

    @Test
    fun social_login_success_caches_user() = runTest {
        val cache = FakeUserCache()
        val outcome = repo(cache).socialLogin(SocialProvider.GOOGLE, "dummy-token").first()
        assertTrue(outcome is LoginOutcome.Success)
        assertEquals("u_1001", outcome.user.id)
        assertEquals(1, cache.upserted.size)
    }

    @Test
    fun forgot_password_success() = runTest {
        assertEquals(ForgotPasswordOutcome.Success, repo().forgotPassword("demo@yfy.dev").first())
    }

    @Test
    fun verify_email_success() = runTest {
        val outcome = repo().verifyEmail(EmailVerifyParams("demo@yfy.dev", "123456")).first()
        assertEquals(EmailVerifyOutcome.Success, outcome)
    }

    @Test
    fun change_password_success() = runTest {
        assertEquals(ChangePasswordOutcome.Success, repo().changePassword("old12345", "new12345").first())
    }

    @Test
    fun reset_password_success() = runTest {
        assertEquals(ResetPasswordOutcome.Success, repo().resetPassword("tok_9", "new12345").first())
    }

    @Test
    fun two_factor_enable_returns_secret_then_verify_enables() = runTest {
        val setup = repo().twoFactorEnable().first()
        assertTrue(setup is TwoFactorOutcome.SetupReady)
        assertEquals("YFY-DUMMY-2FA-SECRET-ABCD", setup.secret)

        assertEquals(TwoFactorOutcome.Enabled, repo().twoFactorVerify("123456").first())
        assertEquals(TwoFactorOutcome.Disabled, repo().twoFactorDisable().first())
    }
}

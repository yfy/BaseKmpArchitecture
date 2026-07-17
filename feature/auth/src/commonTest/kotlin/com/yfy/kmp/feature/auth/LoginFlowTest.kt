package com.yfy.kmp.feature.auth

import com.yfy.kmp.core.common.result.AppError
import com.yfy.kmp.core.common.result.NetworkException
import com.yfy.kmp.core.common.result.Phase
import com.yfy.kmp.core.common.result.asPhaseFlow
import com.yfy.kmp.core.common.result.toAppError
import com.yfy.kmp.core.database.UserCache
import com.yfy.kmp.core.model.AuthUser
import com.yfy.kmp.feature.auth.data.AuthApi
import com.yfy.kmp.feature.auth.data.AuthRepositoryImpl
import com.yfy.kmp.feature.auth.domain.InputError
import com.yfy.kmp.feature.auth.domain.LoginOutcome
import com.yfy.kmp.feature.auth.domain.LoginParams
import com.yfy.kmp.feature.auth.domain.LoginUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class LoginFlowTest {

    private class FakeUserCache : UserCache {
        val upserted = mutableListOf<AuthUser>()
        override fun observeAll() = kotlinx.coroutines.flow.flowOf(upserted.toList())
        override suspend fun get(id: String): AuthUser? = upserted.lastOrNull { it.id == id }
        override suspend fun upsert(user: AuthUser) { upserted += user }
        override suspend fun delete(id: String) { upserted.removeAll { it.id == id } }
    }

    private fun useCase(cache: FakeUserCache = FakeUserCache()): LoginUseCase {
        val repo = AuthRepositoryImpl(AuthApi(mockAuthClient(), "https://mock.local"), userCache = cache, tokenStore = InMemoryTokenStore())
        return LoginUseCase(repo)
    }

    @Test
    fun login_success_emits_success_outcome() = runTest {
        val outcome = useCase()(LoginParams("demo@yfy.dev", "1234")).first()
        assertTrue(outcome is LoginOutcome.Success)
        assertEquals("u_1001", outcome.user.id)
        assertEquals("demo@yfy.dev", outcome.user.email)
    }

    @Test
    fun successful_login_caches_user() = runTest {
        val cache = FakeUserCache()
        useCase(cache)(LoginParams("demo@yfy.dev", "1234")).first()
        assertEquals(1, cache.upserted.size)
        assertEquals("u_1001", cache.upserted.single().id)
    }

    @Test
    fun invalid_email_is_expected_outcome_not_exception() = runTest {
        val outcome = useCase()(LoginParams("bademail", "1234")).first()
        assertTrue(outcome is LoginOutcome.InvalidInput)
        assertEquals(InputError.EMAIL_FORMAT, outcome.reason)
    }

    @Test
    fun short_password_is_expected_outcome() = runTest {
        val outcome = useCase()(LoginParams("a@b.c", "12")).first()
        assertTrue(outcome is LoginOutcome.InvalidInput)
        assertEquals(InputError.PASSWORD_TOO_SHORT, outcome.reason)
    }

    @Test
    fun asPhaseFlow_maps_infra_exception_to_failed_value() = runTest {
        val phases = flow<Int> { throw NetworkException() }
            .asPhaseFlow()
            .toList()
        assertEquals(2, phases.size)
        assertTrue(phases[0] is Phase.Loading)
        val failed = phases[1]
        assertTrue(failed is Phase.Failed)
        assertTrue(failed.error is AppError.Network)
    }

    @Test
    fun toAppError_rethrows_cancellation() {
        assertFailsWith<CancellationException> {
            CancellationException("cancelled").toAppError()
        }
    }
}

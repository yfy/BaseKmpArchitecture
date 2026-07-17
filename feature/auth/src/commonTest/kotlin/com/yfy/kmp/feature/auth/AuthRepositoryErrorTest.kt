package com.yfy.kmp.feature.auth

import com.yfy.kmp.core.common.result.ServerException
import com.yfy.kmp.core.database.UserCache
import com.yfy.kmp.core.model.AuthUser
import com.yfy.kmp.core.network.mock.MockRoute
import com.yfy.kmp.feature.auth.data.AuthApi
import com.yfy.kmp.feature.auth.data.AuthRepositoryImpl
import com.yfy.kmp.feature.auth.domain.LoginOutcome
import com.yfy.kmp.feature.auth.domain.SignupOutcome
import com.yfy.kmp.feature.auth.domain.SignupParams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AuthRepositoryErrorTest {

    private class NoopUserCache : UserCache {
        override fun observeAll(): Flow<List<AuthUser>> = flowOf(emptyList())
        override suspend fun get(id: String): AuthUser? = null
        override suspend fun upsert(user: AuthUser) {}
        override suspend fun delete(id: String) {}
    }

    private fun repo(routes: List<MockRoute>) = AuthRepositoryImpl(
        api = AuthApi(mockAuthClient(routes), "https://mock.local"),
        userCache = NoopUserCache(),
        tokenStore = InMemoryTokenStore(),
    )

    @Test
    fun login_with_401_recovers_to_invalid_credentials() = runTest {
        val repo = repo(listOf(MockRoute("/auth/login", "error_response", status = 401)))
        val outcome = repo.login("demo@yfy.dev", "wrong").first()
        assertEquals(LoginOutcome.InvalidCredentials, outcome)
    }

    @Test
    fun signup_with_409_recovers_to_email_taken() = runTest {
        val repo = repo(listOf(MockRoute("/auth/signup", "error_response", status = 409)))
        val outcome = repo.signup(SignupParams("Demo", "User", "demo@yfy.dev", "1234", "1234")).first()
        assertEquals(SignupOutcome.EmailTaken, outcome)
    }

    @Test
    fun login_with_500_throws_server_exception() = runTest {
        val repo = repo(listOf(MockRoute("/auth/login", "error_response", status = 500)))
        val e = assertFailsWith<ServerException> {
            repo.login("demo@yfy.dev", "1234").first()
        }
        assertEquals(500, e.code)
    }
}

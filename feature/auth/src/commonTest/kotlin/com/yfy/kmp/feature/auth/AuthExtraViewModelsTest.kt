package com.yfy.kmp.feature.auth

import com.yfy.kmp.core.model.AuthUser
import com.yfy.kmp.core.database.UserCache
import com.yfy.kmp.feature.auth.data.AuthApi
import com.yfy.kmp.feature.auth.data.AuthRepositoryImpl
import com.yfy.kmp.feature.auth.domain.ChangePasswordUseCase
import com.yfy.kmp.feature.auth.domain.ResetPasswordUseCase
import com.yfy.kmp.feature.auth.domain.TwoFactorDisableUseCase
import com.yfy.kmp.feature.auth.domain.TwoFactorEnableUseCase
import com.yfy.kmp.feature.auth.domain.TwoFactorVerifyUseCase
import com.yfy.kmp.feature.auth.presentation.ChangePasswordUiError
import com.yfy.kmp.feature.auth.presentation.ChangePasswordViewModel
import com.yfy.kmp.feature.auth.presentation.ResetPasswordViewModel
import com.yfy.kmp.feature.auth.presentation.TwoFactorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthExtraViewModelsTest {

    private class NoopUserCache : UserCache {
        override fun observeAll(): Flow<List<AuthUser>> = flowOf(emptyList())
        override suspend fun get(id: String): AuthUser? = null
        override suspend fun upsert(user: AuthUser) {}
        override suspend fun delete(id: String) {}
    }

    private fun repo() = AuthRepositoryImpl(AuthApi(mockAuthClient(), "https://mock.local"), userCache = NoopUserCache(), tokenStore = InMemoryTokenStore())

    @BeforeTest fun setUp() = Dispatchers.setMain(StandardTestDispatcher())
    @AfterTest fun tearDown() = Dispatchers.resetMain()

    @Test
    fun change_password_mismatch_is_validation_error() = runTest {
        val vm = ChangePasswordViewModel(ChangePasswordUseCase(repo()))
        vm.onCurrentPasswordChange("oldpass")
        vm.onNewPasswordChange("newpass")
        vm.onConfirmPasswordChange("different")
        vm.submit()
        advanceUntilIdle()
        assertEquals(ChangePasswordUiError.PASSWORD_MISMATCH, vm.state.value.error)
    }

    @Test
    fun change_password_success_marks_done() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val vm = ChangePasswordViewModel(ChangePasswordUseCase(repo()))
            vm.onCurrentPasswordChange("oldpass")
            vm.onNewPasswordChange("newpass")
            vm.onConfirmPasswordChange("newpass")
            vm.submit()
            advanceUntilIdle()
            withContext(Dispatchers.Default) {
                withTimeout(5_000) { vm.state.first { it.done || it.error != null } }
            }
            assertTrue(vm.state.value.done)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun reset_password_success_marks_done() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val vm = ResetPasswordViewModel(ResetPasswordUseCase(repo()))
            vm.setToken("tok_1")
            vm.onNewPasswordChange("newpass")
            vm.onConfirmPasswordChange("newpass")
            vm.submit()
            advanceUntilIdle()
            withContext(Dispatchers.Default) {
                withTimeout(5_000) { vm.state.first { it.done || it.error != null } }
            }
            assertTrue(vm.state.value.done)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun two_factor_enable_then_verify_enables() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val r = repo()
            val vm = TwoFactorViewModel(
                TwoFactorEnableUseCase(r),
                TwoFactorVerifyUseCase(r),
                TwoFactorDisableUseCase(r),
            )
            vm.startEnable()
            advanceUntilIdle()
            withContext(Dispatchers.Default) {
                withTimeout(5_000) { vm.state.first { it.secret != null || it.error != null } }
            }
            assertNotNull(vm.state.value.secret)

            vm.onCodeChange("123456")
            vm.verify()
            advanceUntilIdle()
            withContext(Dispatchers.Default) {
                withTimeout(5_000) { vm.state.first { it.enabled || it.error != null } }
            }
            assertTrue(vm.state.value.enabled)
        } finally {
            Dispatchers.resetMain()
        }
    }
}

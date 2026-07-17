package com.yfy.kmp.feature.auth

import com.yfy.kmp.core.analytics.AnalyticsTracker
import com.yfy.kmp.core.database.UserCache
import com.yfy.kmp.core.datastore.PreferencesStore
import com.yfy.kmp.core.model.AuthUser
import com.yfy.kmp.feature.auth.data.AuthApi
import com.yfy.kmp.feature.auth.data.AuthRepositoryImpl
import com.yfy.kmp.feature.auth.data.MockSocialAuthProvider
import com.yfy.kmp.feature.auth.domain.ExchangeSocialTokenUseCase
import com.yfy.kmp.feature.auth.domain.LoginUseCase
import com.yfy.kmp.feature.auth.domain.SocialLoginUseCase
import com.yfy.kmp.feature.auth.domain.SocialProvider
import com.yfy.kmp.feature.auth.presentation.LoginUiError
import com.yfy.kmp.feature.auth.presentation.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.flow.first
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class LoginViewModelTest {

    private class FakePreferences : PreferencesStore {
        val values = MutableStateFlow<Map<String, String>>(emptyMap())
        override fun stringFlow(key: String): Flow<String?> = values.map { it[key] }
        override suspend fun getString(key: String): String? = values.value[key]
        override suspend fun putString(key: String, value: String) {
            values.value += (key to value)
        }
        override suspend fun remove(key: String) {
            values.value -= key
        }
    }

    private class FakeTracker : AnalyticsTracker {
        val events = mutableListOf<Pair<String, Map<String, String>>>()
        val screens = mutableListOf<String>()
        var recordedUserId: String? = null
        override fun logEvent(name: String, params: Map<String, String>) {
            events += name to params
        }
        override fun logScreen(name: String) {
            screens += name
        }
        override fun setUserId(id: String?) {
            recordedUserId = id
        }
        override fun setUserProperty(name: String, value: String) = Unit
        override fun reset() {
            recordedUserId = null
        }
    }

    private class NoopUserCache : UserCache {
        override fun observeAll(): Flow<List<AuthUser>> = flowOf(emptyList())
        override suspend fun get(id: String): AuthUser? = null
        override suspend fun upsert(user: AuthUser) {}
        override suspend fun delete(id: String) {}
    }

    private fun newViewModel(
        preferences: FakePreferences = FakePreferences(),
        tracker: FakeTracker = FakeTracker(),
    ): LoginViewModel {
        val repo = AuthRepositoryImpl(AuthApi(mockAuthClient(), "https://mock.local"), userCache = NoopUserCache(), tokenStore = InMemoryTokenStore())
        return LoginViewModel(
            LoginUseCase(repo),
            SocialLoginUseCase(repo, MockSocialAuthProvider()),
            ExchangeSocialTokenUseCase(repo),
            preferences,
            tracker,
        )
    }

    @Test
    fun successful_login_logs_event_and_persists_email() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val preferences = FakePreferences()
            val tracker = FakeTracker()
            val vm = newViewModel(preferences, tracker)

            vm.onEmailChange("demo@yfy.dev")
            vm.onPasswordChange("1234")
            vm.onRememberMeChange(true)
            vm.login()
            advanceUntilIdle()
            withContext(Dispatchers.Default) {
                withTimeout(5_000) { vm.state.first { it.user != null || it.error != null } }
            }

            assertNotNull(vm.state.value.user)
            assertEquals(listOf("login_succeeded"), tracker.events.map { it.first })
            assertEquals("u_1001", tracker.recordedUserId)
            assertEquals("demo@yfy.dev", preferences.values.value["last_login_email"])
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun social_login_sets_user_and_logs_event() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val tracker = FakeTracker()
            val vm = newViewModel(tracker = tracker)

            vm.socialLogin(SocialProvider.GOOGLE)
            advanceUntilIdle()
            withContext(Dispatchers.Default) {
                withTimeout(5_000) { vm.state.first { it.user != null || it.error != null } }
            }

            assertNotNull(vm.state.value.user)
            assertEquals(listOf("login_succeeded"), tracker.events.map { it.first })
            assertEquals("u_1001", tracker.recordedUserId)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun invalid_input_logs_failure_event_with_reason() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val tracker = FakeTracker()
            val vm = newViewModel(tracker = tracker)

            vm.onEmailChange("bademail")
            vm.onPasswordChange("1234")
            vm.login()
            advanceUntilIdle()

            assertEquals(LoginUiError.EMAIL_FORMAT, vm.state.value.error)
            assertEquals("login_failed" to mapOf("reason" to "email_format"), tracker.events.single())
            assertNull(tracker.recordedUserId)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun last_email_prefills_initial_state() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val preferences = FakePreferences()
            preferences.putString("last_login_email", "saved@yfy.dev")
            val vm = newViewModel(preferences)
            advanceUntilIdle()

            assertEquals("saved@yfy.dev", vm.state.value.email)
        } finally {
            Dispatchers.resetMain()
        }
    }
}

package com.yfy.kmp.feature.profile

import com.yfy.kmp.core.database.UserCache
import com.yfy.kmp.core.datastore.PreferenceKeys
import com.yfy.kmp.core.datastore.PreferencesStore
import com.yfy.kmp.core.model.AuthUser
import com.yfy.kmp.feature.profile.presentation.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProfileViewModelTest {

    private class FakePreferences(initial: Map<String, String> = emptyMap()) : PreferencesStore {
        private val map = initial.toMutableMap()
        override fun stringFlow(key: String): Flow<String?> = flowOf(map[key])
        override suspend fun getString(key: String): String? = map[key]
        override suspend fun putString(key: String, value: String) { map[key] = value }
        override suspend fun remove(key: String) { map.remove(key) }
    }

    private class FakeUserCache(private val users: Map<String, AuthUser>) : UserCache {
        override fun observeAll(): Flow<List<AuthUser>> = flowOf(users.values.toList())
        override suspend fun get(id: String): AuthUser? = users[id]
        override suspend fun upsert(user: AuthUser) {}
        override suspend fun delete(id: String) {}
    }

    @BeforeTest fun setUp() = Dispatchers.setMain(StandardTestDispatcher())
    @AfterTest fun tearDown() = Dispatchers.resetMain()

    @Test
    fun loads_current_user_from_cache() = runTest {
        val user = AuthUser(id = "u_1", username = "yfy", email = "demo@yfy.dev", displayName = "YFY")
        val vm = ProfileViewModel(
            preferences = FakePreferences(mapOf(PreferenceKeys.CURRENT_USER_ID to "u_1")),
            userCache = FakeUserCache(mapOf("u_1" to user)),
        )
        advanceUntilIdle()
        assertEquals(user, vm.state.value.user)
        assertEquals(false, vm.state.value.isLoading)
    }

    @Test
    fun no_current_user_yields_null() = runTest {
        val vm = ProfileViewModel(FakePreferences(), FakeUserCache(emptyMap()))
        advanceUntilIdle()
        assertNull(vm.state.value.user)
        assertEquals(false, vm.state.value.isLoading)
    }
}

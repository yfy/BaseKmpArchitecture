package com.yfy.kmp.feature.settings

import com.yfy.kmp.core.datastore.AppThemeMode
import com.yfy.kmp.core.datastore.PreferencesStore
import com.yfy.kmp.feature.settings.presentation.SettingsViewModel
import com.yfy.kmp.feature.settings.presentation.ThemeViewModel
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
import kotlin.test.assertTrue

class SettingsViewModelTest {

    private class FakePreferences : PreferencesStore {
        val map = mutableMapOf<String, String>()
        override fun stringFlow(key: String): Flow<String?> = flowOf(map[key])
        override suspend fun getString(key: String): String? = map[key]
        override suspend fun putString(key: String, value: String) { map[key] = value }
        override suspend fun remove(key: String) { map.remove(key) }
    }

    @BeforeTest fun setUp() = Dispatchers.setMain(StandardTestDispatcher())
    @AfterTest fun tearDown() = Dispatchers.resetMain()

    @Test
    fun defaults_notifications_on() = runTest {
        val vm = SettingsViewModel(FakePreferences())
        advanceUntilIdle()
        assertEquals(true, vm.state.value.notificationsEnabled)
    }

    @Test
    fun notifications_toggle_persists() = runTest {
        val prefs = FakePreferences()
        val vm = SettingsViewModel(prefs)
        advanceUntilIdle()

        vm.setNotificationsEnabled(false)
        advanceUntilIdle()

        assertEquals(false, vm.state.value.notificationsEnabled)
        assertEquals("false", prefs.map["settings_notifications"])
    }

    @Test
    fun theme_defaults_system_and_set_persists() = runTest {
        val prefs = FakePreferences()
        val vm = ThemeViewModel(prefs)
        advanceUntilIdle()
        assertEquals(AppThemeMode.SYSTEM, vm.state.value)

        vm.setMode(AppThemeMode.DARK)
        advanceUntilIdle()
        assertTrue(vm.state.value == AppThemeMode.DARK)
        assertEquals("DARK", prefs.map["settings_theme_mode"])
    }
}

package com.yfy.kmp.core.datastore

import com.yfy.kmp.core.model.UserSession
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okio.FileSystem
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PreferencesStoreTest {

    private fun newPreferencesStore(): PreferencesStore =
        DataStorePreferencesStore(
            createPreferencesDataStore {
                (FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "test-${Random.nextLong()}.preferences_pb").toString()
            },
        )

    @Test
    fun put_and_get_string_round_trips() = runTest {
        val store = newPreferencesStore()
        assertNull(store.getString("greeting"))
        store.putString("greeting", "merhaba")
        assertEquals("merhaba", store.getString("greeting"))
    }

    @Test
    fun remove_clears_value() = runTest {
        val store = newPreferencesStore()
        store.putString("key", "value")
        store.remove("key")
        assertNull(store.getString("key"))
    }

    @Test
    fun string_flow_emits_latest_value() = runTest {
        val store = newPreferencesStore()
        store.putString("k", "v1")
        store.putString("k", "v2")
        assertEquals("v2", store.getString("k"))
    }

    @Test
    fun user_session_round_trips_and_clears() = runTest {
        val sessions = JsonUserSessionStore(newPreferencesStore(), Json { ignoreUnknownKeys = true })
        assertNull(sessions.current())

        val session = UserSession(
            accessToken = "token-123",
            refreshToken = "refresh-456",
            userId = "u_1001",
            expiresAt = 4_102_444_800_000,
        )
        sessions.save(session)
        assertEquals(session, sessions.current())

        sessions.clear()
        assertNull(sessions.current())
    }
}

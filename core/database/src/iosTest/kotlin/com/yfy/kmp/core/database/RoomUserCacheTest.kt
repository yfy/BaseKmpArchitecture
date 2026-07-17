package com.yfy.kmp.core.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.yfy.kmp.core.model.AuthUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import platform.Foundation.NSTemporaryDirectory
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RoomUserCacheTest {

    private fun newCache(): UserCache {
        val db = Room.databaseBuilder<AppDatabase>(
            name = NSTemporaryDirectory() + "test-${Random.nextLong()}.db",
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        return RoomUserCache(db.userDao())
    }

    private val user = AuthUser(
        id = "u_1001",
        username = "yfy",
        email = "demo@yfy.dev",
        displayName = "YFY Demo",
        isVerified = true,
    )

    @Test
    fun upsert_and_get_round_trips() = runTest {
        val cache = newCache()
        assertNull(cache.get(user.id))
        cache.upsert(user)
        assertEquals(user, cache.get(user.id))
    }

    @Test
    fun upsert_updates_existing_row() = runTest {
        val cache = newCache()
        cache.upsert(user)
        cache.upsert(user.copy(displayName = "Yeni Ad"))
        assertEquals("Yeni Ad", cache.get(user.id)?.displayName)
    }

    @Test
    fun delete_removes_row() = runTest {
        val cache = newCache()
        cache.upsert(user)
        cache.delete(user.id)
        assertNull(cache.get(user.id))
    }

    @Test
    fun observeAll_emits_current_rows() = runTest {
        val cache = newCache()
        cache.upsert(user)
        cache.upsert(user.copy(id = "u_1002", username = "abc"))
        assertEquals(2, cache.observeAll().first().size)
    }
}

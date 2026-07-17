package com.yfy.kmp.core.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.test.platform.app.InstrumentationRegistry
import com.yfy.kmp.core.model.AuthUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RoomUserCacheAndroidTest {

    private fun newCache(): UserCache {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.databaseBuilder<AppDatabase>(
            context = context,
            name = context.cacheDir.resolve("test-${Random.nextLong()}.db").absolutePath,
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

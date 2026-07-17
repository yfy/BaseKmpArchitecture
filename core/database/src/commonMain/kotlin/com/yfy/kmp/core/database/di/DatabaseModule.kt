package com.yfy.kmp.core.database.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.yfy.kmp.core.database.AppDatabase
import com.yfy.kmp.core.database.RoomUserCache
import com.yfy.kmp.core.database.UserCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.dsl.module

// TODO(template): replace before release — Room database file name.
internal const val DATABASE_FILE_NAME: String = "yfy.db"

internal expect val platformDatabaseModule: Module

public val databaseModule: Module = module {
    includes(platformDatabaseModule)
    single<AppDatabase> {
        get<RoomDatabase.Builder<AppDatabase>>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
    single<UserCache> { RoomUserCache(get<AppDatabase>().userDao()) }
}

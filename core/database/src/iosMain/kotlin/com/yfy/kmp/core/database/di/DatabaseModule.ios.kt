package com.yfy.kmp.core.database.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.yfy.kmp.core.database.AppDatabase
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

internal actual val platformDatabaseModule: Module = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        val documents = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true,
        ).first() as String
        Room.databaseBuilder<AppDatabase>(name = "$documents/$DATABASE_FILE_NAME")
    }
}

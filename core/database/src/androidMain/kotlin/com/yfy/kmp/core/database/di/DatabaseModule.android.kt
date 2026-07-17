package com.yfy.kmp.core.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yfy.kmp.core.database.AppDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformDatabaseModule: Module = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        val context = get<Context>()
        Room.databaseBuilder<AppDatabase>(
            context = context,
            name = context.getDatabasePath(DATABASE_FILE_NAME).absolutePath,
        )
    }
}

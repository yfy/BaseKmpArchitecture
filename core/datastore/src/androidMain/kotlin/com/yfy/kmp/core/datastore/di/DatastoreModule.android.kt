package com.yfy.kmp.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.yfy.kmp.core.datastore.DATASTORE_FILE_NAME
import com.yfy.kmp.core.datastore.createPreferencesDataStore
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformDatastoreModule: Module = module {
    single<DataStore<Preferences>> {
        createPreferencesDataStore {
            get<Context>().filesDir.resolve(DATASTORE_FILE_NAME).absolutePath
        }
    }
}

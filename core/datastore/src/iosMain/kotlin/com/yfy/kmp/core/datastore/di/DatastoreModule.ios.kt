package com.yfy.kmp.core.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.yfy.kmp.core.datastore.DATASTORE_FILE_NAME
import com.yfy.kmp.core.datastore.createPreferencesDataStore
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

internal actual val platformDatastoreModule: Module = module {
    single<DataStore<Preferences>> {
        createPreferencesDataStore {
            val documents = NSSearchPathForDirectoriesInDomains(
                NSDocumentDirectory,
                NSUserDomainMask,
                true,
            ).first() as String
            "$documents/$DATASTORE_FILE_NAME"
        }
    }
}

package com.yfy.kmp.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath

// TODO(template): replace before release — DataStore file name.
internal const val DATASTORE_FILE_NAME: String = "yfy.preferences_pb"

internal fun createPreferencesDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })

internal class DataStorePreferencesStore(
    private val dataStore: DataStore<Preferences>,
) : PreferencesStore {

    override fun stringFlow(key: String): Flow<String?> =
        dataStore.data.map { it[stringPreferencesKey(key)] }

    override suspend fun getString(key: String): String? = stringFlow(key).first()

    override suspend fun putString(key: String, value: String) {
        dataStore.edit { it[stringPreferencesKey(key)] = value }
    }

    override suspend fun remove(key: String) {
        dataStore.edit { it.remove(stringPreferencesKey(key)) }
    }
}

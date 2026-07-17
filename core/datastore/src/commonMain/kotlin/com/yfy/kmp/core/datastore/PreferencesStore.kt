package com.yfy.kmp.core.datastore

import kotlinx.coroutines.flow.Flow

public interface PreferencesStore {
    public fun stringFlow(key: String): Flow<String?>
    public suspend fun getString(key: String): String?
    public suspend fun putString(key: String, value: String)
    public suspend fun remove(key: String)
}

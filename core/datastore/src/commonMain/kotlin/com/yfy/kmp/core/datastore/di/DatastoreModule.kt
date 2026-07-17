package com.yfy.kmp.core.datastore.di

import com.yfy.kmp.core.datastore.DataStorePreferencesStore
import com.yfy.kmp.core.datastore.JsonUserSessionStore
import com.yfy.kmp.core.datastore.PreferencesStore
import com.yfy.kmp.core.datastore.UserSessionStore
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val platformDatastoreModule: Module

public val datastoreModule: Module = module {
    includes(platformDatastoreModule)
    single<PreferencesStore> { DataStorePreferencesStore(get()) }
    single<UserSessionStore> { JsonUserSessionStore(get(), Json { ignoreUnknownKeys = true }) }
}

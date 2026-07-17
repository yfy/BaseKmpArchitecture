package com.yfy.kmp.core.datastore

import com.yfy.kmp.core.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

internal class JsonUserSessionStore(
    private val preferences: PreferencesStore,
    private val json: Json,
) : UserSessionStore {

    override val session: Flow<UserSession?> =
        preferences.stringFlow(KEY_USER_SESSION).map { raw ->
            raw?.let { json.decodeFromString<UserSession>(it) }
        }

    override suspend fun current(): UserSession? = session.first()

    override suspend fun save(session: UserSession) {
        preferences.putString(KEY_USER_SESSION, json.encodeToString(UserSession.serializer(), session))
    }

    override suspend fun clear() {
        preferences.remove(KEY_USER_SESSION)
    }

    private companion object {
        const val KEY_USER_SESSION = "user_session"
    }
}

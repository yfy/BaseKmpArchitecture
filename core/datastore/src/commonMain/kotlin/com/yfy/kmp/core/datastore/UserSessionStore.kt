package com.yfy.kmp.core.datastore

import com.yfy.kmp.core.model.UserSession
import kotlinx.coroutines.flow.Flow

public interface UserSessionStore {
    public val session: Flow<UserSession?>
    public suspend fun current(): UserSession?
    public suspend fun save(session: UserSession)
    public suspend fun clear()
}

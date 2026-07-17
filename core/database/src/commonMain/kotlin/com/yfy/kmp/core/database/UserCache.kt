package com.yfy.kmp.core.database

import com.yfy.kmp.core.model.AuthUser
import kotlinx.coroutines.flow.Flow

public interface UserCache {
    public fun observeAll(): Flow<List<AuthUser>>
    public suspend fun get(id: String): AuthUser?
    public suspend fun upsert(user: AuthUser)
    public suspend fun delete(id: String)
}

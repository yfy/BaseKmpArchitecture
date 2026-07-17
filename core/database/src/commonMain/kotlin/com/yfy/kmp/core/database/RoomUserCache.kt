package com.yfy.kmp.core.database

import com.yfy.kmp.core.model.AuthUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RoomUserCache(
    private val dao: UserDao,
) : UserCache {

    override fun observeAll(): Flow<List<AuthUser>> =
        dao.observeAll().map { entities -> entities.map { it.toModel() } }

    override suspend fun get(id: String): AuthUser? = dao.getById(id)?.toModel()

    override suspend fun upsert(user: AuthUser) {
        dao.upsert(user.toEntity())
    }

    override suspend fun delete(id: String) {
        dao.deleteById(id)
    }
}

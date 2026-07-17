package com.yfy.kmp.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yfy.kmp.core.model.AuthUser

@Entity(tableName = "users")
internal data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val email: String,
    val displayName: String?,
    val avatarUrl: String?,
    val isVerified: Boolean,
    val isPremium: Boolean,
)

internal fun UserEntity.toModel(): AuthUser = AuthUser(
    id = id,
    username = username,
    email = email,
    displayName = displayName,
    avatarUrl = avatarUrl,
    isVerified = isVerified,
    isPremium = isPremium,
)

internal fun AuthUser.toEntity(): UserEntity = UserEntity(
    id = id,
    username = username,
    email = email,
    displayName = displayName,
    avatarUrl = avatarUrl,
    isVerified = isVerified,
    isPremium = isPremium,
)

package com.yfy.kmp.core.database

import com.yfy.kmp.core.model.AuthUser
import kotlin.test.Test
import kotlin.test.assertEquals

class UserEntityMappingTest {

    @Test
    fun model_entity_round_trip_is_lossless() {
        val user = AuthUser(
            id = "u_42",
            username = "yfy",
            email = "demo@yfy.dev",
            displayName = "YFY Demo",
            avatarUrl = "https://example.com/a.png",
            isVerified = true,
            isPremium = false,
        )
        assertEquals(user, user.toEntity().toModel())
    }

    @Test
    fun nullable_fields_survive_round_trip() {
        val user = AuthUser(id = "u_1", username = "u", email = "u@x.y")
        assertEquals(user, user.toEntity().toModel())
    }
}

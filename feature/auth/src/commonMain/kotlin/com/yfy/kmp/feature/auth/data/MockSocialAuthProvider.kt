package com.yfy.kmp.feature.auth.data

import com.yfy.kmp.feature.auth.domain.SocialAuthProvider
import com.yfy.kmp.feature.auth.domain.SocialProvider

internal class MockSocialAuthProvider : SocialAuthProvider {
    override suspend fun authenticate(provider: SocialProvider): String =
        "dummy-${provider.name.lowercase()}-token"
}

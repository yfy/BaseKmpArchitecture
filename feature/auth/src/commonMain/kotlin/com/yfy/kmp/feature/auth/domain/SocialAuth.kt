package com.yfy.kmp.feature.auth.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

public enum class SocialProvider {
    GOOGLE, APPLE, INSTAGRAM, GOOGLE_PLAY
}

public interface SocialAuthProvider {
    public suspend fun authenticate(provider: SocialProvider): String
}

public class SocialLoginUseCase(
    private val repository: AuthRepository,
    private val socialAuthProvider: SocialAuthProvider,
) {
    public operator fun invoke(provider: SocialProvider): Flow<LoginOutcome> = flow {
        val token = socialAuthProvider.authenticate(provider)
        emitAll(repository.socialLogin(provider, token))
    }
}

public class ExchangeSocialTokenUseCase(
    private val repository: AuthRepository,
) {
    public operator fun invoke(provider: SocialProvider, token: String): Flow<LoginOutcome> =
        repository.socialLogin(provider, token)
}

package com.yfy.kmp.feature.auth.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

public sealed interface TwoFactorOutcome {
    public data class SetupReady(val secret: String) : TwoFactorOutcome
    public data object Enabled : TwoFactorOutcome
    public data object Disabled : TwoFactorOutcome
    public data object InvalidCode : TwoFactorOutcome
    public data class InvalidInput(val reason: InputError) : TwoFactorOutcome
}

public class TwoFactorEnableUseCase(
    private val repository: AuthRepository,
) {
    public operator fun invoke(): Flow<TwoFactorOutcome> = repository.twoFactorEnable()
}

public class TwoFactorVerifyUseCase(
    private val repository: AuthRepository,
) {
    public operator fun invoke(code: String): Flow<TwoFactorOutcome> =
        if (code.length != 6 || code.any { !it.isDigit() }) {
            flowOf(TwoFactorOutcome.InvalidInput(InputError.CODE_FORMAT))
        } else {
            repository.twoFactorVerify(code)
        }
}

public class TwoFactorDisableUseCase(
    private val repository: AuthRepository,
) {
    public operator fun invoke(): Flow<TwoFactorOutcome> = repository.twoFactorDisable()
}

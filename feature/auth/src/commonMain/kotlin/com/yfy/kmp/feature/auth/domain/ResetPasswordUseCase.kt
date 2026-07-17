package com.yfy.kmp.feature.auth.domain

import com.yfy.kmp.core.common.base.BaseFlowUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

public sealed interface ResetPasswordOutcome {
    public data object Success : ResetPasswordOutcome
    public data object InvalidToken : ResetPasswordOutcome
    public data class InvalidInput(val reason: InputError) : ResetPasswordOutcome
}

public data class ResetPasswordParams(
    val token: String,
    val newPassword: String,
    val confirmPassword: String,
)

public class ResetPasswordUseCase(
    private val repository: AuthRepository,
) : BaseFlowUseCase<ResetPasswordParams, ResetPasswordOutcome>() {

    override fun invoke(parameters: ResetPasswordParams): Flow<ResetPasswordOutcome> = when {
        parameters.newPassword.length < 4 ->
            flowOf(ResetPasswordOutcome.InvalidInput(InputError.PASSWORD_TOO_SHORT))

        parameters.newPassword != parameters.confirmPassword ->
            flowOf(ResetPasswordOutcome.InvalidInput(InputError.PASSWORD_MISMATCH))

        else -> repository.resetPassword(parameters.token, parameters.newPassword)
    }
}

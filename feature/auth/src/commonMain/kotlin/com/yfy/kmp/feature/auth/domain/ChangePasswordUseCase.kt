package com.yfy.kmp.feature.auth.domain

import com.yfy.kmp.core.common.base.BaseFlowUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

public sealed interface ChangePasswordOutcome {
    public data object Success : ChangePasswordOutcome
    public data object WrongCurrent : ChangePasswordOutcome
    public data class InvalidInput(val reason: InputError) : ChangePasswordOutcome
}

public data class ChangePasswordParams(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String,
)

public class ChangePasswordUseCase(
    private val repository: AuthRepository,
) : BaseFlowUseCase<ChangePasswordParams, ChangePasswordOutcome>() {

    override fun invoke(parameters: ChangePasswordParams): Flow<ChangePasswordOutcome> = when {
        parameters.newPassword.length < 4 ->
            flowOf(ChangePasswordOutcome.InvalidInput(InputError.PASSWORD_TOO_SHORT))

        parameters.newPassword != parameters.confirmPassword ->
            flowOf(ChangePasswordOutcome.InvalidInput(InputError.PASSWORD_MISMATCH))

        else -> repository.changePassword(parameters.currentPassword, parameters.newPassword)
    }
}

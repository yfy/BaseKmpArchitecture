package com.yfy.kmp.feature.auth.domain

import com.yfy.kmp.core.common.base.BaseFlowUseCase
import com.yfy.kmp.core.common.ext.isValidEmail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

public sealed interface ForgotPasswordOutcome {
    public data object Success : ForgotPasswordOutcome
    public data class InvalidInput(val reason: InputError) : ForgotPasswordOutcome
}

public class ForgotPasswordUseCase(
    private val repository: AuthRepository,
) : BaseFlowUseCase<String, ForgotPasswordOutcome>() {

    override fun invoke(parameters: String): Flow<ForgotPasswordOutcome> = when {
        !parameters.isValidEmail() ->
            flowOf(ForgotPasswordOutcome.InvalidInput(InputError.EMAIL_FORMAT))

        else -> repository.forgotPassword(parameters)
    }
}

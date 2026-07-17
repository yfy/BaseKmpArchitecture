package com.yfy.kmp.feature.auth.domain

import com.yfy.kmp.core.common.base.BaseFlowUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

public sealed interface EmailVerifyOutcome {
    public data object Success : EmailVerifyOutcome
    public data object InvalidCode : EmailVerifyOutcome
    public data class InvalidInput(val reason: InputError) : EmailVerifyOutcome
}

public data class EmailVerifyParams(val email: String, val code: String)

public class EmailVerifyUseCase(
    private val repository: AuthRepository,
) : BaseFlowUseCase<EmailVerifyParams, EmailVerifyOutcome>() {

    override fun invoke(parameters: EmailVerifyParams): Flow<EmailVerifyOutcome> = when {
        parameters.code.length != CODE_LENGTH || parameters.code.any { !it.isDigit() } ->
            flowOf(EmailVerifyOutcome.InvalidInput(InputError.CODE_FORMAT))

        else -> repository.verifyEmail(parameters)
    }

    private companion object {
        const val CODE_LENGTH = 6
    }
}

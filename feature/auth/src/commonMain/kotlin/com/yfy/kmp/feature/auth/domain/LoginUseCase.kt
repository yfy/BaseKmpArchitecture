package com.yfy.kmp.feature.auth.domain

import com.yfy.kmp.core.common.base.BaseFlowUseCase
import com.yfy.kmp.core.common.ext.isValidEmail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

public data class LoginParams(val email: String, val password: String)

public class LoginUseCase(
    private val repository: AuthRepository,
) : BaseFlowUseCase<LoginParams, LoginOutcome>() {

    override fun invoke(parameters: LoginParams): Flow<LoginOutcome> = when {
        !parameters.email.isValidEmail() ->
            flowOf(LoginOutcome.InvalidInput(InputError.EMAIL_FORMAT))

        parameters.password.length < 4 ->
            flowOf(LoginOutcome.InvalidInput(InputError.PASSWORD_TOO_SHORT))

        else -> repository.login(parameters.email, parameters.password)
    }
}

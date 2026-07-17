package com.yfy.kmp.feature.auth.domain

import com.yfy.kmp.core.common.base.BaseFlowUseCase
import com.yfy.kmp.core.common.ext.isValidEmail
import com.yfy.kmp.core.model.AuthUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

public sealed interface SignupOutcome {
    public data class Success(val user: AuthUser) : SignupOutcome
    public data object EmailTaken : SignupOutcome
    public data class InvalidInput(val reason: InputError) : SignupOutcome
}

public data class SignupParams(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
)

public class SignupUseCase(
    private val repository: AuthRepository,
) : BaseFlowUseCase<SignupParams, SignupOutcome>() {

    override fun invoke(parameters: SignupParams): Flow<SignupOutcome> = when {
        parameters.firstName.isBlank() || parameters.lastName.isBlank() ->
            flowOf(SignupOutcome.InvalidInput(InputError.NAME_REQUIRED))

        !parameters.email.isValidEmail() ->
            flowOf(SignupOutcome.InvalidInput(InputError.EMAIL_FORMAT))

        parameters.password.length < 4 ->
            flowOf(SignupOutcome.InvalidInput(InputError.PASSWORD_TOO_SHORT))

        parameters.password != parameters.confirmPassword ->
            flowOf(SignupOutcome.InvalidInput(InputError.PASSWORD_MISMATCH))

        else -> repository.signup(parameters)
    }
}

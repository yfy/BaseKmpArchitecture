package com.yfy.kmp.feature.auth.presentation

import com.yfy.kmp.core.common.base.BaseViewModel
import com.yfy.kmp.core.common.result.AppError
import com.yfy.kmp.feature.auth.domain.ForgotPasswordOutcome
import com.yfy.kmp.feature.auth.domain.ForgotPasswordUseCase
import com.yfy.kmp.feature.auth.domain.InputError
import kotlinx.coroutines.flow.onStart

public enum class ForgotPasswordUiError {
    EMAIL_FORMAT,
    NETWORK,
    SERVER,
    UNKNOWN,
}

public data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val sent: Boolean = false,
    val error: ForgotPasswordUiError? = null,
)

public class ForgotPasswordViewModel(
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
) : BaseViewModel<ForgotPasswordUiState, Nothing>(ForgotPasswordUiState()) {

    override fun onServiceError(error: AppError) {
        setState { copy(isLoading = false, error = error.toUiError()) }
    }

    public fun onEmailChange(value: String) { setState { copy(email = value, error = null) } }

    public fun submit() {
        if (currentState.isLoading) return
        val email = currentState.email
        serviceLaunch {
            forgotPasswordUseCase(email)
                .onStart { setState { copy(isLoading = true, error = null, sent = false) } }
                .collect { outcome ->
                    setState {
                        when (outcome) {
                            is ForgotPasswordOutcome.Success -> copy(isLoading = false, sent = true, error = null)
                            is ForgotPasswordOutcome.InvalidInput ->
                                copy(isLoading = false, error = ForgotPasswordUiError.EMAIL_FORMAT)
                        }
                    }
                }
        }
    }
}

private fun AppError.toUiError(): ForgotPasswordUiError = when (this) {
    is AppError.Network -> ForgotPasswordUiError.NETWORK
    is AppError.NoConnectivity -> ForgotPasswordUiError.NETWORK
    is AppError.Timeout -> ForgotPasswordUiError.NETWORK
    is AppError.Server -> ForgotPasswordUiError.SERVER
    is AppError.Unauthorized -> ForgotPasswordUiError.UNKNOWN
    is AppError.Unknown -> ForgotPasswordUiError.UNKNOWN
}

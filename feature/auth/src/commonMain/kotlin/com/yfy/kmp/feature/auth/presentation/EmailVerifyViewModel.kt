package com.yfy.kmp.feature.auth.presentation

import com.yfy.kmp.core.common.base.BaseViewModel
import com.yfy.kmp.core.common.result.AppError
import com.yfy.kmp.feature.auth.domain.EmailVerifyOutcome
import com.yfy.kmp.feature.auth.domain.EmailVerifyParams
import com.yfy.kmp.feature.auth.domain.EmailVerifyUseCase
import kotlinx.coroutines.flow.onStart

public enum class EmailVerifyUiError {
    CODE_FORMAT,
    INVALID_CODE,
    NETWORK,
    SERVER,
    UNKNOWN,
}

public data class EmailVerifyUiState(
    val email: String = "",
    val code: String = "",
    val isLoading: Boolean = false,
    val verified: Boolean = false,
    val error: EmailVerifyUiError? = null,
)

public class EmailVerifyViewModel(
    private val emailVerifyUseCase: EmailVerifyUseCase,
) : BaseViewModel<EmailVerifyUiState, Nothing>(EmailVerifyUiState()) {

    override fun onServiceError(error: AppError) {
        setState { copy(isLoading = false, error = error.toUiError()) }
    }

    public fun setEmail(email: String) { setState { copy(email = email) } }
    public fun onCodeChange(value: String) { setState { copy(code = value, error = null) } }

    public fun verify() {
        if (currentState.isLoading) return
        val s = currentState
        serviceLaunch {
            emailVerifyUseCase(EmailVerifyParams(s.email, s.code))
                .onStart { setState { copy(isLoading = true, error = null) } }
                .collect { outcome ->
                    setState {
                        when (outcome) {
                            is EmailVerifyOutcome.Success -> copy(isLoading = false, verified = true, error = null)
                            is EmailVerifyOutcome.InvalidCode -> copy(isLoading = false, error = EmailVerifyUiError.INVALID_CODE)
                            is EmailVerifyOutcome.InvalidInput -> copy(isLoading = false, error = EmailVerifyUiError.CODE_FORMAT)
                        }
                    }
                }
        }
    }
}

private fun AppError.toUiError(): EmailVerifyUiError = when (this) {
    is AppError.Network -> EmailVerifyUiError.NETWORK
    is AppError.NoConnectivity -> EmailVerifyUiError.NETWORK
    is AppError.Timeout -> EmailVerifyUiError.NETWORK
    is AppError.Server -> EmailVerifyUiError.SERVER
    is AppError.Unauthorized -> EmailVerifyUiError.UNKNOWN
    is AppError.Unknown -> EmailVerifyUiError.UNKNOWN
}

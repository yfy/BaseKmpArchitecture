package com.yfy.kmp.feature.auth.presentation

import com.yfy.kmp.core.common.base.BaseViewModel
import com.yfy.kmp.core.common.result.AppError
import com.yfy.kmp.feature.auth.domain.InputError
import com.yfy.kmp.feature.auth.domain.ResetPasswordOutcome
import com.yfy.kmp.feature.auth.domain.ResetPasswordParams
import com.yfy.kmp.feature.auth.domain.ResetPasswordUseCase
import kotlinx.coroutines.flow.onStart

public enum class ResetPasswordUiError {
    PASSWORD_TOO_SHORT,
    PASSWORD_MISMATCH,
    INVALID_TOKEN,
    NETWORK,
    SERVER,
    UNKNOWN,
}

public data class ResetPasswordUiState(
    val token: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val done: Boolean = false,
    val error: ResetPasswordUiError? = null,
)

public class ResetPasswordViewModel(
    private val resetPasswordUseCase: ResetPasswordUseCase,
) : BaseViewModel<ResetPasswordUiState, Nothing>(ResetPasswordUiState()) {

    override fun onServiceError(error: AppError) {
        setState { copy(isLoading = false, error = error.toUiError()) }
    }

    public fun setToken(token: String) { setState { copy(token = token) } }
    public fun onNewPasswordChange(value: String) { setState { copy(newPassword = value, error = null) } }
    public fun onConfirmPasswordChange(value: String) { setState { copy(confirmPassword = value, error = null) } }

    public fun submit() {
        if (currentState.isLoading) return
        val params = ResetPasswordParams(
            currentState.token,
            currentState.newPassword,
            currentState.confirmPassword,
        )
        serviceLaunch {
            resetPasswordUseCase(params)
                .onStart { setState { copy(isLoading = true, error = null, done = false) } }
                .collect { outcome ->
                    setState {
                        when (outcome) {
                            is ResetPasswordOutcome.Success -> copy(isLoading = false, done = true, error = null)
                            is ResetPasswordOutcome.InvalidToken ->
                                copy(isLoading = false, error = ResetPasswordUiError.INVALID_TOKEN)
                            is ResetPasswordOutcome.InvalidInput ->
                                copy(isLoading = false, error = outcome.reason.toUiError())
                        }
                    }
                }
        }
    }
}

private fun InputError.toUiError(): ResetPasswordUiError = when (this) {
    InputError.PASSWORD_TOO_SHORT -> ResetPasswordUiError.PASSWORD_TOO_SHORT
    InputError.PASSWORD_MISMATCH -> ResetPasswordUiError.PASSWORD_MISMATCH
    InputError.EMAIL_FORMAT, InputError.NAME_REQUIRED, InputError.CODE_FORMAT -> ResetPasswordUiError.UNKNOWN
}

private fun AppError.toUiError(): ResetPasswordUiError = when (this) {
    is AppError.Network -> ResetPasswordUiError.NETWORK
    is AppError.NoConnectivity -> ResetPasswordUiError.NETWORK
    is AppError.Timeout -> ResetPasswordUiError.NETWORK
    is AppError.Server -> ResetPasswordUiError.SERVER
    is AppError.Unauthorized -> ResetPasswordUiError.UNKNOWN
    is AppError.Unknown -> ResetPasswordUiError.UNKNOWN
}

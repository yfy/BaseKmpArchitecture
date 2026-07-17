package com.yfy.kmp.feature.auth.presentation

import com.yfy.kmp.core.common.base.BaseViewModel
import com.yfy.kmp.core.common.result.AppError
import com.yfy.kmp.feature.auth.domain.ChangePasswordOutcome
import com.yfy.kmp.feature.auth.domain.ChangePasswordParams
import com.yfy.kmp.feature.auth.domain.ChangePasswordUseCase
import com.yfy.kmp.feature.auth.domain.InputError
import kotlinx.coroutines.flow.onStart

public enum class ChangePasswordUiError {
    PASSWORD_TOO_SHORT,
    PASSWORD_MISMATCH,
    WRONG_CURRENT,
    NETWORK,
    SERVER,
    UNKNOWN,
}

public data class ChangePasswordUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val done: Boolean = false,
    val error: ChangePasswordUiError? = null,
)

public class ChangePasswordViewModel(
    private val changePasswordUseCase: ChangePasswordUseCase,
) : BaseViewModel<ChangePasswordUiState, Nothing>(ChangePasswordUiState()) {

    override fun onServiceError(error: AppError) {
        setState { copy(isLoading = false, error = error.toUiError()) }
    }

    public fun onCurrentPasswordChange(value: String) { setState { copy(currentPassword = value, error = null) } }
    public fun onNewPasswordChange(value: String) { setState { copy(newPassword = value, error = null) } }
    public fun onConfirmPasswordChange(value: String) { setState { copy(confirmPassword = value, error = null) } }

    public fun submit() {
        if (currentState.isLoading) return
        val params = ChangePasswordParams(
            currentState.currentPassword,
            currentState.newPassword,
            currentState.confirmPassword,
        )
        serviceLaunch {
            changePasswordUseCase(params)
                .onStart { setState { copy(isLoading = true, error = null, done = false) } }
                .collect { outcome ->
                    setState {
                        when (outcome) {
                            is ChangePasswordOutcome.Success -> copy(isLoading = false, done = true, error = null)
                            is ChangePasswordOutcome.WrongCurrent ->
                                copy(isLoading = false, error = ChangePasswordUiError.WRONG_CURRENT)
                            is ChangePasswordOutcome.InvalidInput ->
                                copy(isLoading = false, error = outcome.reason.toUiError())
                        }
                    }
                }
        }
    }
}

private fun InputError.toUiError(): ChangePasswordUiError = when (this) {
    InputError.PASSWORD_TOO_SHORT -> ChangePasswordUiError.PASSWORD_TOO_SHORT
    InputError.PASSWORD_MISMATCH -> ChangePasswordUiError.PASSWORD_MISMATCH
    InputError.EMAIL_FORMAT, InputError.NAME_REQUIRED, InputError.CODE_FORMAT -> ChangePasswordUiError.UNKNOWN
}

private fun AppError.toUiError(): ChangePasswordUiError = when (this) {
    is AppError.Network -> ChangePasswordUiError.NETWORK
    is AppError.NoConnectivity -> ChangePasswordUiError.NETWORK
    is AppError.Timeout -> ChangePasswordUiError.NETWORK
    is AppError.Server -> ChangePasswordUiError.SERVER
    is AppError.Unauthorized -> ChangePasswordUiError.WRONG_CURRENT
    is AppError.Unknown -> ChangePasswordUiError.UNKNOWN
}

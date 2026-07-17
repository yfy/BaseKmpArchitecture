package com.yfy.kmp.feature.auth.presentation

import com.yfy.kmp.core.common.base.BaseViewModel
import com.yfy.kmp.core.common.result.AppError
import com.yfy.kmp.core.datastore.PreferenceKeys
import com.yfy.kmp.core.datastore.PreferencesStore
import com.yfy.kmp.core.model.AuthUser
import com.yfy.kmp.feature.auth.domain.InputError
import com.yfy.kmp.feature.auth.domain.SignupOutcome
import com.yfy.kmp.feature.auth.domain.SignupParams
import com.yfy.kmp.feature.auth.domain.SignupUseCase
import kotlinx.coroutines.flow.onStart

public enum class SignupUiError {
    NAME_REQUIRED,
    EMAIL_FORMAT,
    PASSWORD_TOO_SHORT,
    PASSWORD_MISMATCH,
    EMAIL_TAKEN,
    NETWORK,
    SERVER,
    UNKNOWN,
}

public data class SignupUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val termsAccepted: Boolean = false,
    val isLoading: Boolean = false,
    val user: AuthUser? = null,
    val error: SignupUiError? = null,
)

public class SignupViewModel(
    private val signupUseCase: SignupUseCase,
    private val preferences: PreferencesStore,
) : BaseViewModel<SignupUiState, Nothing>(SignupUiState()) {

    override fun onServiceError(error: AppError) {
        setState { copy(isLoading = false, error = error.toSignupUiError()) }
    }

    public fun onFirstNameChange(value: String) { setState { copy(firstName = value, error = null) } }
    public fun onLastNameChange(value: String) { setState { copy(lastName = value, error = null) } }
    public fun onEmailChange(value: String) { setState { copy(email = value, error = null) } }
    public fun onPasswordChange(value: String) { setState { copy(password = value, error = null) } }
    public fun onConfirmPasswordChange(value: String) { setState { copy(confirmPassword = value, error = null) } }
    public fun onTermsAcceptedChange(value: Boolean) { setState { copy(termsAccepted = value) } }

    public fun signup() {
        if (currentState.isLoading || !currentState.termsAccepted) return
        val s = currentState
        serviceLaunch {
            signupUseCase(SignupParams(s.firstName, s.lastName, s.email, s.password, s.confirmPassword))
                .onStart { setState { copy(isLoading = true, error = null) } }
                .collect { outcome ->
                    if (outcome is SignupOutcome.Success) {
                        preferences.putString(PreferenceKeys.CURRENT_USER_ID, outcome.user.id)
                    }
                    setState {
                        when (outcome) {
                            is SignupOutcome.Success -> copy(isLoading = false, user = outcome.user, error = null)
                            is SignupOutcome.EmailTaken -> copy(isLoading = false, error = SignupUiError.EMAIL_TAKEN)
                            is SignupOutcome.InvalidInput -> copy(isLoading = false, error = outcome.reason.toSignupUiError())
                        }
                    }
                }
        }
    }
}

private fun InputError.toSignupUiError(): SignupUiError = when (this) {
    InputError.NAME_REQUIRED -> SignupUiError.NAME_REQUIRED
    InputError.EMAIL_FORMAT -> SignupUiError.EMAIL_FORMAT
    InputError.PASSWORD_TOO_SHORT -> SignupUiError.PASSWORD_TOO_SHORT
    InputError.PASSWORD_MISMATCH -> SignupUiError.PASSWORD_MISMATCH
    InputError.CODE_FORMAT -> SignupUiError.UNKNOWN
}

private fun com.yfy.kmp.core.common.result.AppError.toSignupUiError(): SignupUiError = when (this) {
    is com.yfy.kmp.core.common.result.AppError.Network -> SignupUiError.NETWORK
    is com.yfy.kmp.core.common.result.AppError.NoConnectivity -> SignupUiError.NETWORK
    is com.yfy.kmp.core.common.result.AppError.Timeout -> SignupUiError.NETWORK
    is com.yfy.kmp.core.common.result.AppError.Server -> SignupUiError.SERVER
    is com.yfy.kmp.core.common.result.AppError.Unauthorized -> SignupUiError.UNKNOWN
    is com.yfy.kmp.core.common.result.AppError.Unknown -> SignupUiError.UNKNOWN
}

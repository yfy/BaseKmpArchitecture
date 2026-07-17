package com.yfy.kmp.feature.auth.presentation

import com.yfy.kmp.core.analytics.AnalyticsTracker
import com.yfy.kmp.core.common.base.BaseViewModel
import com.yfy.kmp.core.common.result.AppError
import com.yfy.kmp.core.datastore.PreferenceKeys
import com.yfy.kmp.core.datastore.PreferencesStore
import com.yfy.kmp.core.model.AuthUser
import com.yfy.kmp.feature.auth.domain.InputError
import com.yfy.kmp.feature.auth.domain.LoginOutcome
import com.yfy.kmp.feature.auth.domain.LoginParams
import com.yfy.kmp.feature.auth.domain.ExchangeSocialTokenUseCase
import com.yfy.kmp.feature.auth.domain.LoginUseCase
import com.yfy.kmp.feature.auth.domain.SocialLoginUseCase
import com.yfy.kmp.feature.auth.domain.SocialProvider
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

public enum class LoginUiError {
    EMAIL_FORMAT,
    PASSWORD_TOO_SHORT,
    INVALID_CREDENTIALS,
    NETWORK,
    SERVER,
    UNKNOWN,
}

public data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val user: AuthUser? = null,
    val error: LoginUiError? = null,
)

public class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val socialLoginUseCase: SocialLoginUseCase,
    private val exchangeSocialTokenUseCase: ExchangeSocialTokenUseCase,
    private val preferences: PreferencesStore,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<LoginUiState, Nothing>(LoginUiState()) {

    override fun onServiceError(error: AppError) {
        setState { copy(isLoading = false, error = error.toUiError()) }
    }

    init {
        scope.launch {
            val lastEmail = preferences.getString(KEY_LAST_EMAIL)
            if (!lastEmail.isNullOrBlank() && currentState.email.isBlank()) {
                setState { copy(email = lastEmail, rememberMe = true) }
            }
        }
    }

    public fun onEmailChange(value: String) { setState { copy(email = value, error = null) } }
    public fun onPasswordChange(value: String) { setState { copy(password = value, error = null) } }
    public fun onRememberMeChange(value: Boolean) { setState { copy(rememberMe = value) } }

    public fun login() {
        if (currentState.isLoading) return
        serviceLaunch {
            loginUseCase(LoginParams(currentState.email, currentState.password))
                .onStart { setState { copy(isLoading = true, error = null) } }
                .collect { outcome -> onOutcome(outcome) }
        }
    }

    public fun socialLogin(provider: SocialProvider) {
        if (currentState.isLoading) return
        serviceLaunch {
            socialLoginUseCase(provider)
                .onStart { setState { copy(isLoading = true, error = null) } }
                .collect { outcome -> onOutcome(outcome) }
        }
    }

    public fun socialLoginWithToken(provider: SocialProvider, token: String) {
        if (currentState.isLoading) return
        serviceLaunch {
            exchangeSocialTokenUseCase(provider, token)
                .onStart { setState { copy(isLoading = true, error = null) } }
                .collect { outcome -> onOutcome(outcome) }
        }
    }

    private suspend fun onOutcome(outcome: LoginOutcome) {
        when (outcome) {
            is LoginOutcome.Success -> {
                if (currentState.rememberMe) {
                    preferences.putString(KEY_LAST_EMAIL, currentState.email)
                } else {
                    preferences.putString(KEY_LAST_EMAIL, "")
                }
                preferences.putString(PreferenceKeys.CURRENT_USER_ID, outcome.user.id)
                analytics.setUserId(outcome.user.id)
                analytics.logEvent("login_succeeded")
            }
            is LoginOutcome.InvalidCredentials ->
                analytics.logEvent("login_failed", mapOf("reason" to "invalid_credentials"))
            is LoginOutcome.InvalidInput ->
                analytics.logEvent("login_failed", mapOf("reason" to outcome.reason.name.lowercase()))
        }
        setState {
            when (outcome) {
                is LoginOutcome.Success ->
                    copy(isLoading = false, user = outcome.user, error = null)
                is LoginOutcome.InvalidCredentials ->
                    copy(isLoading = false, error = LoginUiError.INVALID_CREDENTIALS)
                is LoginOutcome.InvalidInput ->
                    copy(isLoading = false, error = outcome.reason.toUiError())
            }
        }
    }

    private companion object {
        const val KEY_LAST_EMAIL = "last_login_email"
    }
}

private fun InputError.toUiError(): LoginUiError = when (this) {
    InputError.EMAIL_FORMAT -> LoginUiError.EMAIL_FORMAT
    InputError.PASSWORD_TOO_SHORT -> LoginUiError.PASSWORD_TOO_SHORT
    InputError.PASSWORD_MISMATCH, InputError.NAME_REQUIRED, InputError.CODE_FORMAT -> LoginUiError.UNKNOWN
}

private fun AppError.toUiError(): LoginUiError = when (this) {
    is AppError.Unauthorized -> LoginUiError.INVALID_CREDENTIALS
    is AppError.Network -> LoginUiError.NETWORK
    is AppError.NoConnectivity -> LoginUiError.NETWORK
    is AppError.Timeout -> LoginUiError.NETWORK
    is AppError.Server -> LoginUiError.SERVER
    is AppError.Unknown -> LoginUiError.UNKNOWN
}

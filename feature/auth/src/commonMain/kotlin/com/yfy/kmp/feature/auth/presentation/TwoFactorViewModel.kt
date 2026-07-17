package com.yfy.kmp.feature.auth.presentation

import com.yfy.kmp.core.common.base.BaseViewModel
import com.yfy.kmp.core.common.result.AppError
import com.yfy.kmp.feature.auth.domain.TwoFactorDisableUseCase
import com.yfy.kmp.feature.auth.domain.TwoFactorEnableUseCase
import com.yfy.kmp.feature.auth.domain.TwoFactorOutcome
import com.yfy.kmp.feature.auth.domain.TwoFactorVerifyUseCase
import kotlinx.coroutines.flow.onStart

public enum class TwoFactorUiError {
    CODE_FORMAT,
    INVALID_CODE,
    NETWORK,
    SERVER,
    UNKNOWN,
}

public data class TwoFactorUiState(
    val enabled: Boolean = false,
    val secret: String? = null,
    val code: String = "",
    val isLoading: Boolean = false,
    val error: TwoFactorUiError? = null,
)

public class TwoFactorViewModel(
    private val enableUseCase: TwoFactorEnableUseCase,
    private val verifyUseCase: TwoFactorVerifyUseCase,
    private val disableUseCase: TwoFactorDisableUseCase,
) : BaseViewModel<TwoFactorUiState, Nothing>(TwoFactorUiState()) {

    override fun onServiceError(error: AppError) {
        setState { copy(isLoading = false, error = error.toUiError()) }
    }

    public fun onCodeChange(value: String) { setState { copy(code = value, error = null) } }

    public fun startEnable() {
        if (currentState.isLoading) return
        serviceLaunch {
            enableUseCase()
                .onStart { setState { copy(isLoading = true, error = null) } }
                .collect { outcome -> applyOutcome(outcome) }
        }
    }

    public fun verify() {
        if (currentState.isLoading) return
        val code = currentState.code
        serviceLaunch {
            verifyUseCase(code)
                .onStart { setState { copy(isLoading = true, error = null) } }
                .collect { outcome -> applyOutcome(outcome) }
        }
    }

    public fun disable() {
        if (currentState.isLoading) return
        serviceLaunch {
            disableUseCase()
                .onStart { setState { copy(isLoading = true, error = null) } }
                .collect { outcome -> applyOutcome(outcome) }
        }
    }

    private fun applyOutcome(outcome: TwoFactorOutcome) {
        setState {
            when (outcome) {
                is TwoFactorOutcome.SetupReady -> copy(isLoading = false, secret = outcome.secret, error = null)
                is TwoFactorOutcome.Enabled -> copy(isLoading = false, enabled = true, secret = null, code = "", error = null)
                is TwoFactorOutcome.Disabled -> copy(isLoading = false, enabled = false, secret = null, code = "", error = null)
                is TwoFactorOutcome.InvalidCode -> copy(isLoading = false, error = TwoFactorUiError.INVALID_CODE)
                is TwoFactorOutcome.InvalidInput -> copy(isLoading = false, error = TwoFactorUiError.CODE_FORMAT)
            }
        }
    }
}

private fun AppError.toUiError(): TwoFactorUiError = when (this) {
    is AppError.Network -> TwoFactorUiError.NETWORK
    is AppError.NoConnectivity -> TwoFactorUiError.NETWORK
    is AppError.Timeout -> TwoFactorUiError.NETWORK
    is AppError.Server -> TwoFactorUiError.SERVER
    is AppError.Unauthorized -> TwoFactorUiError.UNKNOWN
    is AppError.Unknown -> TwoFactorUiError.UNKNOWN
}

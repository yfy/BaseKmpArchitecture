package com.yfy.kmp.core.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.yfy.kmp.core.analytics.AnalyticsTracker
import com.yfy.kmp.core.common.result.AppError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform

public abstract class BaseViewModel<S, E>(initialState: S) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    public val state: StateFlow<S> = _state.asStateFlow()

    private val _effects = Channel<E>(Channel.BUFFERED)
    public val effects: Flow<E> = _effects.receiveAsFlow()

    protected val currentState: S get() = _state.value

    protected fun setState(reducer: S.() -> S) {
        _state.update { it.reducer() }
    }

    protected fun emitEffect(effect: E) {
        scope.launch { _effects.send(effect) }
    }

    protected val scope: CoroutineScope get() = viewModelScope

    private val errorAnalytics: AnalyticsTracker? by lazy {
        runCatching { KoinPlatform.getKoin().get<AnalyticsTracker>() }.getOrNull()
    }

    protected open fun onServiceError(error: AppError) {}

    protected fun serviceLaunch(
        onError: (AppError) -> Unit = ::onServiceError,
        block: suspend CoroutineScope.() -> Unit,
    ): Job = scope.serviceLaunch(
        onError = { error ->
            errorAnalytics?.logEvent("app_error", mapOf("type" to (error::class.simpleName ?: "unknown")))
            onError(error)
        },
        block = block,
    )

    private var isManuallyCleared = false

    // Has no Kotlin caller by design: iOS has no ViewModelStoreOwner, so the Swift shell calls this on
    // deinit. Android's ViewModelStore already cancels the scope, so a second call is a no-op.
    public fun clear() {
        if (isManuallyCleared) return
        isManuallyCleared = true
        Logger.d { "ViewModel cleared: ${this::class.simpleName}" }
        viewModelScope.cancel()
    }
}

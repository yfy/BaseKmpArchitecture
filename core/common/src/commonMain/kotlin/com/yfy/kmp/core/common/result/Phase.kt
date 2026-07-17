package com.yfy.kmp.core.common.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

public sealed interface Phase<out T> {
    public data object Loading : Phase<Nothing>
    public data class Success<T>(val value: T) : Phase<T>
    public data class Failed(val error: AppError) : Phase<Nothing>
}

public fun <T> Flow<T>.asPhaseFlow(): Flow<Phase<T>> =
    map<T, Phase<T>> { Phase.Success(it) }
        .onStart { emit(Phase.Loading) }
        .catch { emit(Phase.Failed(it.toAppError())) }

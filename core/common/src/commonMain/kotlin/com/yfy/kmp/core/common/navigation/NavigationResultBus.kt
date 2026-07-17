package com.yfy.kmp.core.common.navigation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

public class NavigationResultBus {

    private val _results = MutableSharedFlow<NavigationResult>(extraBufferCapacity = 32)

    public val results: Flow<NavigationResult> = _results.asSharedFlow()

    public fun post(key: String, value: String) {
        _results.tryEmit(NavigationResult(key, value))
    }

    public fun resultsFor(key: String): Flow<String> =
        results.filter { it.key == key }.map { it.value }
}

public data class NavigationResult(
    public val key: String,
    public val value: String,
)

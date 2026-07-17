package com.yfy.kmp.core.common.base

import com.yfy.kmp.core.common.result.AppError
import com.yfy.kmp.core.common.result.toAppError
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

public fun CoroutineScope.serviceLaunch(
    onError: (AppError) -> Unit = {},
    block: suspend CoroutineScope.() -> Unit,
): Job {
    val handler = CoroutineExceptionHandler { _, throwable -> onError(throwable.toAppError()) }
    return launch(handler, block = block)
}

public fun CoroutineScope.simpleLaunch(block: suspend CoroutineScope.() -> Unit): Job =
    launch(block = block)

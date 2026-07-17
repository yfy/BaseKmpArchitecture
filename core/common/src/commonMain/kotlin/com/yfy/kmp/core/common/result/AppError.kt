package com.yfy.kmp.core.common.result

import kotlinx.coroutines.CancellationException

public sealed class AppError {
    public data object NoConnectivity : AppError()

    public data object Timeout : AppError()

    public data class Network(val cause: Throwable? = null) : AppError()

    public data class Server(val code: Int) : AppError()

    public data object Unauthorized : AppError()

    public data class Unknown(val cause: Throwable? = null) : AppError()
}

public class NetworkException(cause: Throwable? = null) : Exception(cause)
public class ServerException(public val code: Int, cause: Throwable? = null) : Exception("HTTP $code", cause)
public class UnauthorizedException(cause: Throwable? = null) : Exception(cause)
public class NoConnectivityException(cause: Throwable? = null) : Exception(cause)
public class TimeoutException(cause: Throwable? = null) : Exception(cause)

public fun Throwable.toAppError(): AppError {
    // Cancellation must stay an exception rather than become an error value, or a screen closing
    // mid-request surfaces as a failure.
    if (this is CancellationException) throw this
    return when (this) {
        is UnauthorizedException -> AppError.Unauthorized
        is NoConnectivityException -> AppError.NoConnectivity
        is TimeoutException -> AppError.Timeout
        is ServerException -> AppError.Server(code)
        is NetworkException -> AppError.Network(cause)
        else -> AppError.Unknown(this)
    }
}

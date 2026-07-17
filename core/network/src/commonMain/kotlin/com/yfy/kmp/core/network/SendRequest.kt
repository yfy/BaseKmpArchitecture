package com.yfy.kmp.core.network

import com.yfy.kmp.core.common.result.NetworkException
import com.yfy.kmp.core.common.result.NoConnectivityException
import com.yfy.kmp.core.common.result.ServerException
import com.yfy.kmp.core.common.result.TimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

public fun <T> sendRequest(
    recover: (HttpStatusCode) -> T? = { null },
    block: suspend () -> T,
): Flow<T> = flow {
    emit(
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: NoConnectivityException) {
            throw e
        } catch (e: HttpRequestTimeoutException) {
            throw TimeoutException(e)
        } catch (e: ResponseException) {
            recover(e.response.status) ?: throw ServerException(e.response.status.value, e)
        } catch (e: Exception) {
            throw NetworkException(e)
        },
    )
}

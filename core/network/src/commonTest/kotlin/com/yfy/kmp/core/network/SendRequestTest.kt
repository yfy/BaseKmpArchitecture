package com.yfy.kmp.core.network

import com.yfy.kmp.core.common.result.NetworkException
import com.yfy.kmp.core.common.result.NoConnectivityException
import com.yfy.kmp.core.common.result.ServerException
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.statement.bodyAsText
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SendRequestTest {

    private fun clientReturning(status: HttpStatusCode): HttpClient =
        createHttpClient(MockEngine { respond(content = "{}", status = status) })

    @Test
    fun recover_maps_a_matching_status_to_a_value() = runTest {
        val client = clientReturning(HttpStatusCode.Unauthorized)
        val result = sendRequest(
            recover = { if (it == HttpStatusCode.Unauthorized) "recovered" else null },
        ) {
            client.get("https://test.local/x").bodyAsText()
        }.first()
        assertEquals("recovered", result)
    }

    @Test
    fun unrecovered_client_error_throws_server_exception_with_code() = runTest {
        val client = clientReturning(HttpStatusCode.Unauthorized)
        val e = assertFailsWith<ServerException> {
            sendRequest { client.get("https://test.local/x").bodyAsText() }.first()
        }
        assertEquals(401, e.code)
    }

    @Test
    fun server_error_throws_server_exception_with_code() = runTest {
        val client = clientReturning(HttpStatusCode.InternalServerError)
        val e = assertFailsWith<ServerException> {
            sendRequest { client.get("https://test.local/x").bodyAsText() }.first()
        }
        assertEquals(500, e.code)
    }

    @Test
    fun unexpected_failure_becomes_network_exception() = runTest {
        assertFailsWith<NetworkException> {
            sendRequest<String> { throw IllegalStateException("engine broke") }.first()
        }
    }

    @Test
    fun no_connectivity_passes_through_unwrapped() = runTest {
        assertFailsWith<NoConnectivityException> {
            sendRequest<String> { throw NoConnectivityException() }.first()
        }
    }

    @Test
    fun cancellation_passes_through_unwrapped() = runTest {
        assertFailsWith<CancellationException> {
            sendRequest<String> { throw CancellationException("cancelled") }.first()
        }
    }
}

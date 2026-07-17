package com.yfy.kmp.core.network.mock

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.delay

public data class MockRoute(
    val pathSuffix: String,
    val jsonName: String,
    val status: Int = 200,
)

public fun mockEngine(
    reader: MockJsonReader,
    routes: List<MockRoute>,
    delayMillis: Long = 0,
): HttpClientEngine =
    MockEngine { request ->
        delay(delayMillis)
        val route = routes.firstOrNull { request.url.encodedPath.endsWith(it.pathSuffix) }
        if (route != null) {
            respond(
                content = reader.read(route.jsonName),
                status = HttpStatusCode.fromValue(route.status),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        } else {
            respond(content = "Not Found", status = HttpStatusCode.NotFound)
        }
    }

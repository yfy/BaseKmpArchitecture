package com.yfy.kmp.core.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

public actual fun platformHttpEngine(): HttpClientEngine = OkHttp.create()

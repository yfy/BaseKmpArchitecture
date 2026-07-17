package com.yfy.kmp.core.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

public actual fun platformHttpEngine(): HttpClientEngine = Darwin.create()

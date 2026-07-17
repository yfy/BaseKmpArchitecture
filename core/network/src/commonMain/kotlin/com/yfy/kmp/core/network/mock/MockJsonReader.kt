package com.yfy.kmp.core.network.mock

import org.koin.core.module.Module

public interface MockJsonReader {
    public fun read(name: String): String
}

internal expect val platformMockModule: Module

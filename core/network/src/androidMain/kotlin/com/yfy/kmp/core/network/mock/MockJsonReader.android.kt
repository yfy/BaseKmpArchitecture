package com.yfy.kmp.core.network.mock

import android.content.Context
import org.koin.core.module.Module
import org.koin.dsl.module

internal class AndroidMockJsonReader(private val context: Context) : MockJsonReader {
    override fun read(name: String): String =
        context.assets.open("mock/$name.json").bufferedReader().use { it.readText() }
}

internal actual val platformMockModule: Module = module {
    single<MockJsonReader> { AndroidMockJsonReader(get()) }
}

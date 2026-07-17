package com.yfy.kmp.core.network.mock

import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfFile

@OptIn(ExperimentalForeignApi::class)
internal class IosMockJsonReader : MockJsonReader {
    override fun read(name: String): String {
        val path = NSBundle.mainBundle.pathForResource("mock/$name", "json")
            ?: NSBundle.mainBundle.pathForResource(name, "json", "MockResources/mock")
            ?: error("Mock JSON bulunamadı: $name.json")
        return NSString.stringWithContentsOfFile(path, NSUTF8StringEncoding, null)
            ?: error("Mock JSON okunamadı: $path")
    }
}

internal actual val platformMockModule: Module = module {
    single<MockJsonReader> { IosMockJsonReader() }
}

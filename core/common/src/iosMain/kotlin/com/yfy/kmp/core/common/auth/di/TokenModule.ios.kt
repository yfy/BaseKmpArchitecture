package com.yfy.kmp.core.common.auth.di

import com.yfy.kmp.core.common.auth.IosTokenStore
import com.yfy.kmp.core.common.auth.TokenStore
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformTokenModule: Module = module {
    single<TokenStore> { IosTokenStore() }
}

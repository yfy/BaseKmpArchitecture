package com.yfy.kmp.feature.auth.di

import com.yfy.kmp.feature.auth.data.GoogleAuthProvider
import com.yfy.kmp.feature.auth.domain.SocialAuthProvider
import org.koin.core.module.Module
import org.koin.dsl.module

public val googleSocialAuthModule: Module = module {
    single<SocialAuthProvider> { GoogleAuthProvider() }
}

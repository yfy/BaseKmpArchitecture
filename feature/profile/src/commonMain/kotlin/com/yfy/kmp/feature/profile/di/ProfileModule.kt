package com.yfy.kmp.feature.profile.di

import com.yfy.kmp.feature.profile.presentation.ProfileViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

public val profileModule: Module = module {
    factory { ProfileViewModel(preferences = get(), userCache = get()) }
}

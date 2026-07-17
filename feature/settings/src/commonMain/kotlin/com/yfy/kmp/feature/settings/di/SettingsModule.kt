package com.yfy.kmp.feature.settings.di

import com.yfy.kmp.feature.settings.presentation.LanguageViewModel
import com.yfy.kmp.feature.settings.presentation.SettingsViewModel
import com.yfy.kmp.feature.settings.presentation.ThemeViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

public val settingsModule: Module = module {
    factory { SettingsViewModel(preferences = get()) }
    single { ThemeViewModel(preferences = get()) }
    single { LanguageViewModel(preferences = get()) }
}

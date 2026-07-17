package com.yfy.kmp.feature.onboarding.di

import com.yfy.kmp.feature.onboarding.data.OnboardingRepositoryImpl
import com.yfy.kmp.feature.onboarding.domain.OnboardingRepository
import com.yfy.kmp.feature.onboarding.presentation.OnboardingViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

public val onboardingModule: Module = module {
    single<OnboardingRepository> { OnboardingRepositoryImpl(get()) }
    factory { OnboardingViewModel(repository = get()) }
}

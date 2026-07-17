package com.yfy.kmp.feature.auth.di

import com.yfy.kmp.core.network.NetworkConfig
import com.yfy.kmp.feature.auth.data.AuthApi
import com.yfy.kmp.feature.auth.data.AuthRepositoryImpl
import com.yfy.kmp.feature.auth.data.MockSocialAuthProvider
import com.yfy.kmp.feature.auth.domain.AuthRepository
import com.yfy.kmp.feature.auth.domain.ChangePasswordUseCase
import com.yfy.kmp.feature.auth.domain.ExchangeSocialTokenUseCase
import com.yfy.kmp.feature.auth.domain.EmailVerifyUseCase
import com.yfy.kmp.feature.auth.domain.ForgotPasswordUseCase
import com.yfy.kmp.feature.auth.domain.LoginUseCase
import com.yfy.kmp.feature.auth.domain.ResetPasswordUseCase
import com.yfy.kmp.feature.auth.domain.SignupUseCase
import com.yfy.kmp.feature.auth.domain.SocialAuthProvider
import com.yfy.kmp.feature.auth.domain.SocialLoginUseCase
import com.yfy.kmp.feature.auth.domain.TwoFactorDisableUseCase
import com.yfy.kmp.feature.auth.domain.TwoFactorEnableUseCase
import com.yfy.kmp.feature.auth.domain.TwoFactorVerifyUseCase
import com.yfy.kmp.feature.auth.presentation.ChangePasswordViewModel
import com.yfy.kmp.feature.auth.presentation.EmailVerifyViewModel
import com.yfy.kmp.feature.auth.presentation.ForgotPasswordViewModel
import com.yfy.kmp.feature.auth.presentation.LoginViewModel
import com.yfy.kmp.feature.auth.presentation.ResetPasswordViewModel
import com.yfy.kmp.feature.auth.presentation.SignupViewModel
import com.yfy.kmp.feature.auth.presentation.TwoFactorViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

public val authModule: Module = module {
    single { AuthApi(client = get(), baseUrl = get<NetworkConfig>().baseUrl) }
    single<AuthRepository> {
        AuthRepositoryImpl(api = get(), userCache = get(), tokenStore = get())
    }
    // Overridden by googleSocialAuthModule in PROD.
    single<SocialAuthProvider> { MockSocialAuthProvider() }
    factory { LoginUseCase(repository = get()) }
    factory { SocialLoginUseCase(repository = get(), socialAuthProvider = get()) }
    factory { ExchangeSocialTokenUseCase(repository = get()) }
    factory { SignupUseCase(repository = get()) }
    factory { ForgotPasswordUseCase(repository = get()) }
    factory { EmailVerifyUseCase(repository = get()) }
    factory { ChangePasswordUseCase(repository = get()) }
    factory { ResetPasswordUseCase(repository = get()) }
    factory { TwoFactorEnableUseCase(repository = get()) }
    factory { TwoFactorVerifyUseCase(repository = get()) }
    factory { TwoFactorDisableUseCase(repository = get()) }
    factory {
        LoginViewModel(
            loginUseCase = get(),
            socialLoginUseCase = get(),
            exchangeSocialTokenUseCase = get(),
            preferences = get(),
            analytics = get(),
        )
    }
    factory { SignupViewModel(signupUseCase = get(), preferences = get()) }
    factory { ForgotPasswordViewModel(forgotPasswordUseCase = get()) }
    factory { EmailVerifyViewModel(emailVerifyUseCase = get()) }
    factory { ChangePasswordViewModel(changePasswordUseCase = get()) }
    factory { ResetPasswordViewModel(resetPasswordUseCase = get()) }
    factory { TwoFactorViewModel(enableUseCase = get(), verifyUseCase = get(), disableUseCase = get()) }
}

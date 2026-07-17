package com.yfy.kmp.shared

import kotlinx.coroutines.flow.StateFlow
import com.yfy.kmp.feature.auth.presentation.EmailVerifyUiState
import com.yfy.kmp.feature.auth.presentation.EmailVerifyViewModel
import com.yfy.kmp.feature.auth.presentation.ForgotPasswordUiState
import com.yfy.kmp.feature.auth.presentation.ForgotPasswordViewModel
import com.yfy.kmp.feature.auth.presentation.LoginUiState
import com.yfy.kmp.feature.auth.presentation.LoginViewModel
import com.yfy.kmp.feature.auth.presentation.SignupUiState
import com.yfy.kmp.feature.auth.presentation.SignupViewModel
import com.yfy.kmp.feature.auth.presentation.ChangePasswordUiState
import com.yfy.kmp.feature.auth.presentation.ChangePasswordViewModel
import com.yfy.kmp.feature.auth.presentation.ResetPasswordUiState
import com.yfy.kmp.feature.auth.presentation.ResetPasswordViewModel
import com.yfy.kmp.feature.auth.presentation.TwoFactorUiState
import com.yfy.kmp.feature.auth.presentation.TwoFactorViewModel
import com.yfy.kmp.feature.onboarding.presentation.OnboardingUiState
import com.yfy.kmp.feature.onboarding.presentation.OnboardingViewModel
import com.yfy.kmp.feature.profile.presentation.ProfileUiState
import com.yfy.kmp.feature.profile.presentation.ProfileViewModel
import com.yfy.kmp.feature.settings.presentation.SettingsUiState
import com.yfy.kmp.feature.settings.presentation.SettingsViewModel
import com.yfy.kmp.feature.settings.presentation.ThemeViewModel
import com.yfy.kmp.feature.settings.presentation.LanguageViewModel
import com.yfy.kmp.feature.paywall.presentation.PaywallUiState
import com.yfy.kmp.feature.paywall.presentation.PaywallViewModel
import com.yfy.kmp.core.datastore.AppThemeMode
import com.yfy.kmp.core.datastore.AppLanguage

// Objective-C export erases the generic argument on BaseViewModel<S, E>, so `state` reaches Swift as
// untyped `id<StateFlow>` and SKIE cannot type it. These concrete-return accessors are what let SKIE
// bridge each flow as SkieSwiftStateFlow<ConcreteUiState>. Do not inline them away.
fun loginState(viewModel: LoginViewModel): StateFlow<LoginUiState> = viewModel.state

fun signupState(viewModel: SignupViewModel): StateFlow<SignupUiState> = viewModel.state

fun forgotPasswordState(viewModel: ForgotPasswordViewModel): StateFlow<ForgotPasswordUiState> = viewModel.state

fun emailVerifyState(viewModel: EmailVerifyViewModel): StateFlow<EmailVerifyUiState> = viewModel.state

fun changePasswordState(viewModel: ChangePasswordViewModel): StateFlow<ChangePasswordUiState> = viewModel.state

fun resetPasswordState(viewModel: ResetPasswordViewModel): StateFlow<ResetPasswordUiState> = viewModel.state

fun twoFactorState(viewModel: TwoFactorViewModel): StateFlow<TwoFactorUiState> = viewModel.state

fun onboardingState(viewModel: OnboardingViewModel): StateFlow<OnboardingUiState> = viewModel.state

fun profileState(viewModel: ProfileViewModel): StateFlow<ProfileUiState> = viewModel.state

fun settingsState(viewModel: SettingsViewModel): StateFlow<SettingsUiState> = viewModel.state

fun themeState(viewModel: ThemeViewModel): StateFlow<AppThemeMode> = viewModel.state

fun languageState(viewModel: LanguageViewModel): StateFlow<AppLanguage> = viewModel.state

fun paywallState(viewModel: PaywallViewModel): StateFlow<PaywallUiState> = viewModel.state

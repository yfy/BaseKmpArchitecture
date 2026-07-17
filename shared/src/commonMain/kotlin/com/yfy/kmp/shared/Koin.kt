package com.yfy.kmp.shared

import com.yfy.kmp.core.ads.AdManager
import com.yfy.kmp.core.ads.di.adsModule
import com.yfy.kmp.core.analytics.AnalyticsTracker
import com.yfy.kmp.core.analytics.di.analyticsModule
import com.yfy.kmp.core.analytics.di.mockAnalyticsModule
import com.yfy.kmp.core.common.auth.di.tokenModule
import com.yfy.kmp.core.common.event.AppEventBus
import com.yfy.kmp.core.common.session.SessionManager
import com.yfy.kmp.core.common.session.di.sessionModule
import com.yfy.kmp.core.common.navigation.NavigationResultBus
import com.yfy.kmp.core.common.navigation.di.navigationModule
import com.yfy.kmp.core.common.permission.PermissionController
import com.yfy.kmp.core.common.permission.di.permissionModule
import com.yfy.kmp.core.database.di.databaseModule
import com.yfy.kmp.core.network.di.connectivityModule
import com.yfy.kmp.core.datastore.di.datastoreModule
import com.yfy.kmp.core.network.di.debugNetworkModule
import com.yfy.kmp.core.network.di.mockNetworkModule
import com.yfy.kmp.core.network.di.prodNetworkModule
import com.yfy.kmp.core.notification.di.notificationModule
import com.yfy.kmp.feature.auth.di.authModule
import com.yfy.kmp.feature.auth.mock.authMockRoutes
import com.yfy.kmp.feature.auth.presentation.ChangePasswordViewModel
import com.yfy.kmp.feature.auth.presentation.EmailVerifyViewModel
import com.yfy.kmp.feature.auth.presentation.ForgotPasswordViewModel
import com.yfy.kmp.feature.auth.presentation.LoginViewModel
import com.yfy.kmp.feature.auth.presentation.ResetPasswordViewModel
import com.yfy.kmp.feature.auth.presentation.SignupViewModel
import com.yfy.kmp.feature.auth.presentation.TwoFactorViewModel
import com.yfy.kmp.feature.onboarding.di.onboardingModule
import com.yfy.kmp.feature.onboarding.domain.OnboardingRepository
import com.yfy.kmp.feature.onboarding.presentation.OnboardingViewModel
import com.yfy.kmp.feature.profile.di.profileModule
import com.yfy.kmp.feature.profile.presentation.ProfileViewModel
import com.yfy.kmp.feature.paywall.di.paywallModule
import com.yfy.kmp.feature.paywall.domain.BillingClient
import com.yfy.kmp.feature.paywall.presentation.PaywallViewModel
import com.yfy.kmp.feature.settings.di.settingsModule
import com.yfy.kmp.feature.settings.presentation.LanguageViewModel
import com.yfy.kmp.feature.settings.presentation.SettingsViewModel
import com.yfy.kmp.feature.settings.presentation.ThemeViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.mp.KoinPlatform

enum class AppEnvironment { MOCK, DEBUG, PROD }

private val featureModules: List<Module>
    get() = listOf(
        datastoreModule, databaseModule, notificationModule, adsModule,
        permissionModule, navigationModule, sessionModule, tokenModule, connectivityModule,
        authModule, onboardingModule, profileModule, settingsModule, paywallModule,
    )

private val allMockRoutes get() = authMockRoutes

fun appModules(environment: AppEnvironment): List<Module> = when (environment) {
    AppEnvironment.MOCK -> listOf(mockNetworkModule(allMockRoutes), mockAnalyticsModule) + featureModules
    AppEnvironment.DEBUG -> listOf(debugNetworkModule, analyticsModule) + featureModules
    AppEnvironment.PROD -> listOf(prodNetworkModule, analyticsModule) + featureModules
}

fun startAppKoin(
    environment: AppEnvironment,
    billingClient: BillingClient? = null,
    adManager: AdManager? = null,
    platformDeclaration: KoinAppDeclaration? = null,
) {
    initLogging()
    startKoin {
        platformDeclaration?.invoke(this)
        modules(appModules(environment))
        // Must load after appModules: these native impls override the mock bindings by load order.
        if (billingClient != null) {
            modules(org.koin.dsl.module { single<BillingClient> { billingClient } })
        }
        if (adManager != null) {
            modules(org.koin.dsl.module { single<AdManager> { adManager } })
        }
    }
}

fun getPermissionController(): PermissionController = KoinPlatform.getKoin().get()
fun getNavigationResultBus(): NavigationResultBus = KoinPlatform.getKoin().get()
fun getSessionManager(): SessionManager = KoinPlatform.getKoin().get()
suspend fun hasActiveSession(): Boolean = KoinPlatform.getKoin().get<SessionManager>().currentUserIdOrNull() != null
fun getAnalyticsTracker(): AnalyticsTracker = KoinPlatform.getKoin().get()
fun getAppEventBus(): AppEventBus = KoinPlatform.getKoin().get()

fun getAdManager(): AdManager = KoinPlatform.getKoin().get()

fun getLoginViewModel(): LoginViewModel = KoinPlatform.getKoin().get()
fun getSignupViewModel(): SignupViewModel = KoinPlatform.getKoin().get()
fun getForgotPasswordViewModel(): ForgotPasswordViewModel = KoinPlatform.getKoin().get()
fun getEmailVerifyViewModel(): EmailVerifyViewModel = KoinPlatform.getKoin().get()
fun getChangePasswordViewModel(): ChangePasswordViewModel = KoinPlatform.getKoin().get()
fun getResetPasswordViewModel(): ResetPasswordViewModel = KoinPlatform.getKoin().get()
fun getTwoFactorViewModel(): TwoFactorViewModel = KoinPlatform.getKoin().get()

fun getOnboardingViewModel(): OnboardingViewModel = KoinPlatform.getKoin().get()
fun getProfileViewModel(): ProfileViewModel = KoinPlatform.getKoin().get()
fun getSettingsViewModel(): SettingsViewModel = KoinPlatform.getKoin().get()
fun getPaywallViewModel(): PaywallViewModel = KoinPlatform.getKoin().get()

fun getThemeViewModel(): ThemeViewModel = KoinPlatform.getKoin().get()

fun getLanguageViewModel(): LanguageViewModel = KoinPlatform.getKoin().get()

suspend fun isOnboardingCompleted(): Boolean = KoinPlatform.getKoin().get<OnboardingRepository>().isCompleted()

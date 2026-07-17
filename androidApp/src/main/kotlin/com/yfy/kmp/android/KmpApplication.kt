package com.yfy.kmp.android

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.yfy.kmp.android.BuildConfig
import com.yfy.kmp.core.ads.AdManager
import com.yfy.kmp.core.ads.di.admobAdsModule
import com.yfy.kmp.core.common.CurrentActivityHolder
import com.yfy.kmp.feature.paywall.di.revenueCatPaywallModule
import com.yfy.kmp.feature.auth.di.googleSocialAuthModule
import com.yfy.kmp.shared.AppEnvironment
import com.yfy.kmp.shared.NativeIntegrations
import com.yfy.kmp.shared.startAppKoin
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext

class KmpApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(ActivityTracker)

        val environment = AppEnvironment.valueOf(BuildConfig.APP_ENVIRONMENT)

        val nativeModules = buildList {
            if (environment == AppEnvironment.PROD) {
                if (NativeIntegrations.revenueCat) add(revenueCatPaywallModule)
                if (NativeIntegrations.googleAuth) add(googleSocialAuthModule)
                if (NativeIntegrations.admob) add(admobAdsModule)
            }
        }

        startAppKoin(environment) {
            androidContext(this@KmpApplication)
            if (nativeModules.isNotEmpty()) modules(nativeModules)
        }

        if (environment == AppEnvironment.PROD && NativeIntegrations.admob) {
            get<AdManager>().initialize { }
        }
    }
}

private object ActivityTracker : Application.ActivityLifecycleCallbacks {
    override fun onActivityResumed(activity: Activity) { CurrentActivityHolder.activity = activity }
    override fun onActivityPaused(activity: Activity) {
        if (CurrentActivityHolder.activity === activity) CurrentActivityHolder.activity = null
    }
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}

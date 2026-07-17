package com.yfy.kmp.core.ads

import android.content.Context
import co.touchlab.kermit.Logger
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.yfy.kmp.core.common.CurrentActivityHolder

internal class AdMobAdManager(private val context: Context) : AdManager {

    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(context)

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    override fun initialize(onComplete: () -> Unit) {
        val activity = CurrentActivityHolder.activity
        if (activity == null) {
            startMobileAds(onComplete)
            return
        }
        val params = ConsentRequestParameters.Builder().build()
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    if (formError != null) {
                        Logger.w { "UMP consent form error: ${formError.message}" }
                    }
                    startMobileAds(onComplete)
                }
            },
            { error ->
                Logger.w { "UMP consent info update failed: ${error.message}" }
                startMobileAds(onComplete)
            },
        )
    }

    private fun startMobileAds(onComplete: () -> Unit) {
        MobileAds.initialize(context) { onComplete() }
    }

    override fun loadInterstitial() {
        InterstitialAd.load(
            context,
            AdMobAndroidConfig.INTERSTITIAL_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Logger.w { "Interstitial load failed: ${error.message}" }
                    interstitialAd = null
                }
            },
        )
    }

    override fun showInterstitial(onClosed: () -> Unit) {
        val ad = interstitialAd
        val activity = CurrentActivityHolder.activity
        if (ad == null || activity == null) {
            onClosed()
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                onClosed()
            }

            override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                interstitialAd = null
                onClosed()
            }
        }
        ad.show(activity)
    }

    override fun loadRewarded() {
        RewardedAd.load(
            context,
            AdMobAndroidConfig.REWARDED_UNIT_ID,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Logger.w { "Rewarded load failed: ${error.message}" }
                    rewardedAd = null
                }
            },
        )
    }

    override fun showRewarded(onReward: (AdReward) -> Unit, onClosed: () -> Unit) {
        val ad = rewardedAd
        val activity = CurrentActivityHolder.activity
        if (ad == null || activity == null) {
            onClosed()
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                onClosed()
            }

            override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                rewardedAd = null
                onClosed()
            }
        }
        ad.show(activity) { rewardItem ->
            onReward(AdReward(type = rewardItem.type, amount = rewardItem.amount))
        }
    }
}

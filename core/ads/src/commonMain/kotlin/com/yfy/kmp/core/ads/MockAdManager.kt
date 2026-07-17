package com.yfy.kmp.core.ads

import co.touchlab.kermit.Logger

internal class MockAdManager : AdManager {

    override fun initialize(onComplete: () -> Unit) {
        Logger.i { "MockAdManager: initialize no-op" }
        onComplete()
    }

    override fun loadInterstitial() {
        Logger.i { "MockAdManager: loadInterstitial no-op" }
    }

    override fun showInterstitial(onClosed: () -> Unit) {
        Logger.i { "MockAdManager: showInterstitial no-op" }
        onClosed()
    }

    override fun loadRewarded() {
        Logger.i { "MockAdManager: loadRewarded no-op" }
    }

    override fun showRewarded(onReward: (AdReward) -> Unit, onClosed: () -> Unit) {
        Logger.i { "MockAdManager: showRewarded no-op" }
        onClosed()
    }
}

package com.yfy.kmp.core.ads

public data class AdReward(val type: String, val amount: Int)

public interface AdManager {
    public fun initialize(onComplete: () -> Unit)
    public fun loadInterstitial()
    public fun showInterstitial(onClosed: () -> Unit)
    public fun loadRewarded()
    public fun showRewarded(onReward: (AdReward) -> Unit, onClosed: () -> Unit)
}

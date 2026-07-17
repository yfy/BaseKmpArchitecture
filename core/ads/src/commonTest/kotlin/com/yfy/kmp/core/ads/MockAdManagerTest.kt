package com.yfy.kmp.core.ads

import kotlin.test.Test
import kotlin.test.assertTrue

class MockAdManagerTest {

    private val adManager: AdManager = MockAdManager()

    @Test
    fun initialize_invokes_completion() {
        var completed = false
        adManager.initialize { completed = true }
        assertTrue(completed)
    }

    @Test
    fun show_interstitial_invokes_closed() {
        var closed = false
        adManager.loadInterstitial()
        adManager.showInterstitial { closed = true }
        assertTrue(closed)
    }

    @Test
    fun show_rewarded_invokes_closed_without_reward() {
        var closed = false
        var rewarded = false
        adManager.loadRewarded()
        adManager.showRewarded(onReward = { rewarded = true }, onClosed = { closed = true })
        assertTrue(closed)
        assertTrue(!rewarded)
    }
}

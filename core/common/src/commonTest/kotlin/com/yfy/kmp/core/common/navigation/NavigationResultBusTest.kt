package com.yfy.kmp.core.common.navigation

import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NavigationResultBusTest {

    @Test
    fun resultsFor_emits_only_matching_key() = runTest {
        val bus = NavigationResultBus()
        val awaited = async { bus.resultsFor("picker.color").first() }
        // The bus has no replay, so the subscriber must attach before post() or the value is lost.
        runCurrent()
        bus.post("other.key", "ignored")
        bus.post("picker.color", "red")
        assertEquals("red", awaited.await())
    }

    @Test
    fun results_carries_key_and_value() = runTest {
        val bus = NavigationResultBus()
        val awaited = async { bus.results.first() }
        runCurrent()
        bus.post("picker.size", "large")
        assertEquals(NavigationResult("picker.size", "large"), awaited.await())
    }
}

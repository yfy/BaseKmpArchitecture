package com.yfy.kmp.feature.paywall

import com.yfy.kmp.feature.paywall.data.MockBillingClient
import com.yfy.kmp.feature.paywall.presentation.PaywallViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PaywallViewModelTest {

    @BeforeTest fun setUp() = Dispatchers.setMain(StandardTestDispatcher())
    @AfterTest fun tearDown() = Dispatchers.resetMain()

    @Test
    fun loads_offerings() = runTest {
        val vm = PaywallViewModel(MockBillingClient())
        advanceUntilIdle()
        assertEquals(2, vm.state.value.products.size)
        assertTrue(!vm.state.value.isLoading)
    }

    @Test
    fun purchase_marks_purchased() = runTest {
        val vm = PaywallViewModel(MockBillingClient())
        advanceUntilIdle()
        vm.purchase("premium_monthly")
        advanceUntilIdle()
        assertTrue(vm.state.value.purchased)
    }
}

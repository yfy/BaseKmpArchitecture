package com.yfy.kmp.feature.onboarding

import com.yfy.kmp.feature.onboarding.domain.OnboardingRepository
import com.yfy.kmp.feature.onboarding.presentation.OnboardingViewModel
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OnboardingViewModelTest {

    private class FakeRepo : OnboardingRepository {
        var completed = false
        override suspend fun isCompleted(): Boolean = completed
        override suspend fun markCompleted() { completed = true }
    }

    @BeforeTest fun setUp() = Dispatchers.setMain(StandardTestDispatcher())
    @AfterTest fun tearDown() = Dispatchers.resetMain()

    @Test
    fun next_advances_pages_then_completes_and_persists() = runTest {
        val repo = FakeRepo()
        val vm = OnboardingViewModel(repo)

        vm.next(); assertEquals(1, vm.state.value.pageIndex)
        vm.next(); assertEquals(2, vm.state.value.pageIndex)
        assertFalse(vm.state.value.completed)
        vm.next()
        advanceUntilIdle()

        assertTrue(vm.state.value.completed)
        assertTrue(repo.completed)
    }

    @Test
    fun skip_completes_immediately() = runTest {
        val repo = FakeRepo()
        val vm = OnboardingViewModel(repo)
        vm.skip()
        advanceUntilIdle()
        assertTrue(vm.state.value.completed)
        assertTrue(repo.completed)
    }
}

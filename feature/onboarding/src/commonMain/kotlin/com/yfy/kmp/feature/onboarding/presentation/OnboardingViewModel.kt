package com.yfy.kmp.feature.onboarding.presentation

import com.yfy.kmp.core.common.base.BaseViewModel
import com.yfy.kmp.core.common.base.simpleLaunch
import com.yfy.kmp.feature.onboarding.domain.OnboardingRepository

public data class OnboardingPage(val title: String, val description: String)

public data class OnboardingUiState(
    val pageIndex: Int = 0,
    val completed: Boolean = false,
)

public class OnboardingViewModel(
    private val repository: OnboardingRepository,
) : BaseViewModel<OnboardingUiState, Nothing>(OnboardingUiState()) {

    public val pageCount: Int = PAGE_COUNT

    public fun next() {
        val index = currentState.pageIndex
        if (index < PAGE_COUNT - 1) {
            setState { copy(pageIndex = index + 1) }
        } else {
            complete()
        }
    }

    public fun skip() {
        complete()
    }

    private fun complete() {
        scope.simpleLaunch {
            repository.markCompleted()
            setState { copy(completed = true) }
        }
    }

    private companion object {
        const val PAGE_COUNT = 3
    }
}

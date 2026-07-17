package com.yfy.kmp.feature.onboarding.data

import com.yfy.kmp.core.datastore.PreferencesStore
import com.yfy.kmp.feature.onboarding.domain.OnboardingRepository

internal class OnboardingRepositoryImpl(
    private val preferences: PreferencesStore,
) : OnboardingRepository {

    override suspend fun isCompleted(): Boolean = preferences.getString(KEY) == "true"

    override suspend fun markCompleted() {
        preferences.putString(KEY, "true")
    }

    private companion object {
        const val KEY = "onboarding_completed"
    }
}

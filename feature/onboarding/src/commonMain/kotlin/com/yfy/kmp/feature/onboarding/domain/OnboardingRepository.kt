package com.yfy.kmp.feature.onboarding.domain

public interface OnboardingRepository {
    public suspend fun isCompleted(): Boolean
    public suspend fun markCompleted()
}

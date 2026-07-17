package com.yfy.kmp.feature.profile.presentation

import com.yfy.kmp.core.common.base.BaseViewModel
import com.yfy.kmp.core.common.base.simpleLaunch
import com.yfy.kmp.core.database.UserCache
import com.yfy.kmp.core.datastore.PreferenceKeys
import com.yfy.kmp.core.datastore.PreferencesStore
import com.yfy.kmp.core.model.AuthUser

public data class ProfileUiState(
    val user: AuthUser? = null,
    val isLoading: Boolean = true,
)

public class ProfileViewModel(
    private val preferences: PreferencesStore,
    private val userCache: UserCache,
) : BaseViewModel<ProfileUiState, Nothing>(ProfileUiState()) {

    init {
        scope.simpleLaunch {
            val userId = preferences.getString(PreferenceKeys.CURRENT_USER_ID)
            val user = userId?.let { userCache.get(it) }
            setState { copy(user = user, isLoading = false) }
        }
    }
}

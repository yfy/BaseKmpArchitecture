package com.yfy.kmp.feature.settings.presentation

import com.yfy.kmp.core.common.base.BaseViewModel
import com.yfy.kmp.core.common.base.simpleLaunch
import com.yfy.kmp.core.datastore.PreferencesStore

public data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val isLoading: Boolean = true,
)

public class SettingsViewModel(
    private val preferences: PreferencesStore,
) : BaseViewModel<SettingsUiState, Nothing>(SettingsUiState()) {

    private var touched = false

    init {
        scope.simpleLaunch {
            val notifications = preferences.getString(KEY_NOTIFICATIONS) != "false"
            setState {
                if (touched) copy(isLoading = false)
                else copy(notificationsEnabled = notifications, isLoading = false)
            }
        }
    }

    public fun setNotificationsEnabled(enabled: Boolean) {
        touched = true
        setState { copy(notificationsEnabled = enabled) }
        scope.simpleLaunch { preferences.putString(KEY_NOTIFICATIONS, enabled.toString()) }
    }

    private companion object {
        const val KEY_NOTIFICATIONS = "settings_notifications"
    }
}

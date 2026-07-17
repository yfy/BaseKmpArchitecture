package com.yfy.kmp.feature.settings.presentation

import com.yfy.kmp.core.common.base.BaseViewModel
import com.yfy.kmp.core.common.base.simpleLaunch
import com.yfy.kmp.core.datastore.AppThemeMode
import com.yfy.kmp.core.datastore.PreferencesStore

public class ThemeViewModel(
    private val preferences: PreferencesStore,
) : BaseViewModel<AppThemeMode, Nothing>(AppThemeMode.SYSTEM) {

    init {
        scope.simpleLaunch {
            preferences.stringFlow(KEY_THEME_MODE).collect { raw ->
                val mode = AppThemeMode.parse(raw)
                setState { mode }
            }
        }
    }

    public fun setMode(mode: AppThemeMode) {
        setState { mode }
        scope.simpleLaunch { preferences.putString(KEY_THEME_MODE, mode.name) }
    }

    private companion object {
        const val KEY_THEME_MODE = "settings_theme_mode"
    }
}

package com.yfy.kmp.feature.settings.presentation

import com.yfy.kmp.core.common.base.BaseViewModel
import com.yfy.kmp.core.common.base.simpleLaunch
import com.yfy.kmp.core.datastore.AppLanguage
import com.yfy.kmp.core.datastore.PreferencesStore

public class LanguageViewModel(
    private val preferences: PreferencesStore,
) : BaseViewModel<AppLanguage, Nothing>(AppLanguage.SYSTEM) {

    init {
        scope.simpleLaunch {
            preferences.stringFlow(KEY_LANGUAGE).collect { raw ->
                val language = AppLanguage.parse(raw)
                setState { language }
            }
        }
    }

    public fun setLanguage(language: AppLanguage) {
        setState { language }
        scope.simpleLaunch { preferences.putString(KEY_LANGUAGE, language.name) }
    }

    private companion object {
        const val KEY_LANGUAGE = "settings_language"
    }
}

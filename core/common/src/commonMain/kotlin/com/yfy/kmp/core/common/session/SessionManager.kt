package com.yfy.kmp.core.common.session

import com.yfy.kmp.core.analytics.AnalyticsTracker
import com.yfy.kmp.core.common.auth.TokenStore
import com.yfy.kmp.core.common.event.AppEvent
import com.yfy.kmp.core.common.event.AppEventBus
import com.yfy.kmp.core.database.UserCache
import com.yfy.kmp.core.datastore.PreferenceKeys
import com.yfy.kmp.core.datastore.PreferencesStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

public class SessionManager(
    private val preferences: PreferencesStore,
    private val userCache: UserCache,
    private val tokenStore: TokenStore,
    private val analytics: AnalyticsTracker,
    private val eventBus: AppEventBus,
) {
    public val currentUserId: Flow<String?> =
        preferences.stringFlow(PreferenceKeys.CURRENT_USER_ID).map { it?.takeIf(String::isNotBlank) }

    public val isLoggedIn: Flow<Boolean> = currentUserId.map { it != null }

    public suspend fun currentUserIdOrNull(): String? =
        preferences.getString(PreferenceKeys.CURRENT_USER_ID)?.takeIf(String::isNotBlank)

    public suspend fun setLoggedIn(userId: String) {
        preferences.putString(PreferenceKeys.CURRENT_USER_ID, userId)
    }

    public suspend fun logout(reason: AppEvent = AppEvent.LoggedOut) {
        val userId = currentUserIdOrNull()
        preferences.remove(PreferenceKeys.CURRENT_USER_ID)
        if (userId != null) userCache.delete(userId)
        tokenStore.clear()
        analytics.setUserId(null)
        analytics.reset()
        eventBus.emit(reason)
    }
}

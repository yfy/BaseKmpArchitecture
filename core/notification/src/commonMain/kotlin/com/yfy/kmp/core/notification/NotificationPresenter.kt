package com.yfy.kmp.core.notification

import com.yfy.kmp.core.model.AppRoute

public interface NotificationPresenter {
    public suspend fun hasPermission(): Boolean
    public suspend fun show(id: Int, title: String, body: String, route: AppRoute? = null)
}

public const val NOTIFICATION_ROUTE_KEY: String = "app_route"

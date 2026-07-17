package com.yfy.kmp.core.notification

import com.yfy.kmp.core.model.AppRoute
import com.yfy.kmp.core.model.toUri
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class IosNotificationPresenter : NotificationPresenter {

    private val center: UNUserNotificationCenter
        get() = UNUserNotificationCenter.currentNotificationCenter()

    override suspend fun hasPermission(): Boolean = suspendCoroutine { continuation ->
        center.getNotificationSettingsWithCompletionHandler { settings ->
            continuation.resume(settings?.authorizationStatus == UNAuthorizationStatusAuthorized)
        }
    }

    override suspend fun show(id: Int, title: String, body: String, route: AppRoute?) {
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(body)
            route?.let { setUserInfo(mapOf(NOTIFICATION_ROUTE_KEY to it.toUri())) }
        }
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = id.toString(),
            content = content,
            trigger = null,
        )
        suspendCoroutine { continuation ->
            center.addNotificationRequest(request) { _ ->
                continuation.resume(Unit)
            }
        }
    }
}

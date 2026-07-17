package com.yfy.kmp.core.common.permission

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusDenied
import platform.UserNotifications.UNAuthorizationStatusNotDetermined
import platform.UserNotifications.UNAuthorizationStatusProvisional
import platform.UserNotifications.UNUserNotificationCenter

internal class IosPermissionController : PermissionController {

    private val center get() = UNUserNotificationCenter.currentNotificationCenter()

    override suspend fun status(permission: AppPermission): PermissionStatus = when (permission) {
        AppPermission.NOTIFICATIONS -> suspendCoroutine { continuation ->
            center.getNotificationSettingsWithCompletionHandler { settings ->
                continuation.resume(
                    when (settings?.authorizationStatus) {
                        UNAuthorizationStatusAuthorized, UNAuthorizationStatusProvisional -> PermissionStatus.GRANTED
                        UNAuthorizationStatusDenied -> PermissionStatus.PERMANENTLY_DENIED
                        UNAuthorizationStatusNotDetermined -> PermissionStatus.NOT_DETERMINED
                        else -> PermissionStatus.NOT_DETERMINED
                    },
                )
            }
        }
    }

    override suspend fun request(permission: AppPermission): PermissionStatus = when (permission) {
        AppPermission.NOTIFICATIONS -> suspendCoroutine { continuation ->
            val options = UNAuthorizationOptionAlert or UNAuthorizationOptionBadge or UNAuthorizationOptionSound
            center.requestAuthorizationWithOptions(options) { isGranted, _ ->
                continuation.resume(if (isGranted) PermissionStatus.GRANTED else PermissionStatus.PERMANENTLY_DENIED)
            }
        }
    }
}

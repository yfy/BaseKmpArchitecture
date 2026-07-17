package com.yfy.kmp.core.common.permission

public enum class AppPermission {
    NOTIFICATIONS,
}

public enum class PermissionStatus {
    GRANTED,

    DENIED,

    PERMANENTLY_DENIED,

    NOT_DETERMINED,
}

public interface PermissionController {
    public suspend fun status(permission: AppPermission): PermissionStatus

    public suspend fun request(permission: AppPermission): PermissionStatus
}

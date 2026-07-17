package com.yfy.kmp.core.common.permission

public interface AndroidPermissionRequester {
    public var requestLauncher: ((String) -> Unit)?

    public fun onResult(isGranted: Boolean)
}

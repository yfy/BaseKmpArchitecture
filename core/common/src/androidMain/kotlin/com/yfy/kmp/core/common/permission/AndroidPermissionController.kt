package com.yfy.kmp.core.common.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yfy.kmp.core.common.CurrentActivityHolder
import kotlinx.coroutines.CompletableDeferred

internal class AndroidPermissionController(
    private val context: Context,
) : PermissionController, AndroidPermissionRequester {

    private var pending: CompletableDeferred<Boolean>? = null

    // Registered by MainActivity because a launcher must be created before the Activity starts.
    override var requestLauncher: ((String) -> Unit)? = null

    override suspend fun status(permission: AppPermission): PermissionStatus = when (permission) {
        AppPermission.NOTIFICATIONS ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                PermissionStatus.GRANTED
            } else {
                granted(Manifest.permission.POST_NOTIFICATIONS)
            }
    }

    override suspend fun request(permission: AppPermission): PermissionStatus {
        if (status(permission) == PermissionStatus.GRANTED) return PermissionStatus.GRANTED
        val launcher = requestLauncher ?: return status(permission)
        val deferred = CompletableDeferred<Boolean>()
        pending = deferred
        launcher(permission.androidName)
        val isGranted = deferred.await()
        return if (isGranted) PermissionStatus.GRANTED else deniedStatus(permission)
    }

    override fun onResult(isGranted: Boolean) {
        pending?.complete(isGranted)
        pending = null
    }

    private fun granted(androidPermission: String): PermissionStatus =
        if (ContextCompat.checkSelfPermission(context, androidPermission) == PackageManager.PERMISSION_GRANTED) {
            PermissionStatus.GRANTED
        } else {
            PermissionStatus.DENIED
        }

    private fun deniedStatus(permission: AppPermission): PermissionStatus {
        val activity = CurrentActivityHolder.activity ?: return PermissionStatus.DENIED
        return if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission.androidName)) {
            PermissionStatus.DENIED
        } else {
            PermissionStatus.PERMANENTLY_DENIED
        }
    }
}

private val AppPermission.androidName: String
    get() = when (this) {
        AppPermission.NOTIFICATIONS -> Manifest.permission.POST_NOTIFICATIONS
    }

package com.yfy.kmp.core.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.yfy.kmp.core.model.AppRoute
import com.yfy.kmp.core.model.toUri

internal class AndroidNotificationPresenter(
    private val context: Context,
) : NotificationPresenter {

    override suspend fun hasPermission(): Boolean =
        Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED

    override suspend fun show(id: Int, title: String, body: String, route: AppRoute?) {
        if (!hasPermission()) return

        val manager = NotificationManagerCompat.from(context)
        manager.createNotificationChannel(
            NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
                .setName(CHANNEL_NAME)
                .build(),
        )

        val contentIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?.apply { route?.let { putExtra(NOTIFICATION_ROUTE_KEY, it.toUri()) } }
            ?.let {
                PendingIntent.getActivity(
                    context,
                    id,
                    it,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                )
            }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .apply { contentIntent?.let { setContentIntent(it) } }
            .build()

        try {
            manager.notify(id, notification)
        } catch (_: SecurityException) {
        }
    }

    private companion object {
        // TODO(template): replace before release — notification channel id.
        const val CHANNEL_ID = "yfy_default"
        const val CHANNEL_NAME = "General"
    }
}

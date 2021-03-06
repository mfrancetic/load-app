package com.udacity.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.DetailActivity
import com.udacity.R

private const val NOTIFICATION_ID = 0
private const val REQUEST_CODE = 0

const val CHANNEL_ID = "channelId"
const val CHANNEL_NAME = "channelName"
const val FILE_NAME = "fileName"
const val STATUS = "status"

enum class DownloadStatus(val status: String) {
    SUCCESS("Success"),
    FAILURE("Fail")
}

fun NotificationManager.sendNotification(
        applicationContext: Context, fileName: String,
        status: String
) {
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra(FILE_NAME, fileName)
    contentIntent.putExtra(STATUS, status)

    val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            REQUEST_CODE,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
    )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(
                    applicationContext.getString(R.string.notification_title)
            )
            .setContentText(applicationContext.getString(R.string.notification_description))
            .setContentIntent(contentPendingIntent)
            .addAction(
                    R.mipmap.ic_launcher,
                    applicationContext.getString(R.string.notification_button),
                    contentPendingIntent
            )
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    notify(NOTIFICATION_ID, builder.build())
}

fun cancelAllNotifications(context: Context) {
    val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
    ) as NotificationManager
    notificationManager.cancelAll()
}
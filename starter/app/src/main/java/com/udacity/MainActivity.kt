package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            setButtonState(ButtonState.Loading)
            download()
        }
        context = custom_button.context

        createChannel()
    }

    private fun setButtonState(buttonState: ButtonState) {
        custom_button.setNewButtonState(buttonState)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            var status = DownloadStatus.FAILURE
            if (id == downloadID && intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val query = DownloadManager.Query()
                query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0))
                val manager = context!!.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor: Cursor = manager.query(query)
                if (cursor.moveToFirst()) {
                    if (cursor.count > 0) {
                        val statusInt: Int =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                        if (statusInt == DownloadManager.STATUS_SUCCESSFUL) {
                            status = DownloadStatus.SUCCESS
                        }
                    }
                }
            }
            setButtonState(ButtonState.Completed)
            sendNotification(getFileName(), status.status)
        }
    }

    private fun sendNotification(fileName: String, status: String) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification(context, fileName, status)
    }

    private fun download() {

        val url = getUrl()
        if (url.isEmpty()) {
            Toast.makeText(this, getString(R.string.select_file_for_download), Toast.LENGTH_LONG)
                .show()
            return
        }

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun getUrl(): String {
        return when (download_radio_group.checkedRadioButtonId) {
            R.id.glide_radio_button -> "https://github.com/bumptech/glide/archive/master.zip"
            R.id.loadapp_radio_button -> "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
            R.id.retrofit_radio_button -> "https://github.com/square/retrofit/archive/master.zip"
            else -> ""
        }
    }

    private fun getFileName(): String {
        return when (download_radio_group.checkedRadioButtonId) {
            R.id.glide_radio_button -> getString(R.string.glide_radio_button)
            R.id.loadapp_radio_button -> getString(R.string.loadapp_radio_button)
            R.id.retrofit_radio_button -> getString(R.string.retrofit_radio_button)
            else -> ""
        }
    }

    private fun createChannel() {
        // Channels are available from api level 26.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = resources.getColor(R.color.colorPrimary)
            notificationChannel.enableVibration(false)
            notificationChannel.description = getString(R.string.download_complete)

            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
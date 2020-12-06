package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.utils.CHANNEL_ID
import com.udacity.utils.CHANNEL_NAME
import com.udacity.utils.DownloadStatus
import com.udacity.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        context = custom_button.context

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        createNotificationChannel()

        custom_button.setOnClickListener {
            setButtonState(ButtonState.Loading)
            download()
        }
    }

    private fun setButtonState(buttonState: ButtonState) {
        custom_button.setNewButtonState(buttonState)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            var status = DownloadStatus.FAILURE

            if (isDownloadCompleted(intent)) {
                status = getDownloadStatus()
            }
            setButtonState(ButtonState.Completed)
            sendNotification(getFileName(), status.status)
        }
    }

    private fun isDownloadCompleted(intent: Intent?): Boolean {
        val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        return id == downloadID && intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE
    }

    private fun getDownloadStatus(): DownloadStatus {
        val status = DownloadStatus.FAILURE
        val query = DownloadManager.Query()

        query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0))

        val manager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val cursor: Cursor = manager.query(query)

        if (cursor.moveToFirst()) {
            if (cursor.count > 0) {
                val statusInt: Int =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                if (statusInt == DownloadManager.STATUS_SUCCESSFUL) {
                    return DownloadStatus.SUCCESS
                }
            }
        }
        return status
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
            displayToast()
            Handler(Looper.getMainLooper()).postDelayed({
                setButtonState(ButtonState.Completed)
            }, ANIMATION_DURATION)
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
        downloadID = downloadManager.enqueue(request)
    }

    private fun displayToast() {
        Toast.makeText(this, getString(R.string.select_file_for_download), Toast.LENGTH_LONG).show()
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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = getColor(R.color.colorPrimary)
            notificationChannel.enableVibration(false)
            notificationChannel.description = getString(R.string.download_complete)

            val notificationManager = context.getSystemService(
                    NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
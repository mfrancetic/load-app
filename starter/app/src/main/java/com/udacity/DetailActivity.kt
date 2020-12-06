package com.udacity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.utils.FILE_NAME
import com.udacity.utils.STATUS
import com.udacity.utils.cancelAllNotifications
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        cancelAllNotifications(this)
        setFileNameAndStatus()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        ok_button.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setFileNameAndStatus() {
        val fileName = intent.getStringExtra(FILE_NAME)
        val statusText = intent.getStringExtra(STATUS)

        file_name.text = fileName
        status.text = statusText

        if (statusText.equals(getString(R.string.success))) {
            status.setTextColor(Color.BLACK)
        } else {
            status.setTextColor(Color.RED)
        }
    }
}
package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        setFileNameAndStatus()
    }

    private fun setFileNameAndStatus() {
        val fileName = intent.getStringExtra(FILE_NAME)
        val statusText = intent.getStringExtra(STATUS)

        file_name?.text = fileName
        status?.text = statusText
    }
}
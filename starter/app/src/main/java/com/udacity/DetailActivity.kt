package com.udacity

import android.graphics.Color
import android.graphics.Color.red
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        setFileNameAndStatus()
        setOnClickListener()
        startTransition()
//        overridePendingTransition(0, 0)
    }

    private fun startTransition() {
        detail_motion_layout.setTransition(R.id.start, R.id.end)
        detail_motion_layout.setTransitionDuration(3000)
        detail_motion_layout.transitionToEnd()
        detail_motion_layout.setTransitionListener(
            object : MotionLayout.TransitionListener {
                override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                    println("Transition started")
                }

                override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                    println("Transition changed")

                }

                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                    println("Transition completed")

                }

                override fun onTransitionTrigger(
                    p0: MotionLayout?,
                    p1: Int,
                    p2: Boolean,
                    p3: Float
                ) {
                    println("Transition triggered")

                }
            }
        )
    }

    private fun setOnClickListener() {
        ok_button.setOnClickListener {
            detail_motion_layout.transitionToStart()
//            onBackPressed()
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
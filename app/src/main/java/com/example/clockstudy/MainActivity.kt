package com.example.clockstudy

import android.R.attr.button
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.PI
import kotlin.math.atan2


class MainActivity : AppCompatActivity(), ClockText {
    private var clock : Clock = Clock()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        clock.setCallback(this)
    }

    override fun onStart() {
        super.onStart()
        val clockBoard = findViewById<ClockBoard>(R.id.clockBoard)
        clockBoard.setClock(clock)

        val clearButton: Button = findViewById(R.id.button)
        clearButton.setOnClickListener {
            Log.d("BUTTON", "clear pushed")
            clock.clear()
            updateClockText()
            clockBoard.invalidate()
        }
        val currentTimeButton: Button = findViewById(R.id.button2)
        currentTimeButton.setOnClickListener {
            Log.d("BUTTON", "current pushed")
            clock.setToCurrentTime()
            // ここでneedleのrotateとtextupdateが必要
            updateClockText()
            clockBoard.invalidate()
        }

        val observer: ViewTreeObserver = clockBoard.getViewTreeObserver()
        observer.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                clockBoard.init()
            }
        })
    }


    override fun updateClockText() {
        val textView = findViewById<TextView>(R.id.textView)
        if (textView != null) {
            var hourStr : String = clock.getHour().toString()
            if(clock.getHour() < 10) {
                hourStr = "0" + hourStr
            }
            var minStr : String = clock.getMinute().toString()
            if(clock.getMinute() < 10) {
                minStr = "0" + minStr
            }
            textView.setText(hourStr + "時" + minStr + "分")
        }
    }
}
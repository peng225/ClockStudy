package com.example.clockstudy

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.PI
import kotlin.math.atan2

enum class NeedleType {
    LONG_NEEDLE,
    SHORT_NEEDLE
}

class Clock {
    private var longNeedle : ClockNeedle = ClockNeedle()
    private var shortNeedle : ClockNeedle = ClockNeedle()
    private var ct : ClockText? = null

    fun init(longNeedleLength : Int, shortNeedleLength : Int,
             centerX : Float, centerY : Float) {
        longNeedle.init(longNeedleLength, centerX, centerY)
        shortNeedle.init(shortNeedleLength, centerX, centerY)
    }

    fun setCallback(ct : ClockText) {
        this.ct = ct
    }

    fun rotate() {
        longNeedle.rotate(getLongNeedleRotateAngle())
        shortNeedle.rotate(getShortNeedleRotateAngle())
    }

    fun updateClock(
        x: Float, y: Float, centerX: Float, centerY: Float): Boolean {
        val dx = x - centerX
        val dy = y - centerY
        var angle: Float = atan2(dy, dx)
        angle += PI.toFloat() / 2f
        angle = angle * 180f / PI.toFloat()
        val newMinute = ((angle / 6f).toInt() + 60) % 60
        if (getMinute() != newMinute) {
            var diffMinute: Int = newMinute - getMinute()
            if (diffMinute > 30) {
                diffMinute -= 60
            } else if (diffMinute < -30) {
                diffMinute += 60
            }
            Log.d("TIMEDIFF", diffMinute.toString())
            addMinute(diffMinute)

            ct?.updateClockText()

            return true
        }
        return false
    }

    fun longNeedleTouch(x : Float, y : Float) : Boolean {
        return longNeedle.touch(x, y)
    }

    fun getHeadPoint(type : NeedleType) : FloatArray {
        if(type == NeedleType.LONG_NEEDLE) {
            return longNeedle.getHeadPoint()
        }
        return shortNeedle.getHeadPoint()
    }

    fun getMinute(): Int {
        return minute
    }
    fun getHour(): Int {
        return hour
    }

    fun addMinute(addedMin : Int) {
        if (addedMin in -59..59) {
            if(this.minute + addedMin >= 60) {
                this.hour = (this.hour + 1) % 12
            } else if(this.minute + addedMin < 0) {
                this.hour = (this.hour - 1 + 12) % 12
            }
            this.minute = (this.minute + addedMin + 60) % 60
        }
        rotate()
    }

    fun clear() {
        minute = 0
        hour = 0
        rotate()
    }

    fun setToCurrentTime() {
        val time = LocalTime.now().plusHours(9)
        minute = time.minute
        hour = time.hour % 12
        rotate()
    }

    private var minute : Int = 0
    private var hour : Int = 0

    private fun getLongNeedleRotateAngle() : Float {
        val minute = getMinute()
        return 6f * minute.toFloat()
    }

    private fun getShortNeedleRotateAngle() : Float {
        val minute = getMinute()
        val hour = getHour()
        return 0.5f * minute.toFloat() + 30f * hour.toFloat()
    }
}
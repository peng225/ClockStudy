package com.example.clockstudy

import android.graphics.Matrix
import android.util.Log
import kotlin.math.*

class ClockNeedle {
    private var length : Int = 0
    private var center : FloatArray = FloatArray(2)
    private var head : FloatArray = FloatArray(2)

    fun init(length : Int, x : Float, y : Float) {
        this.length = length
        center[0] = x
        center[1] = y
        head[0] = center[0]
        head[1] = center[1] - this.length.toFloat()
    }

    fun rotate(angle : Float) {
        val mat = Matrix()
        mat.setRotate(angle, center[0], center[1])
        head[0] = center[0]
        head[1] = center[1] - this.length.toFloat()
        mat.mapPoints(head)
    }

    fun getHeadPoint() : FloatArray {
        return head
    }

    fun touch(x : Float, y : Float) : Boolean {
        var dx = x - center[0]
        var dy = y - center[1]
        val touchLength : Float = sqrt(dx*dx + dy*dy)
        if(touchLength < length / 10 || touchLength > length) {
            Log.d("Needle", "touchLength = " + touchLength)
            return false
        }
        val inputAngle : Float = atan2(dy, dx)

        dx = head[0] - center[0]
        dy = head[1] - center[1]
        val needleAngle : Float = atan2(dy, dx)

//        Log.d("Needle", "inputAngle = " + inputAngle + ", needleAngle = " + needleAngle)
        val angleDiff = abs(inputAngle - needleAngle)
        return angleDiff < PI * 5f / 180f
    }
}
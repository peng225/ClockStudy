package com.example.clockstudy

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.DragEvent.ACTION_DRAG_STARTED
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.core.graphics.transform
import androidx.core.view.MotionEventCompat
import kotlin.math.PI
import kotlin.math.atan2

class ClockBoard : View {
    private var clock : Clock = Clock()
    private var clockBoardPaint : Paint = Paint()
    private var clockNumberPaint : Paint = Paint()
    private var longNeedlePaint : Paint = Paint()
    private var shortNeedlePaint : Paint = Paint()
    private var cx : Int = 0
    private var cy : Int = 0
    private var radius : Int = 0
    private var grasp : Boolean = false
    private var initialized : Boolean = false

    init {
        clockBoardPaint.color = Color.argb(255, 0, 0, 0)
        clockBoardPaint.strokeWidth = 10f
        clockBoardPaint.style = Paint.Style.STROKE

        clockNumberPaint.color = Color.argb(255, 0, 0, 0)
        clockNumberPaint.textSize = 60f

        longNeedlePaint.color = Color.argb(255, 255, 0, 0)
        longNeedlePaint.strokeWidth = 40f
        longNeedlePaint.style = Paint.Style.STROKE

        shortNeedlePaint.color = Color.argb(255, 0, 0, 255)
        shortNeedlePaint.strokeWidth = 60f
        shortNeedlePaint.style = Paint.Style.STROKE
    }

    constructor(context : Context) : super(context) {
    }

    constructor(context : Context, attrs : AttributeSet) : super(context, attrs) {
    }

    fun setClock(clock : Clock) {
        this.clock = clock
    }

    fun init() {
        if(!initialized) {
            // Set figure property
            this.cx = this.width / 2
            this.cy = this.height / 2
            this.radius = minOf(this.width, this.height) * 2 / 5
            Log.e("INIT", this.cx.toString() + ", " + this.cy.toString())

            // Set clock needles
            clock.init(radius * 9 / 10, radius * 5 / 10,
                cx.toFloat(), cy.toFloat())
            initialized = true
        }
    }


    private val DEBUG_COORD = "Coordinate"
    private val DEBUG_GESTURE = "Gestures"
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.getAction()) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(DEBUG_GESTURE, "Action was DOWN")
                if(clock.longNeedleTouch(event.getX(), event.getY())) {
                    Log.d(DEBUG_GESTURE, "Grasped")
                    grasp = true
                }
                true
            }
            MotionEvent.ACTION_MOVE -> {
                if(grasp) {
                    if(clock.updateClock(
                            event.getX(), event.getY(),
                            cx.toFloat(), cy.toFloat()
                    )) {
//                        Log.e("CALL CALLBACK", "invalidate is called")
                        invalidate()
                    }
                }
                true
            }
            MotionEvent.ACTION_UP -> {
                grasp = false
                true
            }
            MotionEvent.ACTION_CANCEL -> {
                grasp = false
                true
            }
            MotionEvent.ACTION_OUTSIDE -> {
                grasp = false
                true
            }
            else -> {
                grasp = false
                super.onTouchEvent(event)
            }
        }
    }

    override fun onDraw(canvas : Canvas){
//        Log.d("DRAW", "onDraw called")
        drawClockBoard(canvas)
        drawNeedles(canvas)
    }

    private fun drawClockBoard(canvas : Canvas) {
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), radius.toFloat(), clockBoardPaint)
        drawNumbers(canvas)
        drawTickLines(canvas)
    }

    private fun drawNumbers(canvas : Canvas) {
        val mat = Matrix()
        for(i in 1..12) {
            val point = FloatArray(2)
            point[0] = cx.toFloat()
            point[1] = cy - radius.toFloat() * 0.88f
            mat.setRotate((30 * i).toFloat(), cx.toFloat(), cy.toFloat())
            mat.mapPoints(point)
            point[0] -= 30f
            point[1] += 30f
//            Log.d(DEBUG_COORD, "point " + i + ": " + point[0] + ", " + point[1])
            canvas.drawText(i.toString(), point[0], point[1], clockNumberPaint)
        }
    }

    private fun drawTickLines(canvas : Canvas) {
        val mat = Matrix()
        for(i in 1..60) {
            val point = FloatArray(2)
            point[0] = cx.toFloat()
            point[1] = cy - radius.toFloat()
            val point2 = FloatArray(2)
            point2[0] = cx.toFloat()
            if(i % 5 == 0) {
                point2[1] = cy - radius.toFloat() * 0.90f
            } else {
                point2[1] = cy - radius.toFloat() * 0.95f
            }

            mat.setRotate((6 * i).toFloat(), cx.toFloat(), cy.toFloat())
            mat.mapPoints(point)
            mat.mapPoints((point2))
            canvas.drawLine(point[0], point[1], point2[0], point2[1], clockBoardPaint)
        }
    }

    private fun drawNeedles(canvas : Canvas) {
        val longNeedleHeadPoint = clock.getHeadPoint(NeedleType.LONG_NEEDLE)
        canvas.drawLine(this.cx.toFloat(), this.cy.toFloat(),
                        longNeedleHeadPoint[0], longNeedleHeadPoint[1],
                        longNeedlePaint)

        val shortNeedleleHeadPoint = clock.getHeadPoint(NeedleType.SHORT_NEEDLE)
        canvas.drawLine(this.cx.toFloat(), this.cy.toFloat(),
                        shortNeedleleHeadPoint[0], shortNeedleleHeadPoint[1],
                        shortNeedlePaint)
    }
}
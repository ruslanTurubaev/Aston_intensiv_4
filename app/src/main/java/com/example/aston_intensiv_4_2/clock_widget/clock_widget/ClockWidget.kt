package com.example.aston_intensiv_4_2.clock_widget.clock_widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.aston_intensiv_4_2.R
import com.example.aston_intensiv_4_2.clock_widget.interfaces.Populatable
import com.example.aston_intensiv_4_2.clock_widget.support_class.ColorResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

private const val SECONDS_ANGLE_DELTA : Float = 6f
private const val MINUTES_ANGLE_DELTA : Float = 0.1f
private const val HOURS_ANGLE_DELTA : Float = 0.0083f

class ClockWidget @JvmOverloads constructor(
    context : Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0)
    : View(context,attributeSet, defStyleAttr), Populatable<ClockWidgetViewModel> {

    private var seconds : Int = 0
    private var minutes : Int = 0
    private var hours : Int = 0

    private var centerX : Float = 0f
    private var centerY : Float = 0f

    private var radiusLarge : Float = 0f
    private var radiusSmall : Float = 0f

    private var secondsPointerRadius : Float = 0f
    private var minutesPointerRadius : Float = 0f
    private var hoursPointerRadius : Float = 0f

    private var customSecondsPointerRadius : Float = 0f
    private var customMinutesPointerRadius : Float = 0f
    private var customHoursPointerRadius : Float = 0f

    private var secondsPointerColor : Int = 0
    private var minutesPointerColor : Int = 0
    private var hoursPointerColor : Int = 0

    private val paint : Paint = Paint()

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ClockWidget)

        customSecondsPointerRadius = typedArray.getFloat(R.styleable.ClockWidget_secondsPointerSize, 0f)
        customMinutesPointerRadius = typedArray.getFloat(R.styleable.ClockWidget_minutesPointerSize, 0f)
        customHoursPointerRadius = typedArray.getFloat(R.styleable.ClockWidget_hoursPointerSize, 0f)

        secondsPointerColor = typedArray.getColor(R.styleable.ClockWidget_secondsPointerColor, 0)
        minutesPointerColor = typedArray.getColor(R.styleable.ClockWidget_minutesPointerColor, 0)
        hoursPointerColor = typedArray.getColor(R.styleable.ClockWidget_hoursPointerColor, 0)

        typedArray.recycle()
    }

    override fun populate(model: ClockWidgetViewModel) {
        setSecondsPointerSize(model.secondsPointerRadius)
        setMinutesPointerSize(model.minutesPointerRadius)
        setHoursPointerSize(model.hoursPointerRadius)

        setSecondsPointerColor(model.secondsPointerColor)
        setMinutesPointerColor(model.minutesPointerColor)
        setHoursPointerColor(model.hoursPointerColor)
    }

    fun setSecondsPointerSize(size : Float){
        customSecondsPointerRadius = size
        if(size > 0 && size < radiusSmall) {
            secondsPointerRadius = size
        }
    }

    fun setMinutesPointerSize(size : Float){
        customMinutesPointerRadius = size
        if(size > 0 && size < radiusSmall) {
            minutesPointerRadius = size
        }
    }

    fun setHoursPointerSize(size : Float){
        customHoursPointerRadius = size
        if(size > 0 && size < radiusSmall){
            hoursPointerRadius = size
        }
    }

    fun setSecondsPointerColor(colorRes : ColorResource){
        secondsPointerColor = colorRes.getColor(context)
    }

    fun setMinutesPointerColor(colorRes : ColorResource){
        minutesPointerColor = colorRes.getColor(context)
    }

    fun setHoursPointerColor(colorRes : ColorResource){
        hoursPointerColor = colorRes.getColor(context)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        centerX = (w / 2).toFloat()
        centerY = (h / 2).toFloat()

        radiusLarge = centerX.coerceAtMost(centerY) * 0.9f
        radiusSmall = 0.9f * radiusLarge

        secondsPointerRadius = if(customSecondsPointerRadius != 0f){
            minOf(customSecondsPointerRadius, radiusSmall)
        }
        else{
            0.85f * radiusLarge
        }

        minutesPointerRadius = if(customMinutesPointerRadius != 0f){
            minOf(customMinutesPointerRadius, radiusSmall)
        }
        else{
            0.7f * radiusLarge
        }

        hoursPointerRadius = if(customHoursPointerRadius != 0f){
            minOf(customHoursPointerRadius, radiusSmall)
        }
        else{
            0.5f * radiusLarge
        }

        startClock()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.isAntiAlias = true

        paint.color = context.getColor(R.color.gray)
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX,centerY, radiusLarge, paint)

        paint.color = context.getColor(R.color.black)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 50f
        canvas.drawCircle(centerX,centerY, radiusLarge, paint)

        var angle = 0f

        for(i in 1..12){
            val angleRad = (angle * Math.PI / 180f).toFloat()
            val startX = centerX + radiusLarge * cos(angleRad)
            val startY = centerY + radiusLarge * sin(angleRad)

            val stopX = centerX + radiusSmall * cos(angleRad)
            val stopY = centerY + radiusSmall * sin(angleRad)

            canvas.drawLine(startX, startY, stopX, stopY, paint)

            angle += 30f
        }

        val secondsAngleRad = getPointerAngleRad(seconds, SECONDS_ANGLE_DELTA)
        val minutesAngleRad = getPointerAngleRad(minutes, MINUTES_ANGLE_DELTA)
        val hoursAngleRad = getPointerAngleRad(hours, HOURS_ANGLE_DELTA)

        drawPointer(canvas, hoursPointerColor, 20f, hoursPointerRadius, hoursAngleRad)
        drawPointer(canvas, minutesPointerColor, 10f, minutesPointerRadius, minutesAngleRad)
        drawPointer(canvas, secondsPointerColor, 5f, secondsPointerRadius, secondsAngleRad)
    }

    private fun startClock(){
        val clockWidget = this

        CoroutineScope(Dispatchers.Default).launch {
            while (clockWidget.isAttachedToWindow){
                getCurrentTime()
                clockWidget.invalidate()

                delay(1000)
            }
        }
    }

    private fun getCurrentTime(){
        val currentTime = System.currentTimeMillis()
        val totalSeconds = currentTime / 1000

        seconds = (totalSeconds % 60).toInt()
        minutes = (totalSeconds % 3600).toInt()
        hours = (totalSeconds % 43200 + 10800).toInt()
    }

    private fun getPointerAngleRad(timeUnit: Int, angleDelta: Float): Float {
        val angle = 270 + timeUnit * angleDelta
        return (angle * Math.PI / 180f).toFloat()
    }

    private fun drawPointer(canvas : Canvas,
                            colorRes : Int,
                            strokeWidth : Float,
                            pointerRadius : Float,
                            angleRad : Float){

        paint.color = if(colorRes == 0){
            context.getColor(R.color.black)
        }
        else{
            colorRes
        }

        paint.strokeWidth = strokeWidth

        val stopX = centerX + pointerRadius * cos(angleRad)
        val stopY = centerY + pointerRadius * sin(angleRad)
        canvas.drawLine(centerX, centerY, stopX, stopY, paint)
    }
}
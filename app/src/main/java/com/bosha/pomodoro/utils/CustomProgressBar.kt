package com.bosha.pomodoro.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import com.bosha.pomodoro.R

class CustomProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.customProgressButtonStyle
) : View(context, attrs, defStyleAttr) {

    private var periodMs = 0L
    private var currentMs = 0L
    private var color = 0
    private var colorBackground = 0
    private var style = FILL
    private val paint = Paint()
    private val paintBack = Paint()



    init {
        if (attrs != null) {
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CustomProgressBar,
                defStyleAttr,
                0
            ).use {
                color = it.getColor(R.styleable.CustomProgressBar_customColor,  ContextCompat.getColor(context, R.color.red))
                style = it.getInt(R.styleable.CustomProgressBar_customStyle, 0)
                colorBackground = it.getColor(R.styleable.CustomProgressBar_circleBackground, ContextCompat.getColor(context, R.color.red) )
            }
        }

        paint.color = color
        paint.style = if (style == FILL) Paint.Style.FILL else Paint.Style.STROKE
        paint.strokeWidth = 5F
        paint.isAntiAlias = true

        paintBack.color = colorBackground
        paintBack.style = Paint.Style.FILL
        paintBack.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (periodMs == currentMs || periodMs == 0L || currentMs <= 0L) return



        val startAngel = (((currentMs % periodMs).toFloat() / periodMs) * 360)

        canvas.drawOval(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            paintBack

        )

        canvas.drawArc(
            18f,
            18f,
            width.toFloat()-18,
            height.toFloat()-18,
            -90f,
            startAngel,
            true,
            paint
        )

        canvas.drawArc(
            24f,
            24f,
            width.toFloat()-24,
            height.toFloat()-24,
            -90f,
            startAngel,
            true,
            paint
        )

        canvas.drawArc(
            48f,
            48f,
            width.toFloat()-48,
            height.toFloat()-48,
            -90f,
            startAngel,
            true,
            paintBack
        )


    }

    /**
     * Set lasted milliseconds
     */
    fun setCurrent(current: Long) {
        currentMs = current
        invalidate()
    }

    /**
     * Set time period
     */
    fun setPeriod(period: Long) {
        periodMs = period
    }


    private companion object {

        private const val FILL = 0
    }
}
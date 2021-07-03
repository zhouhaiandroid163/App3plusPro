package com.zjw.apps3pluspro.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.zjw.apps3pluspro.R

class RoundProgress2View(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var angleProgress1: Float = 0.0f
    private var angleProgress2: Float = 0.0f
    private var angleProgress3: Float = 0.0f
    private var paint: Paint = Paint()
    private var paintBg: Paint = Paint()
    private var centerY: Float = 0.0f
    private var centerX: Float = 0.0f

    private var bgColor: Int = 0
    private var fillingColor: Int = 0
    private var strokeWidth: Int = 0

    init {
        val typeArray =
            context!!.theme.obtainStyledAttributes(attrs, R.styleable.roundProgress2View, 0, 0)
        bgColor = typeArray.getColor(
            R.styleable.roundProgress2View_round_progress_bg,
            ContextCompat.getColor(context, R.color.bt_text_color)
        )
        fillingColor = typeArray.getColor(
            R.styleable.roundProgress2View_round_progress_filling,
            ContextCompat.getColor(context, R.color.multiProgressView3)
        )
        strokeWidth = typeArray.getColor(
            R.styleable.roundProgress2View_round_progress_width,
            10
        )

        paint.strokeWidth = dp2px(strokeWidth).toFloat()
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.color = ContextCompat.getColor(context!!, R.color.round_progress_bg)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        centerX = width / 2f
        centerY = height / 2f

        val rectF = RectF()
        rectF.left = 0f + paint.strokeWidth / 2f
        rectF.top = 0f + paint.strokeWidth / 2f
        rectF.right = width.toFloat() - paint.strokeWidth / 2f
        rectF.bottom = height.toFloat() - paint.strokeWidth / 2f

        paint.color = bgColor
        canvas.drawArc(rectF, -90f, 360f, false, paint)

//        paint.color = ContextCompat.getColor(context!!, R.color.multiProgressView1)
//        canvas.drawArc(rectF, -90f, 360f * angleProgress1, false, paint)

        paint.color = fillingColor
        canvas.drawArc(rectF, -90f, 360f * angleProgress2, false, paint)

//        paint.color = ContextCompat.getColor(context!!, R.color.multiProgressView3)
//        canvas.drawArc(rectF, -90f, 360f * angleProgress3, false, paint)

    }

    fun setProgress(angleProgress1: Float, angleProgress2: Float, angleProgress3: Float) {
        this.angleProgress1 = angleProgress1
        this.angleProgress2 = angleProgress2
        this.angleProgress3 = angleProgress3
        invalidate()
    }

    fun setFillingColor(fillingColor: Int) {
        this.fillingColor = fillingColor
    }

    fun setFullProgress(fillingColor: Int) {
        this.fillingColor = fillingColor
        this.angleProgress1 = 0f
        this.angleProgress2 = 100f
        this.angleProgress3 = 0f
        invalidate()
    }

    private fun dp2px(value: Int): Int {
        val v = context.resources.displayMetrics.density
        return (v * value + 0.5f).toInt()
    }
}

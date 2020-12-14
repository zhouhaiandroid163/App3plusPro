package com.zjw.apps3pluspro.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.zjw.apps3pluspro.R
import kotlin.math.roundToInt

@Suppress("DEPRECATION")
class DistributedView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var paint_line: Paint

    init {
        paint_line = Paint()
        paint_line.setAntiAlias(true)
        paint_line.setStrokeCap(Paint.Cap.SQUARE)
        paint_line.setStyle(Paint.Style.FILL)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val rect1 = Rect(0, 0, (progress1 * width).roundToInt(), height)
        paint_line.color = resources.getColor(R.color.device_sport_heart1)
        canvas.drawRect(rect1, paint_line)

        var startX = this.progress1 * width
        val rect2 = Rect(startX.roundToInt(), 0, (startX + progress2 * width).roundToInt(), height)
        paint_line.color = resources.getColor(R.color.device_sport_heart2)
        canvas.drawRect(rect2, paint_line)

        startX += this.progress2 * width
        val rect3 = Rect(startX.roundToInt(), 0, (startX + progress3 * width).roundToInt(), height)
        paint_line.color = resources.getColor(R.color.device_sport_heart3)
        canvas.drawRect(rect3, paint_line)

        startX += this.progress3 * width
        val rect4 = Rect(startX.roundToInt(), 0, (startX + progress4 * width).roundToInt(), height)
        paint_line.color = resources.getColor(R.color.device_sport_heart4)
        canvas.drawRect(rect4, paint_line)

        startX += this.progress4 * width
        val rect5 = Rect(startX.roundToInt(), 0, (startX + progress5 * width).roundToInt(), height)
        paint_line.color = resources.getColor(R.color.device_sport_heart5)
        canvas.drawRect(rect5, paint_line)
    }

    private var progress1 = 0f
    private var progress2 = 0f
    private var progress3 = 0f
    private var progress4 = 0f
    private var progress5 = 0f
    private var total = 0

    fun start(progress1: Int, progress2: Int, progress3: Int, progress4: Int, progress5: Int) {
        val total = progress1 + progress2 + progress3 + progress4 + progress5
        if (total != 0) {
            this.progress1 = (progress1 * 1f / total)
            this.progress2 = (progress2 * 1f / total)
            this.progress3 = (progress3 * 1f / total)
            this.progress4 = (progress4 * 1f / total)
            this.progress5 = (progress5 * 1f / total)
        }
        invalidate()
    }

}
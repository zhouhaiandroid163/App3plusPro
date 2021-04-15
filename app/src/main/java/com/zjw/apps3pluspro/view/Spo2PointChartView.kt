package com.zjw.apps3pluspro.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.Color.WHITE
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.zjw.apps3pluspro.R
import kotlin.math.ceil


/**
 * Created by android
 * on 2021/4/13
 */
class Spo2PointChartView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var textWidth: Int = 0
    private var textWidth2: Int = 0
    private var paintText: Paint = Paint()
    private var paintStandardLine: Paint = Paint()
    private var paintDash: Paint = Paint()
    private var paintDash2: Paint = Paint()
    private var paintPoint: Paint = Paint()
    private var list = ArrayList<DataValue>()
    private var textHeight = 0f
    private var padding: Int = 0
    private val totalLength = 24 * 3600;

    init {
        val typeArray = context!!.theme.obtainStyledAttributes(attrs, R.styleable.curveChartView, 0, 0)

        paintText = Paint()
        paintText.strokeWidth = dp2px(2).toFloat()
        paintText.isAntiAlias = true
        paintText.style = Paint.Style.FILL
        paintText.color = ContextCompat.getColor(context!!, R.color.white);
        paintText.textSize = sp2px(10).toFloat()

        paintStandardLine = Paint()
        paintStandardLine.strokeWidth = dp2px(1).toFloat()
        paintStandardLine.isAntiAlias = true
        paintStandardLine.style = Paint.Style.FILL
        paintStandardLine.color = ContextCompat.getColor(context!!, R.color.color_DDDDDD)
        paintStandardLine.textSize = sp2px(10).toFloat()

        padding = dp2px(6)
        val fontMetrics: Paint.FontMetricsInt = paintText.fontMetricsInt
        textHeight = ceil(fontMetrics.descent - fontMetrics.ascent.toDouble()).toFloat()
        textWidth = getTextWidth(paintText, "00:00")
        textWidth2 = getTextWidth(paintText, "100")

        paintDash = Paint()
        paintDash.style = Paint.Style.STROKE
        paintDash.color = WHITE
        paintDash.isAntiAlias = true
        paintDash.strokeWidth = dp2px(1).toFloat()

        paintDash2 = Paint()
        paintDash2.style = Paint.Style.STROKE
        paintDash2.isAntiAlias = true
        paintDash2.color = ContextCompat.getColor(context!!, R.color.white_30)
        paintDash2.strokeWidth = dp2px(1).toFloat()

        paintPoint = Paint()
        paintPoint.isAntiAlias = true
        paintPoint.style = Paint.Style.FILL
        paintPoint.strokeCap = Paint.Cap.ROUND
        paintPoint.strokeWidth = dp2px(8).toFloat()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val baseline = height - textHeight - padding
        val width = this.width - textWidth2 * 3f / 2

        val effects: PathEffect = DashPathEffect(floatArrayOf(15f, 5f), 15f)

        val pathDash0 = Path()
        pathDash0.moveTo(0f, baseline * 0 / 4)
        pathDash0.lineTo(width, baseline * 0 / 4)
        paintDash2.pathEffect = effects
        canvas.drawPath(pathDash0, paintDash2)

        val pathDash1 = Path()
        pathDash1.moveTo(0f, baseline * 1 / 4)
        pathDash1.lineTo(width, baseline * 1 / 4)
        canvas.drawPath(pathDash1, paintDash2)

        val pathDash2 = Path()
        pathDash2.moveTo(0f, baseline * 2 / 4)
        pathDash2.lineTo(width, baseline * 2 / 4)
        paintDash.pathEffect = effects
        canvas.drawPath(pathDash2, paintDash)

        val pathDash3 = Path()
        pathDash3.moveTo(0f, baseline * 3 / 4)
        pathDash3.lineTo(width, baseline * 3 / 4)
        paintDash2.pathEffect = effects
        canvas.drawPath(pathDash3, paintDash2)

        canvas.drawLine(0f, baseline * 4 / 4, width, baseline * 4 / 4, paintStandardLine)

        val pathDashY = Path()
        pathDashY.moveTo(width * 0 / 4f, 0f)
        pathDashY.lineTo(width * 0 / 4f, baseline)
        paintDash2.pathEffect = effects
        canvas.drawPath(pathDashY, paintDash2)

        pathDashY.moveTo(width * 1 / 4f, 0f)
        pathDashY.lineTo(width * 1 / 4f, baseline)
        canvas.drawPath(pathDashY, paintDash2)

        pathDashY.moveTo(width * 2 / 4f, 0f)
        pathDashY.lineTo(width * 2 / 4f, baseline)
        canvas.drawPath(pathDashY, paintDash2)

        pathDashY.moveTo(width * 3 / 4f, 0f)
        pathDashY.lineTo(width * 3 / 4f, baseline)
        canvas.drawPath(pathDashY, paintDash2)

        pathDashY.moveTo(width * 4 / 4f, 0f)
        pathDashY.lineTo(width * 4 / 4f, baseline)
        canvas.drawPath(pathDashY, paintDash2)

        canvas.drawText("100", width + textWidth2 / 3f, baseline * 0f / 4f + textHeight * 3 / 4f, paintText)
        canvas.drawText("95", width + textWidth2 / 3f, baseline * 1f / 4f + textHeight * 1 / 3f, paintText)
        canvas.drawText("90", width + textWidth2 / 3f, baseline * 2f / 4f + textHeight * 1 / 3f, paintText)
        canvas.drawText("85", width + textWidth2 / 3f, baseline * 3f / 4f + textHeight * 1 / 3f, paintText)
        canvas.drawText("80", width + textWidth2 / 3f, baseline * 4f / 4f + textHeight * 1 / 3f, paintText)

        canvas.drawText("00:00", 0f, baseline + textHeight, paintText)
        canvas.drawText("06:00", width * 1 / 4f - textWidth / 2, baseline + textHeight, paintText)
        canvas.drawText("12:00", width * 2 / 4f - textWidth / 2, baseline + textHeight, paintText)
        canvas.drawText("18:00", width * 3 / 4f - textWidth / 2, baseline + textHeight, paintText)
        canvas.drawText("23:00", (width - textWidth), baseline + textHeight, paintText)

        if (list.isEmpty()) {
            return
        }
        for (i in 0 until list.size) {
            val dataValue = list[i]
            if (dataValue.value > 10) {
                paintPoint.color = ContextCompat.getColor(context!!, R.color.color_3AFF68)
                canvas.drawPoint(width * dataValue.time / totalLength, (1 - dataValue.value / 20f) * baseline, paintPoint)
            } else {
                paintPoint.color = ContextCompat.getColor(context!!, R.color.color_F8E754)
                canvas.drawPoint(width * dataValue.time / totalLength, (1 - dataValue.value / 20f) * baseline, paintPoint)
            }
        }

    }

    private var touchPos = -1.0f
    fun setTouchPos(eventX: Float) {
        touchPos = eventX
    }

    private var onSlidingListener: OnSlidingListener? = null

    fun setOnSlidingListener(onSlidingListener: OnSlidingListener?) {
        this.onSlidingListener = onSlidingListener
    }

    interface OnSlidingListener {
        fun SlidingDisOver(data: String, index: Int)
    }

    fun setParameter(data: ArrayList<DataValue>) {
        this.list = data
        invalidate()
    }

    private fun dp2px(value: Int): Int {
        val v = context.resources.displayMetrics.density
        return (v * value + 0.5f).toInt()
    }

    private fun sp2px(value: Int): Int {
        val v = context.resources.displayMetrics.scaledDensity
        return (v * value + 0.5f).toInt()
    }

    private fun getTextWidth(paint: Paint, str: String?): Int {
        var iRet = 0
        if (str != null && str.length > 0) {
            val len = str.length
            val widths = FloatArray(len)
            paint.getTextWidths(str, widths)
            for (j in 0 until len) {
                iRet += Math.ceil(widths[j].toDouble()).toInt()
            }
        }
        return iRet
    }

    class DataValue {
        var time = 0
        var value = 0
    }
}
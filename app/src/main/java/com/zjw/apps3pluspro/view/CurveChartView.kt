package com.zjw.apps3pluspro.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.zjw.apps3pluspro.R
import kotlin.math.ceil
import kotlin.math.roundToInt

class CurveChartView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var lineColor: Int = 0
    private var gradientColorStart: Int = 0
    private var gradientColorEnd: Int = 0
    private var textWidth: Int = 0
    private var paintText: Paint = Paint()
    private var paintStandardLine: Paint = Paint()
    private var paintLine: Paint = Paint()
    private var paintCircle: Paint = Paint()
    private var standard: String = ""
    var data: Array<String> = emptyArray()
    private var textHeight = 0f
    private var padding: Int = 0

    init {
        val typeArray = context!!.theme.obtainStyledAttributes(attrs, R.styleable.curveChartView, 0, 0)
        lineColor = typeArray.getColor(R.styleable.curveChartView_curveChartView_LineColor, -0x1)
        gradientColorStart = typeArray.getColor(R.styleable.curveChartView_gradientColorStart, -0x1)
        gradientColorEnd = typeArray.getColor(R.styleable.curveChartView_gradientColorEnd, -0x1)

        paintCircle = Paint()
        paintCircle.strokeWidth = dp2px(1).toFloat()
        paintCircle.isAntiAlias = true
        paintCircle.color = lineColor
        paintCircle.style = Paint.Style.FILL

        paintLine = Paint()
        paintLine.strokeWidth = dp2px(1).toFloat()
        paintLine.isAntiAlias = true
        paintLine.color = lineColor
        paintLine.textSize = sp2px(10).toFloat()
        paintLine.style = Paint.Style.STROKE

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
        paintStandardLine.color = ContextCompat.getColor(context!!, R.color.color_DDDDDD);
        paintStandardLine.textSize = sp2px(10).toFloat()

        padding = dp2px(6)
        val fontMetrics: Paint.FontMetricsInt = paintText.fontMetricsInt
        textHeight = ceil(fontMetrics.descent - fontMetrics.ascent.toDouble()).toFloat()
        textWidth = getTextWidth(paintText, "00:00")
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val baseline = height - textHeight - padding;

        // y 轴
        canvas.drawLine(0f, baseline * 0 / 4, width.toFloat(), baseline * 0 / 4, paintStandardLine)
        canvas.drawLine(0f, baseline * 1 / 4, width.toFloat(), baseline * 1 / 4, paintStandardLine)
        canvas.drawLine(0f, baseline * 2 / 4, width.toFloat(), baseline * 2 / 4, paintStandardLine)
        canvas.drawLine(0f, baseline * 3 / 4, width.toFloat(), baseline * 3 / 4, paintStandardLine)
        canvas.drawLine(0f, baseline * 4 / 4, width.toFloat(), baseline * 4 / 4, paintStandardLine)

        canvas.drawLine(width * 0 / 4f, 0f, width * 0 / 4f, baseline, paintStandardLine)
        canvas.drawLine(width * 1 / 4f, 0f, width * 1 / 4f, baseline, paintStandardLine)
        canvas.drawLine(width * 2 / 4f, 0f, width * 2 / 4f, baseline, paintStandardLine)
        canvas.drawLine(width * 3 / 4f, 0f, width * 3 / 4f, baseline, paintStandardLine)
        canvas.drawLine(width * 4 / 4f, 0f, width * 4 / 4f, baseline, paintStandardLine)

        canvas.drawText(standard, 0f, baseline / 2 - textHeight / 3, paintText)
        canvas.drawText("00:00", 0f, baseline + textHeight, paintText)
        canvas.drawText("06:00", width * 1 / 4f - textWidth / 2, baseline + textHeight, paintText)
        canvas.drawText("12:00", width * 2 / 4f - textWidth / 2, baseline + textHeight, paintText)
        canvas.drawText("18:00", width * 3 / 4f - textWidth / 2, baseline + textHeight, paintText)
        canvas.drawText("23:00", (width - textWidth).toFloat(), baseline + textHeight, paintText)
        if (data.isEmpty()) {
            return
        }
        mPoints = arrayOfNulls<Point>(data.size)
        for (i in data.indices) {
            val x: Float = i * width * 1f / (data.size - 1)
            val y: Float = baseline - baseline * data[i].toFloat() / (2 * standard.toFloat())
            mPoints[i] = Point(x.roundToInt(), y.roundToInt())
        }

        var startp: Point = mPoints[0]!!
        var endp: Point = mPoints[mPoints.size - 1]!!
        val path = Path()

        path.moveTo(startp.x.toFloat(), baseline)
        path.lineTo(startp.x.toFloat(), startp.y.toFloat())

        for (i in 0 until mPoints.size - 1) {
            startp = mPoints[i]!!
            endp = mPoints[i + 1]!!
            val wt = (startp.x + endp.x) / 2
            val p3 = Point()
            val p4 = Point()
            p3.y = startp.y
            p3.x = wt
            p4.y = endp.y
            p4.x = wt
            path.lineTo(startp.x.toFloat(), startp.y.toFloat())
            path.cubicTo(p3.x.toFloat(), p3.y.toFloat(), p4.x.toFloat(), p4.y.toFloat(), endp.x.toFloat(), endp.y.toFloat())
        }
        path.lineTo(endp.x.toFloat(), baseline);
        path.close()

        val lineGradientPaint = Paint()
        val startColor: Int = gradientColorStart
        val endColor: Int = gradientColorEnd
        val gradient = LinearGradient(0f, padding * 1f, 0f, baseline, startColor, endColor, Shader.TileMode.MIRROR)
        lineGradientPaint.shader = gradient
        lineGradientPaint.strokeWidth = dp2px(2).toFloat()
        canvas.drawPath(path, lineGradientPaint)


        val path2 = Path()
        for (i in 0 until mPoints.size - 1) {
            startp = mPoints[i]!!
            endp = mPoints[i + 1]!!
            val wt = (startp.x + endp.x) / 2
            val p3 = Point()
            val p4 = Point()
            p3.y = startp.y
            p3.x = wt
            p4.y = endp.y
            p4.x = wt

            path2.moveTo(startp.x.toFloat(), startp.y.toFloat())
            path2.cubicTo(p3.x.toFloat(), p3.y.toFloat(), p4.x.toFloat(), p4.y.toFloat(), endp.x.toFloat(), endp.y.toFloat())
//            canvas.drawCircle(startp.x.toFloat(), startp.y.toFloat(), dp2px(2).toFloat(), paintCircle)
        }
//        endp = mPoints[mPoints.size - 1]!!
//        canvas.drawCircle(endp.x.toFloat(), endp.y.toFloat(), dp2px(2).toFloat(), paintCircle)
        canvas.drawPath(path2, paintLine)

        if (touchPos == -1.0f) {
            if (onSlidingListener != null) {
                onSlidingListener!!.SlidingDisOver("-1", -1)
            }
            return
        }
        var dataBlockIndex = (touchPos / (width * 1f / (data.size - 1))).toInt()

        if (dataBlockIndex > data.size - 1) {
            dataBlockIndex = data.size - 1
        }
        if (dataBlockIndex < 0) {
            dataBlockIndex = 0
        }
        val value: String = data.get(dataBlockIndex)
        onSlidingListener?.SlidingDisOver(value, dataBlockIndex)

        val x: Float = dataBlockIndex * width * 1f / (data.size - 1)
        canvas.drawLine(x, 0f, x, baseline, paintLine)

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

    private var mPoints: Array<Point?> = emptyArray()
    fun setParameter(data: Array<String>, standard: String) {
        this.data = data
        this.standard = standard
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
}
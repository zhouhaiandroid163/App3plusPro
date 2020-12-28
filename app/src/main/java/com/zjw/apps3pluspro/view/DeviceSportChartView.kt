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

class DeviceSportChartView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var type: Int = 0
    private var maxY: Float = 0f
    private var lineColor: Int = 0
    private var gradientColorStart: Int = 0
    private var gradientColorEnd: Int = 0
    private var textWidth: Int = 0
    private var paintText: Paint = Paint()
    private var paintStandardLine: Paint = Paint()
    private var paintLine: Paint = Paint()
    private var paintCircle: Paint = Paint()
    private var standard: String = ""
    var yData = ArrayList<Double>()
    var xData = ArrayList<Double>()
    private var textHeight = 0f
    private var padding: Int = 24

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
        paintText.color = ContextCompat.getColor(context!!, R.color.color_878B90);
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
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val baseline = height - textHeight - padding;

        if (xData.isEmpty() || yData.isEmpty() || maxY == 0f) {
            return
        }
        var startX: Float = (padding + textWidth).toFloat()
        var endX: Float = width - (padding + textWidth).toFloat()

        var spacing: Float = 0f
        var endIndex: Float = 0f
        if (xData.size == 17) {
            endIndex = ((xData[16] - xData[15]) / (xData[1] - xData[0])).toFloat()
            spacing = (endX - startX) / (xData.size - 1 + endIndex)
        } else {
            spacing = (endX - startX) / xData.size
        }

        val spaceingY = (height - textHeight - 2 * padding) / 4
        canvas.drawLine(startX, padding + 0 * spaceingY, endX, padding + 0 * spaceingY, paintStandardLine)
        canvas.drawLine(startX, padding + 1 * spaceingY, endX, padding + 1 * spaceingY, paintStandardLine)
        canvas.drawLine(startX, padding + 2 * spaceingY, endX, padding + 2 * spaceingY, paintStandardLine)
        canvas.drawLine(startX, padding + 3 * spaceingY, endX, padding + 3 * spaceingY, paintStandardLine)
        canvas.drawLine(startX, padding + 4 * spaceingY, endX, padding + 4 * spaceingY, paintStandardLine)

//        canvas.drawText((maxY * 4 / 4).toString(), padding.toFloat(), padding + 0 * spaceingY + textHeight * 1 / 3, paintText)
        if (type == 1) {
            var textWidth3 = getTextWidth(paintText, text3)
            var textWidth2 = getTextWidth(paintText, text2)
            var textWidth1 = getTextWidth(paintText, text1)
            canvas.drawText(text3, startX - textWidth3, padding + 1 * spaceingY + textHeight * 1 / 3, paintText)
            canvas.drawText(text2, startX - textWidth2, padding + 2 * spaceingY + textHeight * 1 / 3, paintText)
            canvas.drawText(text1, startX - textWidth1, padding + 3 * spaceingY + textHeight * 1 / 3, paintText)
        } else {
            if (maxY < 30) {
                canvas.drawText(String.format("%.1f", (maxY * 3.0 / 4)), padding.toFloat(), padding + 1 * spaceingY + textHeight * 1 / 3, paintText)
                canvas.drawText(String.format("%.1f", (maxY * 2.0 / 4)), padding.toFloat(), padding + 2 * spaceingY + textHeight * 1 / 3, paintText)
                canvas.drawText(String.format("%.1f", (maxY * 1.0 / 4)), padding.toFloat(), padding + 3 * spaceingY + textHeight * 1 / 3, paintText)
            } else {
                canvas.drawText((maxY * 3 / 4).toInt().toString(), padding.toFloat(), padding + 1 * spaceingY + textHeight * 1 / 3, paintText)
                canvas.drawText((maxY * 2 / 4).toInt().toString(), padding.toFloat(), padding + 2 * spaceingY + textHeight * 1 / 3, paintText)
                canvas.drawText((maxY * 1 / 4).toInt().toString(), padding.toFloat(), padding + 3 * spaceingY + textHeight * 1 / 3, paintText)
            }
        }

        var textUnit = "(" + resources.getString(R.string.minute) + ")"
        canvas.drawText(textUnit, 0f, baseline + textHeight, paintText)
        canvas.drawText("0", startX - getTextWidth(paintText, "0") / 2, baseline + textHeight, paintText)
        canvas.drawLine(startX, padding.toFloat(), startX, baseline, paintStandardLine)

        mPoints = arrayOfNulls<Point>(yData.size + 1)
        mPoints[0] = Point(startX.roundToInt(), baseline.roundToInt())

        for (i in xData.indices) {
            var x: Float = 0f
            if (xData.size == 17) {
                if (i < xData.size - 1) {
                    x = startX + (i + 1) * spacing
                    if ((i + 1) % 2 == 0) {
                        canvas.drawText(String.format("%.1f", xData[i]), x - getTextWidth(paintText, String.format("%.1f", xData[i])) / 2, baseline + textHeight, paintText)
                        canvas.drawLine(x, padding.toFloat(), x, baseline, paintStandardLine)
                    }
                }
                if (i == xData.size - 1) {
                    x = startX + (i + endIndex) * spacing
//                    canvas.drawText(String.format("%.1f", xData[i]), x - getTextWidth(paintText, String.format("%.1f", xData[i])) / 2, baseline + textHeight, paintText)
                    canvas.drawLine(x, padding.toFloat(), x, baseline, paintStandardLine)
                }
            } else {
                x = startX + (i + 1) * spacing

                if ((i + 1) % 2 == 0) {
                    canvas.drawText(String.format("%.1f", xData[i]), x - getTextWidth(paintText, String.format("%.1f", xData[i])) / 2, baseline + textHeight, paintText)
                    canvas.drawLine(x, padding.toFloat(), x, baseline, paintStandardLine)
                }
            }
            val y: Double = baseline - (baseline - padding) * yData[i] / maxY
            mPoints[i + 1] = Point(x.roundToInt(), y.roundToInt())
        }


        var startp: Point
        var endp: Point = mPoints[mPoints.size - 1]!!
        val path = Path()

        path.moveTo(startX, baseline)

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
        path.lineTo(endp.x.toFloat(), baseline)
        path.close()

        val lineGradientPaint = Paint()
        val startColor: Int = gradientColorStart
        val endColor: Int = gradientColorEnd
        val gradient = LinearGradient(0f, padding * 1f, 0f, baseline, startColor, endColor, Shader.TileMode.MIRROR)
        lineGradientPaint.shader = gradient
        lineGradientPaint.strokeWidth = dp2px(2).toFloat()
        canvas.drawPath(path, lineGradientPaint)


        val path2 = Path()
        path2.moveTo(startX, baseline)
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

    private var text3: String = ""
    private var text2: String = ""
    private var text1: String = ""
    private var mPoints: Array<Point?> = emptyArray()
    fun setParameter(xData: ArrayList<Double>, yData: ArrayList<Double>) {
        this.yData = yData
        this.xData = xData

        var max: Double = 0.0
        for (i in yData.indices) {
            if (yData.get(i) > max) {
                max = yData.get(i)
            }
        }
        maxY = (max * 1.2).toFloat()
        if (maxY == 0f) {
            maxY = 1f
        }

        if (type == 1) { // 配速
            val second3: Int = (maxY * 3 * 60f / 4).toInt()
            text3 = String.format("%1$02d'%2$02d\"", (second3 / 60), (second3 % 60))

            val second2: Int = (maxY * 2 * 60f / 4).toInt()
            text2 = String.format("%1$02d'%2$02d\"", (second2 / 60), (second2 % 60))

            val second1: Int = (maxY * 1 * 60f / 4).toInt()
            text1 = String.format("%1$02d'%2$02d\"", (second1 / 60), (second1 % 60))
        }

        invalidate()
    }

    fun setType(type: Int) {
        this.type = type;
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
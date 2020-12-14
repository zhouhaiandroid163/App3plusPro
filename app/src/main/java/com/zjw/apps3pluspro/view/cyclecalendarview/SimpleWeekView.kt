package com.zjw.apps3pluspro.view.cyclecalendarview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.WeekView
import com.zjw.apps3pluspro.module.home.cycle.utils.MyCalendarUtils

class SimpleWeekView(private val my_contex: Context) : WeekView(my_contex) {
    var spacing = 4f
    var strokeWidth = 4f
    private var mRadius = 0f
    override fun onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 11 * 5.toFloat()
    }

    /**
     * 如果这里和 onDrawScheme 是互斥的，则 return false，
     * return true 会先绘制 onDrawSelected，再绘制onDrawSelected
     *
     * @param canvas    canvas
     * @param calendar  日历日历calendar
     * @param x         日历Card x起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return
     */
    //选中
    override fun onDrawSelected(canvas: Canvas, calendar: Calendar, x: Int, hasScheme: Boolean): Boolean {
        mSelectedPaint.style = Paint.Style.STROKE
        mSelectedPaint.strokeWidth = strokeWidth
        val cx = x + mItemWidth / 2f
        val cy = mItemHeight / 2f
        val rectF = RectF()
        rectF.left = cx - mRadius
        rectF.right = cx + mRadius
        rectF.top = cy - mRadius
        rectF.bottom = cy + mRadius
        canvas.drawArc(rectF, 0f, 360f, true, mSelectedPaint)
        return true
    }

    //标记日期
    override fun onDrawScheme(canvas: Canvas, calendar: Calendar, x: Int) { //        canvas.drawRect(x + xishu, 0 + xishu, x + mItemWidth - xishu, 0 + mItemHeight - xishu, mSchemePaint);
        val cx = x + mItemWidth / 2f
        val cy = mItemHeight / 2f
        val rectF = RectF()
        rectF.left = cx - mRadius + spacing
        rectF.right = cx + mRadius - spacing
        rectF.top = cy - mRadius + spacing
        rectF.bottom = cy + mRadius - spacing
        val str = calendar.scheme
        val text_color = MyCalendarUtils.getBgColor(my_contex, str)
        mSchemePaint.color = resources.getColor(text_color)
        canvas.drawCircle(cx, cy, mRadius - spacing, mSchemePaint)
    }

    override fun onDrawText(canvas: Canvas, calendar: Calendar, x: Int, hasScheme: Boolean, isSelected: Boolean) {
        val cx = x + mItemWidth / 2
        val str = calendar.scheme
        val text_color = MyCalendarUtils.getTextColor(my_contex, str)
        mSchemeTextPaint.color = resources.getColor(text_color)
        mCurDayTextPaint.color = resources.getColor(text_color)
        if (hasScheme) {
            canvas.drawText(calendar.day.toString(), cx.toFloat(), mTextBaseLine ,
                    if (calendar.isCurrentDay) mCurDayTextPaint else if (calendar.isCurrentMonth) mSchemeTextPaint else mCurMonthTextPaint)
        } else {
            canvas.drawText(calendar.day.toString(), cx.toFloat(), mTextBaseLine ,
                    if (calendar.isCurrentDay) mCurDayTextPaint else if (calendar.isCurrentMonth) mCurMonthTextPaint else mCurMonthTextPaint)
        }
    }

    init {
        mSchemePaint.isAntiAlias = true
        mSchemePaint.style = Paint.Style.FILL
        mSchemePaint.strokeWidth = 2f
    }
}

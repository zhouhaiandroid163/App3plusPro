package com.zjw.apps3pluspro.view.cyclecalendarview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.MonthView
import com.zjw.apps3pluspro.module.home.cycle.utils.MyCalendarUtils

class SimpleMonthView(private val my_contex: Context) : MonthView(my_contex) {
    var spacing = 4f
    var strokeWidth = 4f
    override fun onPreviewHook() { //        System.out.println("日历测试 = 月视图");
        mRadius = Math.min(mItemWidth, mItemHeight) / 11 * 5.toFloat()
    }

    override fun onLoopStart(x: Int, y: Int) {}
    override fun onDrawSelected(canvas: Canvas, calendar: Calendar, x: Int, y: Int, hasScheme: Boolean): Boolean {
        mSelectedPaint.isAntiAlias = true
        mSelectedPaint.style = Paint.Style.STROKE
        mSelectedPaint.strokeWidth = strokeWidth
        val cx = x + mItemWidth / 2f
        val cy = y + mItemHeight / 2f
        val rectF = RectF()
        rectF.left = cx - mRadius
        rectF.right = cx + mRadius
        rectF.top = cy - mRadius
        rectF.bottom = cy + mRadius
        canvas.drawArc(rectF, 0f, 360f, true, mSelectedPaint)
        return true
    }

    private var mRadius = 0f
    override fun onDrawScheme(canvas: Canvas, calendar: Calendar, x: Int, y: Int) {
        val cx = x + mItemWidth / 2f
        val cy = y + mItemHeight / 2f
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

    override fun onDrawText(canvas: Canvas, calendar: Calendar, x: Int, y: Int, hasScheme: Boolean, isSelected: Boolean) {
        val baselineY = mTextBaseLine + y
        val cx = x + mItemWidth / 2
        val str = calendar.scheme
        val text_color = MyCalendarUtils.getTextColor(my_contex, str)
        mSchemeTextPaint.color = resources.getColor(text_color)
        mCurDayTextPaint.color = resources.getColor(text_color)
        if (hasScheme) {
            canvas.drawText(calendar.day.toString(),
                    cx.toFloat(),
                    baselineY,
                    if (calendar.isCurrentDay) mCurDayTextPaint else if (calendar.isCurrentMonth) mSchemeTextPaint else mOtherMonthTextPaint)
        } else {
            canvas.drawText(calendar.day.toString(), cx.toFloat(), baselineY,
                    if (calendar.isCurrentDay) mCurDayTextPaint else if (calendar.isCurrentMonth) mCurMonthTextPaint else mOtherMonthTextPaint)
        }
    }

    init {
        mSchemePaint.isAntiAlias = true
        mSchemePaint.style = Paint.Style.FILL
        mSchemePaint.strokeWidth = 2f
    }
}

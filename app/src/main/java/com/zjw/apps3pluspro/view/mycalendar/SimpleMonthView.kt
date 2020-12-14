package com.zjw.apps3pluspro.view.mycalendar

import android.content.Context
import android.graphics.Canvas
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.MonthView

class SimpleMonthView(context: Context) : MonthView(context) {
    private var mRadius = 0

    init {
    }
    override fun onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 11 * 4
    }

    override fun onDrawSelected(canvas: Canvas, calendar: Calendar?, x: Int, y: Int, hasScheme: Boolean): Boolean {
        val cx = x + mItemWidth / 2
        val cy = y + mItemHeight / 2
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), mRadius.toFloat(), mSelectedPaint)
        return false
    }

    override fun onDrawScheme(canvas: Canvas, calendar: Calendar, x: Int, y: Int) {
    }

    override fun onDrawText(canvas: Canvas, calendar: Calendar, x: Int, y: Int, hasScheme: Boolean, isSelected: Boolean) {
        val baselineY = mTextBaseLine + y
        val cx = x + mItemWidth / 2
        if (isSelected) {
            canvas.drawText(calendar.day.toString(),
                    cx.toFloat(),
                    baselineY,
                    mSelectTextPaint)
        } else if (hasScheme) {
            canvas.drawText(calendar.day.toString(),
                    cx.toFloat(),
                    baselineY,
                    if (calendar.isCurrentDay) mCurDayTextPaint else if (calendar.isCurrentMonth) mSchemeTextPaint else mOtherMonthTextPaint)
        } else {
            canvas.drawText(calendar.day.toString(), cx.toFloat(), baselineY,
                    if (calendar.isCurrentDay) mCurDayTextPaint else if (calendar.isCurrentMonth) mCurMonthTextPaint else mOtherMonthTextPaint)
        }
    }
}
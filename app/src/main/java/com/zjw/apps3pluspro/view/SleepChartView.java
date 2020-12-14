package com.zjw.apps3pluspro.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.View;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.module.home.entity.SleepData;

import java.util.ArrayList;
import java.util.List;

public class SleepChartView extends View {
    private float barWidthRate;
    private int[] colors;
    private int h;
    private List<SleepData> sleepDataList;
    private Paint p;
    private float paddingBottom;
    private float paddingLeft;
    private float paddingRight;
    private float paddingTop;
    private int w;
    private List<String> xLables;
    private int xLablesCount;
    private int xMax;
    private int xMin;
    private List<String> yLables;
    private int yMax = 3000;
    private int yMin;

    @SuppressWarnings("ResourceType")
    public SleepChartView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        int[] arrayOfInt = new int[5];
//		arrayOfInt[0] = Color.parseColor("#d8d8d8");	// 熬夜颜色
////		arrayOfInt[1] = Color.parseColor("#C0C0F8");    // 开始睡眠的过程，显示颜色
//		arrayOfInt[1] = Color.parseColor("#a79df3");    // 开始睡眠的过程，显示颜色
//		arrayOfInt[2] = Color.parseColor("#8a7def");	//浅睡颜色
//		arrayOfInt[3] = Color.parseColor("#6d5ceb");    //深睡颜色
//		arrayOfInt[4] = Color.parseColor("#FFFFFF");    //中间醒来颜色

        int color0 = getResources().getColor(R.color.sleep_state0);
        int color1 = getResources().getColor(R.color.sleep_state1);
        int color2 = getResources().getColor(R.color.sleep_state2);
        int color3 = getResources().getColor(R.color.sleep_state3);
        int color4 = getResources().getColor(R.color.sleep_state4);

        arrayOfInt[0] = color0;    // 熬夜颜色
        arrayOfInt[1] = color1; // 开始睡眠的过程，显示颜色
        arrayOfInt[2] = color2;    //浅睡颜色
        arrayOfInt[3] = color3;    //深睡颜色
        arrayOfInt[4] = color4;    //中间醒来颜色
        this.colors = arrayOfInt;
        init();
        TypedArray localTypedArray = paramContext.obtainStyledAttributes(
                paramAttributeSet, R.styleable.chart);
        this.xLablesCount = localTypedArray.getInt(0, 0);
//		this.paddingLeft = localTypedArray.getDimension(1, 20.0F);
//		this.paddingRight = localTypedArray.getDimension(2, 20.0F);
//		this.paddingTop = localTypedArray.getDimension(3, 20.0F);
        this.paddingBottom = localTypedArray.getDimension(4, 60.0F);
        this.barWidthRate = localTypedArray.getFloat(9, 0.7F);
        if (this.barWidthRate > 1.0F)
            this.barWidthRate = 1.0F;
        this.xMin = localTypedArray.getInt(5, 0);
        this.xMax = localTypedArray.getInt(6, 480);
        this.yMin = localTypedArray.getInt(7, 0);
        this.yMax = localTypedArray.getInt(8, 3000);
        localTypedArray.recycle();
        //防止切换到后台，再回到前台时候，视图被初始化，没有数据！
        setLayerType(LAYER_TYPE_SOFTWARE, null);

    }


    @SuppressWarnings("ResourceType")
    public SleepChartView(Context paramContext, AttributeSet paramAttributeSet,int color0,int color1,int color2,int color3,int color4) {
        super(paramContext, paramAttributeSet);
        int[] arrayOfInt = new int[5];
//		arrayOfInt[0] = Color.parseColor("#d8d8d8");	// 熬夜颜色
////		arrayOfInt[1] = Color.parseColor("#C0C0F8");    // 开始睡眠的过程，显示颜色
//		arrayOfInt[1] = Color.parseColor("#a79df3");    // 开始睡眠的过程，显示颜色
//		arrayOfInt[2] = Color.parseColor("#8a7def");	//浅睡颜色
//		arrayOfInt[3] = Color.parseColor("#6d5ceb");    //深睡颜色
//		arrayOfInt[4] = Color.parseColor("#FFFFFF");    //中间醒来颜色

//        int color0 = getResources().getColor(R.color.sleep_state0);
//        int color1 = getResources().getColor(R.color.sleep_state1);
//        int color2 = getResources().getColor(R.color.sleep_state2);
//        int color3 = getResources().getColor(R.color.sleep_state3);
//        int color4 = getResources().getColor(R.color.sleep_state4);

        arrayOfInt[0] = color0;    // 熬夜颜色
        arrayOfInt[1] = color1; // 开始睡眠的过程，显示颜色
        arrayOfInt[2] = color2;    //浅睡颜色
        arrayOfInt[3] = color3;    //深睡颜色
        arrayOfInt[4] = color4;    //中间醒来颜色
        this.colors = arrayOfInt;
        init();
        TypedArray localTypedArray = paramContext.obtainStyledAttributes(
                paramAttributeSet, R.styleable.chart);
        this.xLablesCount = localTypedArray.getInt(0, 0);
//		this.paddingLeft = localTypedArray.getDimension(1, 20.0F);
//		this.paddingRight = localTypedArray.getDimension(2, 20.0F);
//		this.paddingTop = localTypedArray.getDimension(3, 20.0F);
//        this.paddingBottom = localTypedArray.getDimension(4, 60.0F);
        this.paddingBottom = localTypedArray.getDimension(4, 0.0F);
        this.barWidthRate = localTypedArray.getFloat(9, 0.0F);
        if (this.barWidthRate > 1.0F)
            this.barWidthRate = 1.0F;
        this.xMin = localTypedArray.getInt(5, 0);
        this.xMax = localTypedArray.getInt(6, 480);
        this.yMin = localTypedArray.getInt(7, 0);
        this.yMax = localTypedArray.getInt(8, 3000);
        localTypedArray.recycle();
        //防止切换到后台，再回到前台时候，视图被初始化，没有数据！
        setLayerType(LAYER_TYPE_SOFTWARE, null);

    }

    private void drawAxis(Canvas paramCanvas, Paint paramPaint) {
        paramPaint.setColor(Color.parseColor("#888888"));
        paramPaint.setTextSize(sp2px(12.0f));
        paramCanvas.drawLine(this.paddingLeft, this.h - this.paddingBottom,
                this.w - this.paddingRight, this.h - this.paddingBottom,
                paramPaint);
        float f1, f2, f3, f4, f5, f6;
        if ((this.xLables != null) && (this.xLables.size() >= 2)) {
//			paramPaint.setColor(Color.parseColor("#ff0000"));
            paramPaint.setColor(Color.parseColor("#9B9B9B"));
            paramPaint.setTextSize(sp2px(12.0f));
            for (int i = 0; i < xLables.size(); i++) {
                if (i == 0) {
                    paramPaint.setTextAlign(Paint.Align.LEFT);
                    f3 = this.paddingLeft;
                    f4 = (this.w - this.paddingLeft - this.paddingRight) / -1
                            + this.xLables.size();
                    paramCanvas.drawText(xLables.get(i), f3,
                            35.0F + (this.h - this.paddingBottom), paramPaint);
                } else {
                    paramPaint.setTextAlign(Align.RIGHT);
                    paramCanvas.drawText(xLables.get(i), this.w
                                    - this.paddingRight,
                            35.0F + (this.h - this.paddingBottom), paramPaint);
                }

            }
        }

    }

    int offSet = 0;
    List<Integer> tiemList = new ArrayList<Integer>();

    private void drawBar(Canvas paramCanvas, Paint paramPaint) {
        tiemList.clear();
        if (this.sleepDataList == null)
            return;
        float f1;
        float f3;
        float f4;
        float f5;

        paramPaint.setStyle(Paint.Style.FILL);
        f1 = this.w - this.paddingLeft - this.paddingRight;
        float f2 = this.h - this.paddingTop - this.paddingBottom;
        f3 = this.paddingTop + 2.0F * f2 / 3.0F;
        f4 = this.paddingTop + 1.0F * f2 / 3.0F;
        f5 = this.paddingTop;
        float f8 = f3;

        // for test
        // float f6 = this.paddingLeft;
        // float f7 = this.w - this.paddingRight;
        // if (f7 > this.w - this.paddingRight)
        // f7 = this.w - this.paddingRight;
        // paramCanvas.drawRect(f6, f8, f7, this.h - this.paddingBottom - 1.0F,
        // paramPaint);
        // test end


        for (int i = 0; i < this.sleepDataList.size(); i++) {

            SleepData sleepDataItem = sleepDataList.get(i);
            SleepData sleepDataItemNext;

            if (i < this.sleepDataList.size() - 1) {
                sleepDataItemNext = sleepDataList.get(i + 1);
            } else {
                sleepDataItemNext = sleepDataList.get(i);
            }

            String[] startDates = sleepDataItem.getStartTime().split(":");

            String[] endDates = sleepDataItemNext.getStartTime().split(":");

            int startHour = Integer.parseInt(startDates[0]);
            int startMin = Integer.parseInt(startDates[1]);
            int endHour = Integer.parseInt(endDates[0]);
            int endMin = Integer.parseInt(endDates[1]);
            int sleep_time;
            if (endHour - startHour < 0) {
                sleep_time = (24 + endHour - startHour) * 60 + (endMin - startMin);
            } else {
                sleep_time = (endHour - startHour) * 60 + (endMin - startMin);
            }

            tiemList.add(sleep_time);
            if (i > 0) {
                offSet = offSet + tiemList.get(i - 1);
            }

            float f6 = this.paddingLeft + f1 * offSet / this.xMax;
            float f7 = this.paddingLeft + (f1 * (offSet + sleep_time) / xMax);
            if (f7 > this.w - this.paddingRight) f7 = this.w - this.paddingRight;
            int type = Integer.parseInt(sleepDataItem.getSleep_type());
            switch (type) {
                case 0:
                    f8 = f5;
                    paramPaint.setColor(this.colors[0]);
                    break;
                case 1:
                    f8 = f5;
                    paramPaint.setColor(this.colors[1]);
                    break;
                case 2:
                    f8 = f4;
                    paramPaint.setColor(this.colors[2]);
                    break;
                case 3:
                    f8 = f5;
                    paramPaint.setColor(this.colors[3]);
                    break;
                case 4:
                    f8 = f4;
                    paramPaint.setColor(this.colors[4]);
                    break;
                case 5:
                    f8 = f4;
                    paramPaint.setColor(this.colors[4]);
                    break;
                default:
                    break;
            }
            paramCanvas.drawRect(f6, f5, f7, this.h - this.paddingBottom - 1.0F, paramPaint);//高度一样
//			paramCanvas.drawRect(f6, f8, f7,this.h - this.paddingBottom - 1.0F, paramPaint);//高度不一样
        }
    }

    private void init() {
        this.p = new Paint();
        this.p.setAntiAlias(true);
    }

    protected void onDraw(Canvas paramCanvas) {
//        drawAxis(paramCanvas, this.p);
        drawBar(paramCanvas, this.p);
    }

    protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3,
                                 int paramInt4) {
        this.w = paramInt1;
        this.h = paramInt2;
        super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    }

    public void setItems(List<SleepData> sleep_data_list) {
        this.sleepDataList = sleep_data_list;
//		postInvalidate();
        int currendThreadId = (int) Thread.currentThread().getId();
        if (BaseApplication.getMainThreadId() == currendThreadId) {
            invalidate();
        } else {
            postInvalidate();
        }

    }

    public void setxLables(List<String> paramList) {
        this.xLables = paramList;
    }

    public void setxLablesCount(int paramInt) {
        this.xLablesCount = paramInt;
    }

    public void setxMax(int paramInt) {
        this.xMax = paramInt;
    }

    public void setxMin(int paramInt) {
        this.xMin = paramInt;
    }

    public void setyLables(List<String> paramList) {
        this.yLables = paramList;
    }

    public void setyMax(int paramInt) {
        this.yMax = paramInt;
    }

    public void setyMin(int paramInt) {
        this.yMin = paramInt;
    }

    private float sp2px(float value) {
        float v = getContext().getResources().getDisplayMetrics().scaledDensity;
        return v * value + 0.5f;
    }


}

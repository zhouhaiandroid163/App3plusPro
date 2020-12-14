package com.zjw.apps3pluspro.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;


import com.zjw.apps3pluspro.R;

import java.text.DecimalFormat;

@SuppressLint("DrawAllocation")
public class StepHistogramView extends View {
    private final int textHeight;
    private final int textWidth;
    private Paint paint_line, paint_step, paint_step_bg, paint_text, paint_scale, paint_step_touch, paintStandardLine;
    private float P_width, P_height, spacing, goal = 100;
    private float[] aniProgress, progress;
    private int[] progress_time, progress_step;
    private int type = 1;
    private HistogramAnimation ani;
    private boolean noData;
    private DecimalFormat aFormat;
    private Paint paint_value;
    private boolean isgrid = false;
    private boolean isDrawX0 = false;
    private float baseline;

    public StepHistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.stepHistogramView, 0, 0);
        int stepHistogramView_histogram_bg = typeArray.getColor(R.styleable.stepHistogramView_stepHistogramView_histogram_bg, getResources().getColor(R.color.StepHistogramView7f));
        int stepHistogramView_histogram = typeArray.getColor(R.styleable.stepHistogramView_stepHistogramView_histogram, getResources().getColor(R.color.StepHistogramView));
        int stepHistogramView_histogram_touch = typeArray.getColor(R.styleable.stepHistogramView_stepHistogramView_histogram_touch, getResources().getColor(R.color.StepHistogramView));
        int stepHistogramView_x_text_color = typeArray.getColor(R.styleable.stepHistogramView_stepHistogramView_x_text_color, getResources().getColor(R.color.StepHistogramView_text));
        isDrawX0 = typeArray.getBoolean(R.styleable.stepHistogramView_stepHistogramView_isDrawX0, false);

        ani = new HistogramAnimation();
        ani.setDuration(1000);

        aniProgress = new float[24];
        for (int i = 0; i < 24; i++) {
            aniProgress[i] = 0;
        }

        paintStandardLine = new Paint();
        paintStandardLine.setStrokeWidth(dp2px(1));
        paintStandardLine.setAntiAlias(true);
        paintStandardLine.setStyle(Paint.Style.FILL);
        paintStandardLine.setColor(getResources().getColor(R.color.white_12));

        paint_value = new Paint();
        paint_value.setStrokeWidth(dp2px(2));
        paint_value.setAntiAlias(true);
        paint_value.setStyle(Paint.Style.FILL);
        paint_value.setColor(getResources().getColor(R.color.color_000111));
        paint_value.setTextSize(sp2px(12));

        paint_line = new Paint();
        paint_line.setStrokeWidth(dp2px(1));
        paint_line.setAntiAlias(true);
        paint_line.setStrokeCap(Cap.ROUND);
        paint_line.setStyle(Paint.Style.FILL);

        paint_step = new Paint();
        paint_step.setStrokeWidth(dp2px(5));
        paint_step.setAntiAlias(true);
        paint_step.setStyle(Paint.Style.FILL);
        paint_step.setColor(stepHistogramView_histogram);

        paint_step_bg = new Paint();
        paint_step_bg.setStrokeWidth(dp2px(5));
        paint_step_bg.setAntiAlias(true);
        paint_step_bg.setStyle(Paint.Style.FILL);
        paint_step_bg.setColor(stepHistogramView_histogram_bg);

        paint_step_touch = new Paint();
        paint_step_touch.setStrokeWidth(dp2px(5));
        paint_step_touch.setAntiAlias(true);
        paint_step_touch.setStyle(Paint.Style.FILL);
        paint_step_touch.setColor(stepHistogramView_histogram_touch);

        paint_text = new Paint();
        paint_text.setStrokeWidth(dp2px(2));
        paint_text.setAntiAlias(true);
        paint_text.setStyle(Paint.Style.FILL);
        paint_text.setColor(stepHistogramView_x_text_color);
        paint_text.setTextSize(sp2px(10));

        paint_scale = new Paint();
        paint_scale.setStrokeWidth(dp2px(2));
        paint_scale.setAntiAlias(true);
        paint_scale.setStyle(Paint.Style.FILL);
        paint_scale.setColor(getResources().getColor(R.color.StepHistogramView_text));
        paint_scale.setTextSize(sp2px(10));


        aFormat = new DecimalFormat(",##0");
        aFormat.applyPattern(",##0.00");

        FontMetricsInt fontMetrics = paint_text.getFontMetricsInt();
        textHeight = fontMetrics.descent - fontMetrics.ascent;
        textWidth = getTextWidth(paint_text, "00:00");
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        P_width = getWidth();
        P_height = getHeight();

        baseline = P_height - textHeight;
        spacing = (P_width) / ((aniProgress.length - 1) + aniProgress.length);

        if (isgrid) {
            // y 轴
            canvas.drawLine(0f, baseline * 0 / 4, P_width, baseline * 0 / 4, paintStandardLine);
            canvas.drawLine(0f, baseline * 1 / 4, P_width, baseline * 1 / 4, paintStandardLine);
            canvas.drawLine(0f, baseline * 2 / 4, P_width, baseline * 2 / 4, paintStandardLine);
            canvas.drawLine(0f, baseline * 3 / 4, P_width, baseline * 3 / 4, paintStandardLine);
            canvas.drawLine(0f, baseline * 4 / 4, P_width, baseline * 4 / 4, paintStandardLine);

            canvas.drawLine(P_width * 0 / 4f, 0f, P_width * 0 / 4f, baseline, paintStandardLine);
            canvas.drawLine(P_width * 1 / 4f, 0f, P_width * 1 / 4f, baseline, paintStandardLine);
            canvas.drawLine(P_width * 2 / 4f, 0f, P_width * 2 / 4f, baseline, paintStandardLine);
            canvas.drawLine(P_width * 3 / 4f, 0f, P_width * 3 / 4f, baseline, paintStandardLine);
            canvas.drawLine(P_width * 4 / 4f, 0f, P_width * 4 / 4f, baseline, paintStandardLine);
        }
        if(isDrawX0){
            canvas.drawLine(0f, baseline * 4 / 4, P_width, baseline * 4 / 4, paintStandardLine);
        }
        canvas.drawText("00:00", 0f, baseline + textHeight, paint_text);
        canvas.drawText("06:00", P_width * 1 / 4f - textWidth / 2f, baseline + textHeight, paint_text);
        canvas.drawText("12:00", P_width * 2 / 4f - textWidth / 2f, baseline + textHeight, paint_text);
        canvas.drawText("18:00", P_width * 3 / 4f - textWidth / 2f, baseline + textHeight, paint_text);
        canvas.drawText("23:00", P_width - textWidth, baseline + textHeight, paint_text);

        if (noData) {
            int width = getTextWidth(paint_text, getResources().getString(R.string.no_data));
            canvas.drawText(getResources().getString(R.string.no_data), (P_width - width) / 2,
                    (P_height) / 2, paint_text);
        }

        // 柱子宽度=2倍间距
        if (aniProgress.length > 0 && aniProgress != null) {
            for (int i = 0; i < aniProgress.length; i++) {
                float value = aniProgress[i] / goal;
                if (value >= 1) {
                    value = 1.0f;
                } else if (value < 0.08 && value > 0) {
                    value = 0.05f;
                }

                RectF bg = new RectF();
                bg.left = 2 * spacing * i;
                bg.top = 0;
                bg.right = 2 * spacing * i + spacing;
                bg.bottom = baseline;
                canvas.drawRoundRect(bg, spacing, spacing, paint_step_bg);

                RectF rectF = new RectF();
                rectF.left = 2 * i * spacing;
                rectF.top = (1 - value) * baseline;
                rectF.right = 2 * i * spacing + spacing;
                rectF.bottom = baseline;
                canvas.drawRoundRect(rectF, spacing, spacing, paint_step);
            }
        }

        if (touchPos == -1.0f) {
            if (onSlidingListener != null) {
                onSlidingListener.SlidingDisOver(-1, -1, 0, 0);
            }
            return;
        }
        int dataBlockIndex = (int) ((touchPos) / (2 * spacing));

        if (progress == null) return;
        if (dataBlockIndex > progress.length - 1) {
            dataBlockIndex = progress.length - 1;
        }
        if (dataBlockIndex < 0) {
            dataBlockIndex = 0;
        }

        float value = progress[dataBlockIndex] / goal;
        if (value >= 1) {
            value = 1.0f;
        } else if (value < 0.08 && value > 0) {
            value = 0.05f;
        }

        int time = 0;
        int step = 0;
        if (progress_time != null) time = progress_time[dataBlockIndex];
        if (progress_step != null) step = progress_step[dataBlockIndex];

        if (step != 0) {

            RectF rectF = new RectF();
            rectF.left = 2 * spacing * dataBlockIndex;
            rectF.top = (1 - value) * baseline;
            rectF.right = 2 * spacing * dataBlockIndex + spacing;
            rectF.bottom = baseline;
            canvas.drawRoundRect(rectF, spacing, spacing, paint_step_touch);
        }

        if (onSlidingListener != null) {
            onSlidingListener.SlidingDisOver(progress[dataBlockIndex], dataBlockIndex, time, step);
        }

    }

    public void start(float[] progress, float goal, int type, int[] progress_time, int[] progress_step, boolean isgrid) {
        this.isgrid = isgrid;
        this.type = type;
        this.progress = progress;
        this.progress_time = progress_time;
        this.progress_step = progress_step;

        aniProgress = new float[progress.length];
        for (int i = 0; i < progress.length; i++) {
            aniProgress[i] = 0;
        }

        if (goal != 0) {
            this.goal = goal;
            switch (type) {
                case 2:
                    if (this.goal < 1000) this.goal = 1000;//卡
                    break;
                case 3:
                    if (this.goal < 1) this.goal = 1;//大卡
                    break;
            }
        } else {
            switch (type) {
                case 1:
                    this.goal = 100; // 步数默认目标
                    break;
                case 2:
                    this.goal = 100 * 1000;// 卡路里默认目标 （卡） ，显示（大卡）
                    break;
                case 3:
                    this.goal = 100;//大卡
                    break;
            }
        }

        noData = true;
        for (int i = 0; i < this.progress.length; i++) {
            if (progress[i] != 0) {
                noData = false;
            }
        }

        this.startAnimation(ani);
    }

    private class HistogramAnimation extends Animation {
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
                for (int i = 0; i < aniProgress.length; i++) {
//                    aniProgress[i] = (progress[i] * interpolatedTime);
                    aniProgress[i] = (progress[i]);
                }
            } else {
                for (int i = 0; i < aniProgress.length; i++) {
                    aniProgress[i] = progress[i];
                }
            }
            postInvalidate();
        }
    }

    private float touchPos = -1.0f;

    public void setTouchPos(float eventX) {
        touchPos = eventX;
    }

    private OnSlidingListener onSlidingListener;

    public void setOnSlidingListener(OnSlidingListener onSlidingListener) {
        this.onSlidingListener = onSlidingListener;
    }

    public interface OnSlidingListener {
        void SlidingDisOver(float data, int index, int time, int step);
    }

    private int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    private int dp2px(int value) {
        float v = getContext().getResources().getDisplayMetrics().density;
        return (int) (v * value + 0.5f);
    }

    private int sp2px(int value) {
        float v = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (v * value + 0.5f);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
}

package com.zjw.apps3pluspro.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.zjw.apps3pluspro.R;

/**
 * Created by android
 * on 2020/5/13.
 */
public class MultiProgressView extends View {
    private Paint paint_line;
    private viewAnimation ani;
    private float P_width;
    private float P_height;

    private float aniProgress;

    public MultiProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        ani = new viewAnimation();
        ani.setDuration(1000);

        paint_line = new Paint();
        paint_line.setStrokeWidth(dp2px(1));
        paint_line.setAntiAlias(true);
        paint_line.setStrokeCap(Paint.Cap.SQUARE);
        paint_line.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        P_width = getWidth();
        P_height = getHeight();

        float paint_StrokeWidth = P_height / 3;
        paint_line.setStrokeWidth(paint_StrokeWidth);


        if (progress1 == 0 && progress2 == 0 && progress3 == 0) {

            paint_line.setColor(getResources().getColor(R.color.multiProgressView4));
//            canvas.drawLine(start , P_height / 2, end, P_height / 2, paint_line);
        } else {
//            if (progress3 != 0) {
//                paint_line.setColor(getResources().getColor(R.color.multiProgressView1));
//                canvas.drawLine(progress1 * P_width + progress2 * P_width + paint_StrokeWidth / 2, P_height / 6, P_width, P_height / 6, paint_line);
//            }
//            if (progress2 != 0) {
//                paint_line.setColor(getResources().getColor(R.color.multiProgressView2));
//                canvas.drawLine(progress1 * P_width + paint_StrokeWidth / 2, P_height * 3 / 6,
//                        progress1 * P_width + progress2 * P_width - paint_StrokeWidth / 2, P_height * 3 / 6, paint_line);
//            }
//            if (progress1 != 0) {
//                paint_line.setColor(getResources().getColor(R.color.multiProgressView3));
//                canvas.drawLine(0, P_height * 5 / 6, progress1 * P_width - paint_StrokeWidth / 2, P_height * 5 / 6, paint_line);
//            }

            @SuppressLint("DrawAllocation")
            Rect rect1 = new Rect(0, (int) (P_height * 2 / 3), (int) (progress1 * P_width), (int) P_height);
            paint_line.setColor(getResources().getColor(R.color.multiProgressView3));
            canvas.drawRect(rect1, paint_line);

            @SuppressLint("DrawAllocation")
            Rect rect2 = new Rect((int) (progress1 * P_width), (int) (P_height * 1 / 3), (int) ((progress1 + progress2) * P_width), (int) (P_height * 2 / 3));
            paint_line.setColor(getResources().getColor(R.color.multiProgressView2));
            canvas.drawRect(rect2, paint_line);

            @SuppressLint("DrawAllocation")
            Rect rect3 = new Rect((int) ((progress1 + progress2) * P_width), (int) (P_height * 0 / 3), (int) (P_width), (int) (P_height * 1 / 3));
            paint_line.setColor(getResources().getColor(R.color.multiProgressView1));
            canvas.drawRect(rect3, paint_line);
        }

    }

    private float progress1, progress2, progress3;

    public void start(int progress1, int progress2, int progress3) {
        int total = progress1 + progress2 + progress3;
        if (progress3 == 0) {
            this.progress3 = 0;
        }
        if (progress1 == 0) {
            this.progress1 = 0;
        }
        if (progress2 == 0) {
            this.progress2 = 0;
        }
        if (total != 0) {
            this.progress1 = progress1 * 1.f / total;
            this.progress2 = progress2 * 1.f / total;
            this.progress3 = progress3 * 1.f / total;
        }

//        this.startAnimation(ani);
        invalidate();
    }


    private class viewAnimation extends Animation {
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
                aniProgress = interpolatedTime;
            } else {
                aniProgress = 1;
            }
            postInvalidate();
        }
    }

    private int dp2px(int value) {
        float v = getContext().getResources().getDisplayMetrics().density;
        return (int) (v * value + 0.5f);
    }
}

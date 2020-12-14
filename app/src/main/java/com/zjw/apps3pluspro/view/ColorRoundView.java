package com.zjw.apps3pluspro.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.zjw.apps3pluspro.R;

public class ColorRoundView extends View {
    private static final int DEFAULT__COLOR = Color.argb(235, 0, 0, 0);
    private int bgColor, ringColor;
    private Paint paintBg, paintRing, paintClick;
    private boolean click = false;
    private float padding = 20;

    public ColorRoundView(Context context) {
        super(context);
    }

    public ColorRoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray tArray = context.obtainStyledAttributes(attrs, R.styleable.colorRoundAttr);
        bgColor = tArray.getColor(R.styleable.colorRoundAttr_bgColor, DEFAULT__COLOR);
        ringColor = tArray.getColor(R.styleable.colorRoundAttr_ringColor, DEFAULT__COLOR);
        init();
    }

    @SuppressLint("CustomViewStyleable")
    public ColorRoundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private void init() {
        paintBg = new Paint();
        paintBg.setAntiAlias(true);
        paintBg.setStrokeCap(Paint.Cap.ROUND);
        paintBg.setStyle(Paint.Style.FILL);
        paintBg.setColor(bgColor);

        paintRing = new Paint();
        paintRing.setAntiAlias(true);
        paintRing.setStrokeCap(Paint.Cap.ROUND);
        paintRing.setStyle(Paint.Style.STROKE);
        paintRing.setColor(ringColor);
        paintRing.setStrokeWidth(2);

        paintClick = new Paint();
        paintClick.setAntiAlias(true);
        paintClick.setStrokeCap(Paint.Cap.ROUND);
        paintClick.setStyle(Paint.Style.STROKE);
        paintClick.setColor(getResources().getColor(R.color.my_color_view_poheart_details_color1));
        paintClick.setStrokeWidth(4);

    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float mWidth = getWidth();
        float mHeight = getHeight();
        float radius = mHeight / 2;
        if (mHeight > mWidth) {
            radius = mWidth / 2;
        }

        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, radius - padding, paintBg);

        RectF rectRing = new RectF(getWidth() / 2f - (radius - padding), getHeight() / 2f - (radius - padding),
                getWidth() / 2f + ((radius - padding)), getHeight() / 2f + (radius - padding));
        canvas.drawArc(rectRing, 0, 360, false, paintRing);

        if (click) {
            RectF rectF = new RectF(getWidth() / 2f - radius + paintClick.getStrokeWidth(), getHeight() / 2f - radius + paintClick.getStrokeWidth(),
                    getWidth() / 2f + radius - paintClick.getStrokeWidth(), getHeight() / 2f + radius - paintClick.getStrokeWidth());
            canvas.drawArc(rectF, 0, 360, false, paintClick);
        }

    }

    public void setClick(boolean isClick) {
        this.click = isClick;
        invalidate();
    }

    public void setBgColor(int bgColor, int ringColor) {
        paintBg.setColor(getResources().getColor(bgColor));
        paintRing.setColor(getResources().getColor(ringColor));
        this.bgColor = getResources().getColor(bgColor);
        invalidate();
    }

    public int getcolor() {
        return bgColor;
    }
}

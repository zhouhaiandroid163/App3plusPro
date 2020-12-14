package com.zjw.apps3pluspro.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.utils.FontsUtils;
import com.zjw.apps3pluspro.utils.ImageUtil;

public class TargetCircleBar extends View {

    private int YuanHuanWidth = 8;//圆环宽度(单位是dp)
    private int YuanHuanTextSize = 0;//圆环字体大小(单位是dp)

    private RectF mColorWheelRectangle = new RectF();
    private Paint mColorWheelPaint;// 圆环的彩色画笔
    private Paint mColorWheelPaintCentre;// 圆环中心的默认的灰色画笔
    private Paint mTextnum;// 设置指数的数字画笔
    private float circleStrokeWidth;// 圆弧的宽度
    private float mSweepAnglePer; // 主要是绘制刷新数据的。
    private int stepnumber, stepnumbernow;
    private float pressExtraStrokeWidth;// 圆弧离矩形的距离
    private BarAnimation anim;
    private int stepnumbermax;// 默认最大步数
    private float stepnumber_y;

    public TargetCircleBar(Context context) {
        super(context);
        init(null, 0);
    }

    public TargetCircleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TargetCircleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        // 这个是设置中间进度的颜色画笔。
        mColorWheelPaint = new Paint();// 147 112 219
        mColorWheelPaint.setStyle(Paint.Style.STROKE);// 空心
        mColorWheelPaint.setStrokeCap(Paint.Cap.ROUND);// 圆角画笔
        mColorWheelPaint.setAntiAlias(true);// 去锯齿
        setColorWheelPaint(R.color.my_color_yuanhuan_color);//填充颜色
        // 这个是设置圆环整体的颜色，默认的状态是灰色画笔。
        mColorWheelPaintCentre = new Paint();
        setColorWheelPaintCentre(R.color.my_color_yuanhuan_bg);//底色
        mColorWheelPaintCentre.setStyle(Paint.Style.STROKE);
        mColorWheelPaintCentre.setStrokeCap(Paint.Cap.ROUND);
        mColorWheelPaintCentre.setAntiAlias(true);

        // 设置指数的数字画笔
        mTextnum = new Paint();
        mTextnum.setAntiAlias(true);
        anim = new BarAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawArc(mColorWheelRectangle, 0, 359, false,
                mColorWheelPaintCentre);
        canvas.drawArc(mColorWheelRectangle, 90, mSweepAnglePer, false,
                mColorWheelPaint);
//		LogUtil.i("步数的值和目标值为:",stepnumbernow+":"+stepnumbermax);

        if (stepnumbermax != 0) {
//			LogUtil.i("步数和目标的比值为：",(int)(((float)stepnumbernow/stepnumbermax)*100)+"%");
            canvas.drawText((int) (((float) stepnumbernow / stepnumbermax) * 100) + "%", mColorWheelRectangle.centerX()
                            - (mTextnum.measureText(String.valueOf(stepnumbernow)) / 2),
                    stepnumber_y, mTextnum);
        }


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getDefaultSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int min = Math.min(width, height);// 获取View最短边的长度
        setMeasuredDimension(min, min);// 强制改View为以最短边为长度的正方形
        circleStrokeWidth = Textscale(ImageUtil.dip2px(YuanHuanWidth), min);// 圆弧的宽度
        pressExtraStrokeWidth = Textscale(2, min);// 圆弧离矩形的距离
        mColorWheelRectangle.set(circleStrokeWidth + pressExtraStrokeWidth,
                circleStrokeWidth + pressExtraStrokeWidth, min
                        - circleStrokeWidth - pressExtraStrokeWidth, min
                        - circleStrokeWidth - pressExtraStrokeWidth);// 设置矩形
        mTextnum.setTextSize(Textscale(ImageUtil.sp2px(YuanHuanTextSize, getContext()), min));//进度条字体大小
        mTextnum.setTypeface(FontsUtils.modefyNumber(getContext()));
        setNumberColor(R.color.my_color_yuanhuan_text);//字体颜色
        stepnumber_y = Textscale(300, min);
        mColorWheelPaint.setStrokeWidth(circleStrokeWidth);
        mColorWheelPaintCentre.setStrokeWidth(circleStrokeWidth);
    }

    /**
     * 进度条动画
     *
     * @author Administrator
     */
    public class BarAnimation extends Animation {
        public BarAnimation() {

        }

        /**
         * 每次系统调用这个方法时， 改变mSweepAnglePer，mPercent，stepnumbernow的值，
         * 然后调用postInvalidate()不停的绘制view。
         */
        @Override
        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
                mSweepAnglePer = interpolatedTime * stepnumber * 360
                        / stepnumbermax;
                stepnumbernow = (int) (interpolatedTime * stepnumber);
            } else {
                mSweepAnglePer = stepnumber * 360 / stepnumbermax;
                stepnumbernow = stepnumber;
            }
            postInvalidate();
        }
    }

    /**
     * 根据控件的大小改变绝对位置的比例
     *
     * @param n
     * @param m
     * @return
     */
    public float Textscale(float n, float m) {
        return n / 500 * m;
    }

    /**
     * 更新步数和设置一圈动画时间
     *
     * @param stepnumber
     * @param time
     */
    public void update(int stepnumber, int time) {
        this.stepnumber = stepnumber;
        anim.setDuration(time);
        this.startAnimation(anim);
    }

    /**
     * 设置指数的最大范围
     *
     * @param Maxstepnumber
     */
    public void setMaxstepnumber(int Maxstepnumber) {
        stepnumbermax = Maxstepnumber;
    }

    /**
     * 设置动画时间
     *
     * @param time
     */
    public void setAnimationTime(int time) {
        anim.setDuration(time * stepnumber / stepnumbermax);// 按照比例设置动画执行时间
    }

    //设置字体颜色
    public void setNumberColor(int colorId) {
        mTextnum.setColor(this.getResources().getColor(colorId));
    }

    //圆环底色
    public void setColorWheelPaintCentre(int colorId) {
        mColorWheelPaintCentre.setColor(this.getResources().getColor(colorId));
    }

    //圆环填充颜色
    public void setColorWheelPaint(int colorId) {
        mColorWheelPaint.setColor(this.getResources().getColor(colorId));
    }


}

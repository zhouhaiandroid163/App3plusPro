package com.zjw.apps3pluspro.view.ecg;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Handler;
import android.os.Looper;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.zjw.apps3pluspro.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author maoning 2016/4/12
 */
public class ECGAllView extends View {

    private final String TAG = ECGAllView.class.getSimpleName();

    //左边线的宽度
    int bg_line_left_size = 5;
    int bg_line_bootom_size = 5;
    int bg_line_top_size = 5;
    int bg_line_right_size = 5;
    //把线加长 - 补偿
    int bg_line_compensation = 2;

    private final static String X_KEY = "Xpos";
    private final static String Y_KEY = "Ypos";
    private final static int LOW_ALARM = 0;
    private final static int HIGH_ALARM = 1;
    private int _backLineColor;
    private int _titleColor;
    private int _pointerLineColor;
    private int _titleSize;
    private int _XYTextSize;

    // X轴大小
    private int _PointMaxAmount;

    private float _XUnitLength;
    // 褰撳墠鍔犲叆鐐�
    private int _CurP = 0;
    private int _RemovedPointNum = 0;
    private int _EveryNPointBold = 1;
    //是否先画背景
    private Boolean _isfristDrawBackGround = true;
    // 缩进
    private int _LeftIndent = 0;
    private int _RightIndent = 0;
    private int _BottomIndent = 0;
    private int _TopIndent = 0;
    private float _CurX = _LeftIndent + 1;
    private float _CurY = _TopIndent;
    // 一次画几个点？
    private int _EveryNPointRefresh = 1;
    private float _MaxYNumber;
    private int _Height;
    private int _Width;
    private float _EffectiveHeight = 1;// 有效高度
    private float _EffectiveWidth = 1;// 有效宽度
    private float _EveryOneValue = 1;// 每个小格子占多少像素
    private int _LatticeWidth = 1;//格子宽度
    private List<Map<String, Float>> _ListPoint = new ArrayList<Map<String, Float>>();
    private List<Float> _ListVLine = new ArrayList<Float>();
    private List<Float> _ListHLine = new ArrayList<Float>();
    private Paint _PaintLine;
    private Paint _PaintDataLine;
    private TextPaint _TitleTextPaint;
    private TextPaint _XYTextPaint;
    private String title = "abc";
    private int _YSize;
    private int _XSize;
    private Context _context;
    private String _lowAlarmMsg;// 达到限定的低值
    private String _highAlarmMsg;// 达到限定的高值
    private int _maxAlarmNumber;// 最高报警值
    private int _minAlarmNumber;// 最低报警值
    private boolean isSetAlarmFlag = false;
    private Handler _handler;

    public ECGAllView(Context context, AttributeSet attrs,
                      int defStyle) {
        super(context, attrs, defStyle);
        _context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.elg);

        //背景线条y
        _backLineColor = typedArray.getColor(R.styleable.elg_BackLineColor,
                Color.GREEN);
        _titleColor = typedArray
                .getColor(R.styleable.elg_TitleColor, Color.RED);
        _pointerLineColor = typedArray.getColor(
                R.styleable.elg_PointerLineColor, Color.WHITE);
        _titleSize = typedArray.getDimensionPixelSize(
                R.styleable.elg_TitleSize, 30);
        _XYTextSize = typedArray.getDimensionPixelSize(
                R.styleable.elg_XYTextSize, 20);
        typedArray.recycle();
        initView();
    }

    public ECGAllView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.elg);
        _backLineColor = typedArray.getColor(R.styleable.elg_BackLineColor,
                Color.GREEN);
        _titleColor = typedArray
                .getColor(R.styleable.elg_TitleColor, Color.RED);
        _pointerLineColor = typedArray.getColor(
                R.styleable.elg_PointerLineColor, Color.WHITE);
        _titleSize = typedArray.getDimensionPixelSize(
                R.styleable.elg_TitleSize, 30);
        _XYTextSize = typedArray.getDimensionPixelSize(
                R.styleable.elg_XYTextSize, 20);
        typedArray.recycle();
        initView();
    }

    public ECGAllView(Context context) {
        this(context, null);
        _context = context;
        initView();
    }

    private void initView() {
        _handler = new Handler(Looper.getMainLooper());
        _PaintLine = new Paint();
        //线的大小
        _PaintLine.setStrokeWidth(2.5f);
        _PaintLine.setColor(_pointerLineColor);
        _PaintLine.setAntiAlias(true);
        _PaintDataLine = new Paint();
        _PaintDataLine.setColor(_backLineColor);
        _PaintDataLine.setAntiAlias(true);
        _PaintDataLine.setStrokeWidth(10);
        _XYTextPaint = new TextPaint();
        _XYTextPaint.setColor(_titleColor);
        _XYTextPaint.setTextSize(_XYTextSize);
        _TitleTextPaint = new TextPaint();
        _TitleTextPaint.setColor(_titleColor);
        _TitleTextPaint.setTextSize(_titleSize);
    }


    private boolean isAaa = true;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (_isfristDrawBackGround) {
            _Height = getHeight();
            _Width = getWidth();
        }
        _EffectiveHeight = _Height - _TopIndent - _BottomIndent;
        _EffectiveWidth = _Width - _RightIndent - _LeftIndent;
        _XUnitLength = (_EffectiveWidth) / (_PointMaxAmount - 1);// 涓ゆ潯绾夸箣闂寸殑闂磋窛绛変簬瀹藉噺鍑哄乏鍙崇缉杩涢櫎浠ョ偣锟�?1

        if (isAaa) {
            drawBackground(canvas);
            isAaa = false;
        }

        drawWave(canvas);
    }

    private int ecgViewXzhou = 2;
    private int ecgViewYzhou = 2;

    /**
     * 画背景的线条
     *
     * @param canvas
     */
    public void drawBackground(Canvas canvas) {
        if (_isfristDrawBackGround) {
            _YSize = (int) (_MaxYNumber / _EveryOneValue);// 鍨傜洿鏍煎瓙鏁伴噺
            _LatticeWidth = (int) (_EffectiveHeight / _YSize);
            _XSize = (_Width - _RightIndent - _LeftIndent) / _LatticeWidth;// 姘村钩鏍煎瓙鏁伴噺
            float curX = 0;
            if (_EveryNPointBold > _YSize || _EveryNPointBold > _XSize) {
                _EveryNPointBold = Math.min(_YSize, _XSize) / 2 + 1;
            }
            for (int i = 0; i < _XSize; i++) {
                _ListVLine.add(curX);
                curX += _LatticeWidth;
            }
            float curY = 0;
            for (int j = 0; j < _YSize; j++) {
                _ListHLine.add(curY);
                curY += _LatticeWidth;
            }
            _isfristDrawBackGround = false;
        }
        _PaintDataLine.setStrokeWidth(1);
        int sText = 5;
        for (int i = 0; i < _ListVLine.size(); i++) {
            sText = 5 * i;
            canvas.drawText(sText + "", _ListVLine.get(i) + _TopIndent, _Height
                    - _TopIndent + _XYTextSize, _XYTextPaint);
            if (i == 0) {
                _PaintDataLine.setStrokeWidth(bg_line_left_size);

                canvas.drawLine(_ListVLine.get(i) + _LeftIndent,
                        0 + _TopIndent - bg_line_compensation, _ListVLine.get(i) + _LeftIndent,
                        _Height - _BottomIndent + bg_line_compensation, _PaintDataLine);
                _PaintDataLine.setStrokeWidth(1);
            } else {
                if (i % _EveryNPointBold == 0) {
                    _PaintDataLine.setStrokeWidth(ecgViewYzhou);
                    canvas.drawLine(_ListVLine.get(i) + _LeftIndent,
                            0 + _TopIndent, _ListVLine.get(i) + _LeftIndent,
                            _Height - _BottomIndent, _PaintDataLine);
                    _PaintDataLine.setStrokeWidth(1);
                } else {
                    canvas.drawLine(_ListVLine.get(i) + _LeftIndent,
                            0 + _TopIndent, _ListVLine.get(i) + _LeftIndent,
                            _Height - _BottomIndent, _PaintDataLine);
                }
            }
        }
        _PaintDataLine.setStrokeWidth(bg_line_bootom_size);
        canvas.drawLine(0 + _LeftIndent, _Height - _TopIndent, _Width
                - _RightIndent, _Height - _BottomIndent, _PaintDataLine);
        _PaintDataLine.setStrokeWidth(1);
        String sYText = "";
        for (int i = 0; i < _ListHLine.size(); i++) {
            if (i == 0) {
                sYText = (int) _EveryOneValue * (_YSize - i) + "";
                canvas.drawText(sYText, _LeftIndent - _XYTextSize * 3,
                        _ListHLine.get(i) + _TopIndent, _XYTextPaint);
                _PaintDataLine.setStrokeWidth(bg_line_top_size);
                canvas.drawLine(0 + _LeftIndent,
                        _ListHLine.get(i) + _TopIndent, _Width - _RightIndent,
                        _ListHLine.get(i) + _BottomIndent, _PaintDataLine);
                _PaintDataLine.setStrokeWidth(1);
            } else {
                if (i % _EveryNPointBold == 0) {
                    sYText = (int) _EveryOneValue * (_YSize - i) + "";
                    canvas.drawText(sYText, _LeftIndent - _XYTextSize * 3,
                            _ListHLine.get(i) + _TopIndent, _XYTextPaint);
                    _PaintDataLine.setStrokeWidth(ecgViewXzhou);
                    canvas.drawLine(0 + _LeftIndent, _ListHLine.get(i)
                                    + _TopIndent, _Width - _RightIndent,
                            _ListHLine.get(i) + _BottomIndent, _PaintDataLine);
                    _PaintDataLine.setStrokeWidth(1);
                } else {
                    canvas.drawLine(0 + _LeftIndent, _ListHLine.get(i)
                                    + _TopIndent, _Width - _RightIndent,
                            _ListHLine.get(i) + _BottomIndent, _PaintDataLine);
                }
            }
        }
        canvas.drawText(title, _Width / 2 - 100, _TopIndent / 2,
                _TitleTextPaint);
        _PaintDataLine.setStrokeWidth(bg_line_right_size);
        canvas.drawLine(_Width - _RightIndent, 0 + _TopIndent - bg_line_compensation, _Width
                - _RightIndent, _Height - _BottomIndent + bg_line_compensation, _PaintDataLine);
    }

    /**
     * 画折线
     *
     * @param canvas
     */
    public void drawWave(Canvas canvas) {
        for (int index = 0; index < _ListPoint.size(); index++) {
            if (_ListPoint.size() == _PointMaxAmount
                    && (index >= _CurP && index < _CurP + _RemovedPointNum)) {
                continue;
            }
            if (index > 0) {
                if (_ListPoint.get(index).get(Y_KEY) < 0
                        || _ListPoint.get(index).get(Y_KEY) < _TopIndent) {
                    continue;
                }


                float a = _ListPoint.get(index - 1).get(X_KEY);
                float b = _ListPoint.get(index - 1).get(Y_KEY);
                float c = _ListPoint.get(index).get(X_KEY);
                float d = _ListPoint.get(index).get(Y_KEY);

                if (c == 0) {

                    b = d;
                    a = c;

                }

                canvas.drawLine(a,
                        b,
                        c,
                        d,
                        _PaintLine);

//                if(c==0)
//                {
//                    MyLog.i(TAG,"画图ECG "
//                            +"  A = " + _ListPoint.get(index - 1).get(X_KEY)
//                            +"  B = " +   _ListPoint.get(index - 1).get(Y_KEY)
//                            +"  C = " +  _ListPoint.get(index).get(X_KEY)
//                            +"  D = " + _ListPoint.get(index) .get(Y_KEY)
//                    );
//                }


                canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
                        Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            }
        }
    }

    /**
     * 调用画图
     *
     * @param curY
     */
    public void setLinePoint(float curY) {
        //new Map 存入
        Map<String, Float> temp = new HashMap<String, Float>();
        //存入一个X
        temp.put(X_KEY, _CurX);
        _CurX += _XUnitLength;
        float number = curY / _EveryOneValue;
        if (_Height != 0) {
            _CurY = _Height - (_BottomIndent + number * _LatticeWidth);
        }
        if (_CurY < _TopIndent) {
            _CurY = _TopIndent + 10;
        }
        //存入一个Y
        temp.put(Y_KEY, _CurY);

        if (_CurP < _PointMaxAmount) {
            try {
                if (_ListPoint.size() == _PointMaxAmount
                        && _ListPoint.get(_CurP) != null) {
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


//            _ListPoint.add(_CurP, temp);
            _ListPoint.add(temp);
            _CurP++;
        } else {
            _CurP = 0;
            _CurX = _RightIndent;
        }


        if (_CurP % _EveryNPointRefresh == 0) {
            invalidate();
        }
        if (isSetAlarmFlag) {
            if (!(curY > _minAlarmNumber && curY < _maxAlarmNumber)) {
                _handler.post(new alarmThread(
                        curY < _minAlarmNumber ? LOW_ALARM : HIGH_ALARM));
            }

        }
    }

    /**
     * 擦除的宽度
     *
     * @param removedPointNum
     */
    public void setRemovedPointNum(int removedPointNum) {
        _RemovedPointNum = removedPointNum;
    }

    /**
     * 一次画几个点？
     *
     * @param num
     */
    public void setEveryNPointRefresh(int num) {
        _EveryNPointRefresh = num;
    }

    //获取当前X点的大小
    public float getCurrentPointX() {
        return _CurX;
    }

    //获取当前Y点的大小
    public float getCurrentPointY() {
        return _CurY;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    /**
     * 设置X轴最大点
     *
     * @param i
     */
    public void setMaxPointAmount(int i) {
        _PointMaxAmount = i;
    }

    /**
     * 小格子是 几乘几
     */
    public void setEveryNPoint(int everyNPointBold) {
        if (everyNPointBold < 0) {
            return;
        }
        _EveryNPointBold = everyNPointBold;
    }

    /**
     * // 设置Y轴最大值
     *
     * @param maxYNumber
     */
    public void setMaxYNumber(float maxYNumber) {
        this._MaxYNumber = maxYNumber;
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 每个小格子占多少像素
     *
     * @param value
     */
    public void setEffticeValue(int value) {
        _EveryOneValue = value;
    }

    /**
     * // 达到一定的值，报警
     *
     * @param maxAlarmNumber
     * @param minAlarmNumber
     * @param lowAlarmMsg
     * @param highAlarmMsg
     */
    public void setAlarmMessage(int maxAlarmNumber, int minAlarmNumber,
                                String lowAlarmMsg, String highAlarmMsg) {
        isSetAlarmFlag = true;
        _maxAlarmNumber = maxAlarmNumber;
        _minAlarmNumber = minAlarmNumber;
        _lowAlarmMsg = lowAlarmMsg;
        _highAlarmMsg = highAlarmMsg;
    }

    //警报
    class alarmThread implements Runnable {
        int _flag;

        alarmThread(int flag) {
            _flag = flag;
        }

        @Override
        public void run() {
            Toast.makeText(_context, _flag == LOW_ALARM ? _lowAlarmMsg : _highAlarmMsg, Toast.LENGTH_SHORT).show();


        }

    }


}

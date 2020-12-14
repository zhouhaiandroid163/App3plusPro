package com.zjw.apps3pluspro.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by android
 * on 2020/11/7
 */
public class GoogleMapView extends RelativeLayout {


    public GoogleMapView(Context context) {
        super(context);
    }

    public GoogleMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GoogleMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
}

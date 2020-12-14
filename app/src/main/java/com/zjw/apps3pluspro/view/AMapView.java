package com.zjw.apps3pluspro.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.amap.api.maps.AMapOptions;

/**
 * Created by android
 * on 2020/11/7
 */
public class AMapView extends com.amap.api.maps.MapView{
    public AMapView(Context context) {
        super(context);
    }

    public AMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public AMapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public AMapView(Context context, AMapOptions aMapOptions) {
        super(context, aMapOptions);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
}

package com.zjw.apps3pluspro.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;

/**
 * Created by android
 * on 2020/10/10
 */
public class GlideUtils {
    public static Bitmap getBitMapByService(Context context, String url, int width, int height) {
        Bitmap myBitmap = null;
        try {
            myBitmap = Glide.with(context)
                    .load(url)
                    .asBitmap() //必须
                    .into(width, height)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myBitmap;
    }
}

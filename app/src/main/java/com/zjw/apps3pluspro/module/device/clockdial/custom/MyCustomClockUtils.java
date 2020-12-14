package com.zjw.apps3pluspro.module.device.clockdial.custom;

import android.graphics.Bitmap;
import android.graphics.Color;


public class MyCustomClockUtils {


    public static Bitmap getCustomBEffectImg(byte[] source_data, int color_R, int color_G, int color_B, Bitmap bg_bmp, Bitmap text_bitmp) {
        Bitmap new_text_bitmap = getNewTextBitmap(text_bitmp, color_R, color_G, color_B);
        return CustomClockSubUtils.combineBitmap(bg_bmp, new_text_bitmap, 0, 0);
    }

    //反向-低位在前，高位在后
    public static Bitmap getNewTextBitmap(Bitmap input_bitmap, int r, int g, int b) {
        int width = input_bitmap.getWidth();
        int height = input_bitmap.getHeight();
        Bitmap out_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int color = input_bitmap.getPixel(j, i);
                int a = Color.alpha(color);
                int color_output = Color.argb(a, r, g, b);
                out_bitmap.setPixel(j, i, color_output);
            }
        }
        return out_bitmap;
    }


}

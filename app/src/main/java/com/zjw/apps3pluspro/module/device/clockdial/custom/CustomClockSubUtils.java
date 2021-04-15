package com.zjw.apps3pluspro.module.device.clockdial.custom;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;

public class CustomClockSubUtils {
    private static final String TAG = CustomClockSubUtils.class.getSimpleName();

    public static Bitmap getHorBitmap(Bitmap background, byte[] a_data_list, int r, int g, int b, int w, int h, int x, int y) {
        Bitmap foreground = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        int pos = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int a = a_data_list[pos] & 0xff;
                int color = Color.argb(a, r, g, b);
                foreground.setPixel(j, i, color);
                pos++;
            }
        }
        if (background == null) {
            return null;
        }
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        Bitmap newmap = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newmap);
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawBitmap(foreground, x, y, null);
        canvas.save();
        canvas.restore();
        int w_1 = newmap.getWidth();
        int h_1 = newmap.getHeight();
        if (w_1 < w || h_1 < w) {
            w = w_1 > h_1 ? h_1 : w_1;
        }
        return Bitmap.createBitmap(newmap, x, y, w, h, null, false);
    }

    public static byte[] getbitmapByte(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        byte[] result = new byte[width * height * 3];
        int abc = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int color = bitmap.getPixel(j, i);
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);

                result[abc] = (byte) r;
                abc += 1;
                result[abc] = (byte) g;
                abc += 1;
                result[abc] = (byte) b;
                abc += 1;
            }
        }
        result = bmp_24_16(result);
        return result;
    }


    public static byte[] bmp_24_16(byte[] byt) {
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

        byte[] retu = new byte[byt.length / 3 * 2];
        int abc = 0;
        for (int i = 0; i < byt.length; i += 3) {
            byte[] rgb16 = null;

            //木兰表盘规则
            if (mBleDeviceTools.getClockDialGenerationMode() == 1) {
                //木兰-规则1
                if (mBleDeviceTools.getClockDialMuLanVersion() == 1) {
                    //正向
                    rgb16 = RGB24TORGB16Forward(byt[i], byt[i + 1], byt[i + 2]);
                }
                //木兰-老规则
                else {
                    //反向
                    if (mBleDeviceTools.getClockDialDataFormat() == 1) {
                        rgb16 = RGB24TORGB16Reverse(byt[i], byt[i + 1], byt[i + 2]);
                    }
                    //正向
                    else {
                        rgb16 = RGB24TORGB16Forward(byt[i], byt[i + 1], byt[i + 2]);
                    }
                }
            }
            //舟海表盘规则
            else {
                //反向
                if (mBleDeviceTools.getClockDialDataFormat() == 1) {
                    rgb16 = RGB24TORGB16Reverse(byt[i], byt[i + 1], byt[i + 2]);
                }
                //正向
                else {
                    rgb16 = RGB24TORGB16Forward(byt[i], byt[i + 1], byt[i + 2]);
                }
            }

            retu[abc] = rgb16[0];
            abc += 1;
            retu[abc] = rgb16[1];
            abc += 1;
        }
        return retu;
    }

    //正向
    public static byte[] RGB24TORGB16Forward(byte bmp_r, byte bmp_g, byte bmp_b) {
        byte result_r = (byte) (bmp_r >> 3 & 0x1f);
        byte result_g = (byte) (bmp_g >> 2 & 0x3f);
        byte result_b = (byte) (bmp_b >> 3 & 0x1f);
        byte[] result = new byte[2];
        result[0] = (byte) ((result_r << 3 & 0xf8) | (result_g >> 3 & 0x1f));
        result[1] = (byte) ((result_g << 5 & 0xe0) | result_b);
        return result;
    }

    //反向
    public static byte[] RGB24TORGB16Reverse(byte bmp_r, byte bmp_g, byte bmp_b) {
        byte result_r = (byte) (bmp_r >> 3 & 0x1f);
        byte result_g = (byte) (bmp_g >> 2 & 0x3f);
        byte result_b = (byte) (bmp_b >> 3 & 0x1f);
        byte[] result = new byte[2];
        result[1] = (byte) ((result_r << 3 & 0xf8) | (result_g >> 3 & 0x1f));
        result[0] = (byte) ((result_g << 5 & 0xe0) | result_b);
        return result;
    }

    public static int getNumData(byte[] byteData) {
        int result = 0;
        if (byteData.length == 1) {
            result = byteData[0] & 0xff;
        } else if (byteData.length == 2) {
            result = (byteData[0] & 0xff) << 8 | byteData[1] & 0xff;
        } else if (byteData.length == 3) {
            result = (byteData[0] & 0xffff) << 16 | (byteData[1] & 0xff) << 8 | byteData[2] & 0xff;
        } else if (byteData.length == 4) {
            result = (byteData[0] & 0xffffff) << 24 | (byteData[1] & 0xffff) << 16 | (byteData[2] & 0xff) << 8 | byteData[3] & 0xff;
        }
        return result;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    public static int getMyData(byte[] src, int begin, int count) {
        byte[] data = subBytes(src, begin, count);
        return getNumData(data);
    }


    public static Bitmap combineBitmap(Bitmap background, Bitmap foreground, int x, int y) {
        if (background == null) {
            return null;
        }
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        Bitmap newmap = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newmap);
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawBitmap(foreground, x, y, null);
        canvas.save();
        canvas.restore();
        return newmap;
    }

    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }


}

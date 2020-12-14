package com.zjw.apps3pluspro.module.device.clockdial;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.utils.BinUtils;
import com.zjw.apps3pluspro.utils.BmpUtils;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.ThemeUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义表盘数据处理
 */
public class CustomClockDialUtils {

    private final static String TAG = "CustomClockDialUtils";

    private static int O_HEAD_LEN = 416;
    private static int N_HEAD_ITEM_LEN = 17;
    private static int N_HEAD_SEEK_LEN = 1;


    public static byte[] getCustonClockDialData(Context context, String fileName, int color_R, int color_G, int color_B, Bitmap bg_bmp) {

        //黑色需要处理
        if (color_R < 8) {
            color_R = 8;
        }
        if (color_G < 8) {
            color_G = 8;
        }
        if (color_B < 8) {
            color_B = 8;
        }


        String file_path = Constants.ASSETS_CUSTOM_DIAL_DIR + fileName;
        MyLog.i(TAG, "file_path = " + file_path);

        byte[] byte_data = getAssetBytes(context, file_path);
        MyLog.i(TAG, "byte_data = " + byte_data.length);

        List<byte[]> byte_new_data_list = new ArrayList<>();

        int byte_data_len = byte_data.length;
        MyLog.i(TAG, "byte_data_len = " + byte_data_len);

        //数据长度
        int data_len = byte_data[O_HEAD_LEN] & 0xff;
        MyLog.i(TAG, "data_len = " + data_len);


        //背景长度
        byte[] bg_data = BmpUtils.getbitmapByte(bg_bmp);
        MyLog.i(TAG, "bg_data_len = " + bg_data.length);

        byte[] old_head = ThemeUtils.subBytes(byte_data, 0, O_HEAD_LEN);
        MyLog.i(TAG, "old_head_len = " + old_head.length);

        //拼接-头
        byte_new_data_list.add(old_head);
        //拼接-背景
        byte_new_data_list.add(bg_data);


        int size = 0;

        for (int i = 0; i < data_len; i++) {

//            MyLog.i(TAG, "===== i = " + i + "  ======================");

            int start_pos = O_HEAD_LEN + i * N_HEAD_ITEM_LEN + N_HEAD_SEEK_LEN;
//            MyLog.i(TAG, "start_pos=  " + start_pos);

            byte[] new_data = getNewColorData(color_R, color_G, color_B, byte_data, bg_bmp, start_pos);

            if (new_data != null) {
//                MyLog.i(TAG, "有数据");
                //拼接-数据
                byte_new_data_list.add(new_data);
                size += new_data.length;
            } else {
//                MyLog.i(TAG, "没有数据");
            }

//            MyLog.i(TAG, "size = " + size);

        }


        byte[] end_data = BinUtils.byteMergerList(byte_new_data_list);
        MyLog.i(TAG, "end_data_len = " + end_data.length);

        return end_data;


    }


    public static byte[] getCustonClockDialDataByFile(Context context, String fileName, int color_R, int color_G, int color_B, Bitmap bg_bmp) {

        //黑色需要处理
        if (color_R < 8) {
            color_R = 8;
        }
        if (color_G < 8) {
            color_G = 8;
        }
        if (color_B < 8) {
            color_B = 8;
        }


        String file_path = Constants.DOWN_THEME_FILE + fileName;
        MyLog.i(TAG, "file_path = " + file_path);

        byte[] byte_data = ThemeUtils.getBytes(file_path);
        MyLog.i(TAG, "byte_data = " + byte_data.length);

        List<byte[]> byte_new_data_list = new ArrayList<>();

        int byte_data_len = byte_data.length;
        MyLog.i(TAG, "byte_data_len = " + byte_data_len);

        //数据长度
        int data_len = byte_data[O_HEAD_LEN] & 0xff;
        MyLog.i(TAG, "data_len = " + data_len);


        //背景长度
        byte[] bg_data = BmpUtils.getbitmapByte(bg_bmp);
        MyLog.i(TAG, "bg_data_len = " + bg_data.length);

        byte[] old_head = ThemeUtils.subBytes(byte_data, 0, O_HEAD_LEN);
        MyLog.i(TAG, "old_head_len = " + old_head.length);

        //拼接-头
        byte_new_data_list.add(old_head);
        //拼接-背景
        byte_new_data_list.add(bg_data);


        int size = 0;

        for (int i = 0; i < data_len; i++) {

//            MyLog.i(TAG, "===== i = " + i + "  ======================");

            int start_pos = O_HEAD_LEN + i * N_HEAD_ITEM_LEN + N_HEAD_SEEK_LEN;
//            MyLog.i(TAG, "start_pos=  " + start_pos);

            byte[] new_data = getNewColorData(color_R, color_G, color_B, byte_data, bg_bmp, start_pos);

            if (new_data != null) {
//                MyLog.i(TAG, "有数据");
                //拼接-数据
                byte_new_data_list.add(new_data);
                size += new_data.length;
            } else {
//                MyLog.i(TAG, "没有数据");
            }

//            MyLog.i(TAG, "size = " + size);

        }


        byte[] end_data = BinUtils.byteMergerList(byte_new_data_list);
        MyLog.i(TAG, "end_data_len = " + end_data.length);

        return end_data;


    }


    /**
     * 获取Asset文件-bin文件
     */
    private static byte[] getAssetBytes(Context context, String fileName) {
        byte[] buffer = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            buffer = new byte[size];
            is.read(buffer);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 获取Asset文件-图片
     */
    public static Bitmap getAssetBitmap(Context context, String fileName) {
        Bitmap bitmap = null;
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open(fileName);//filename是assets目录下的图片名
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    private static byte[] getNewColorData(int color_R, int color_G, int color_B, byte[] byte_data, Bitmap bg_bitmap, int start_pos) {


        List<byte[]> byte_color_data_list = new ArrayList<>();

        byte[] now_data = ThemeUtils.subBytes(byte_data, start_pos, N_HEAD_ITEM_LEN);


        int address = (now_data[0] & 0xffffff) << 24 | (now_data[1] & 0xffff) << 16 | (now_data[2] & 0xff) << 8 | now_data[3] & 0xff;
        int data_size = (now_data[4] & 0xffffff) << 24 | (now_data[5] & 0xffff) << 16 | (now_data[6] & 0xff) << 8 | now_data[7] & 0xff;
        int img_num = now_data[8] & 0xff;
        int img_x = (now_data[9] & 0xff) << 8 | now_data[10] & 0xff;
        int img_y = (now_data[11] & 0xff) << 8 | now_data[12] & 0xff;
        int img_w = (now_data[13] & 0xff) << 8 | now_data[14] & 0xff;
        int img_h = (now_data[15] & 0xff) << 8 | now_data[16] & 0xff;


        byte[] my_data = null;

        if (img_num != 0 && data_size != 0) {

//            MyLog.i(TAG, "==========有效数据==========");
////             MyLog.i(TAG, "数据处理 自定义 = now_data = " + FileUtils.printHexString(now_data));
//            MyLog.i(TAG, "address = " + address);
//            MyLog.i(TAG, "data_size = " + data_size);
//            MyLog.i(TAG, "img_num = " + img_num);
//            MyLog.i(TAG, "img_x = " + img_x);
//            MyLog.i(TAG, "img_y = " + img_y);
//            MyLog.i(TAG, "img_w = " + img_w);
//            MyLog.i(TAG, "img_h = " + img_h);
//            MyLog.i(TAG, "now_address = " + address);


            byte[] a_now_data = ThemeUtils.subBytes(byte_data, address, data_size);
//            MyLog.i(TAG, "a_now_data = " + a_now_data.length);

            int img_size = img_w * img_h;


            for (int j = 0; j < img_num; j++) {

                int a_now_data2_start = address + img_size * j;

//                MyLog.i(TAG, "图片下标 j = " + j + " a_now_data2_start = " + a_now_data2_start + " img_size = " + img_size);

                byte[] a_now_data2 = ThemeUtils.subBytes(byte_data, a_now_data2_start, img_size);

//                String img_str = BleTools.bytes2HexString(a_now_data2);
//                 MyLog.i(TAG, "图片下标 j = " + j + " img_str = " + img_str);

                //横向扫描
                Bitmap now_img_new_data_c = getHorBitmap(bg_bitmap, a_now_data2, color_R, color_G, color_B, img_w, img_h, img_x, img_y);
//                Bitmap now_img_new_data_c = getVerBitmap(bg_bitmap, a_now_data2, color_R, color_G, color_B, img_w, img_h, img_x, img_y);
                my_data = BmpUtils.getbitmapByte(now_img_new_data_c);
                byte_color_data_list.add(my_data);

            }
        } else {
//            MyLog.i(TAG, "==========无效数据==========");
//            MyLog.i(TAG, "address = " + address);
//            MyLog.i(TAG, "data_size = " + data_size);
//            MyLog.i(TAG, "img_x = " + img_x);
//            MyLog.i(TAG, "img_y = " + img_y);
//            MyLog.i(TAG, "img_w = " + img_w);
//            MyLog.i(TAG, "img_h = " + img_h);
        }

        byte[] result_data = null;

        if (byte_color_data_list.size() > 0) {

            if (byte_color_data_list.size() == 1) {
                result_data = my_data;
            } else {
                result_data = BinUtils.byteMergerList(byte_color_data_list);
            }

            if (result_data != null && result_data.length > 0) {
                return result_data;
            } else {
                return result_data;
            }


        } else {
            return null;
        }


    }

    private static Bitmap getHorBitmap(Bitmap background, byte[] a_data_list, int r, int g, int b, int w, int h, int x, int y) {
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


    public static Bitmap getNewBitmap(Bitmap background, Bitmap img) {

        if (background == null) {
            return null;
        }
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        Bitmap newmap = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newmap);
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawBitmap(img, 0, 0, null);
        canvas.save();
        canvas.restore();
        return newmap;
    }


    //暂不使用-后期可能需要
    private static Bitmap getVerBitmap(Bitmap background, byte[] a_data_list, int r, int g, int b, int w, int h, int x, int y) {

        Bitmap foreground = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        int pos = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
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


    public static Bitmap getNewTextBitmap(Bitmap input_bitmap, int r, int g, int b) {
        int width = input_bitmap.getWidth();
        int height = input_bitmap.getHeight();

        Bitmap out_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = input_bitmap.getPixel(j, i);
                int a = Color.alpha(color);
                int color_output = Color.argb(a, r, g, b);
                out_bitmap.setPixel(j, i, color_output);
            }
        }
        return out_bitmap;
    }

    public static boolean checkIsNewClockDial() {
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

        int deviceWidth = mBleDeviceTools.get_device_theme_resolving_power_width();
        int deviceHeight = mBleDeviceTools.get_device_theme_resolving_power_height();
        int deviceShape = mBleDeviceTools.get_device_theme_shape();
        boolean deviceScanfTypeIsVer = mBleDeviceTools.get_device_theme_scanning_mode();
        int deviceClockDialDataSize = mBleDeviceTools.get_device_theme_available_space();


        if (deviceWidth == 240 && deviceHeight == 240
                && deviceShape == 0 && !deviceScanfTypeIsVer
                && deviceClockDialDataSize > ClockDiaConstants.SQUARE_240X240_BIN_MAX_SIZE) {
            return true;
        } else if (deviceWidth == 240 && deviceHeight == 240
                && deviceShape == 2 && !deviceScanfTypeIsVer
                && deviceClockDialDataSize > ClockDiaConstants.CIRCULAR_240X240_BIN_MAX_SIZE) {
            return true;
        } else {
            return false;
        }


    }

}

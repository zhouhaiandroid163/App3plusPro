package com.zjw.apps3pluspro.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Bmp具类
 */

public class BmpUtils {



    //读取本地文件转成byte[]
    public static byte[] InputStream2ByteArray(String filePath) throws IOException {

        InputStream in = new FileInputStream(filePath);
        byte[] data = toByteArray(in);
        in.close();

        return data;
    }


    public static byte[] toByteArray(InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }


    public static byte[] bmp_24_16(byte[] byt) {
        byte[] retu = new byte[byt.length / 3 * 2];
        int abc = 0;

        for (int i = 0; i < byt.length; i += 3) {

//            int[] ccc =getHrgb2(byt[i]&0xff, byt[i + 1]&0xff, byt[i + 2]&0xff,180);
//            byte[] rgb16 = RGB24TORGB16((byte)ccc[0], (byte)ccc[1], (byte)ccc[2]);

            byte[] rgb16 = RGB24TORGB16(byt[i], byt[i + 1], byt[i + 2]);

            retu[abc] = rgb16[0];
            abc += 1;
            retu[abc] = rgb16[1];
            abc += 1;
        }

        return retu;
    }


    public static byte[] RGB24TORGB16(byte bmp_r, byte bmp_g, byte bmp_b) {


        byte result_r = (byte) (bmp_r >> 3 & 0x1f);
        byte result_g = (byte) (bmp_g >> 2 & 0x3f);
        byte result_b = (byte) (bmp_b >> 3 & 0x1f);

        byte[] result = new byte[2];
        result[0] = (byte) ((result_r << 3 & 0xf8) | (result_g >> 3 & 0x1f));
        result[1] = (byte) ((result_g << 5 & 0xe0) | result_b);

        return result;

    }



    public static byte[] getbitmapByte(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        byte[] result = new byte[width * height * 3];

        int abc = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

//                System.out.println("文件大小 = " + j + "," + i);
                int color = bitmap.getPixel(j, i);
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
//                int a = Color.alpha(color);
//                String r1 = Integer.toHexString(r);
//                String g1 = Integer.toHexString(g);
//                String b1 = Integer.toHexString(b);
//
//
//                if (r1.length() == 1) {
//                    r1 = "0" + r1;
//                }
//                if (g1.length() == 1) {
//                    g1 = "0" + g1;
//                }
//                if (b1.length() == 1) {
//                    b1 = "0" + b1;
//                }

//                String colorStr = r1 + g1 + b1;    //十六进制的颜色字符串。

//                System.out.println("测试数据解析 abc = " + abc + "   colorStr = " + colorStr);

                result[abc] = (byte) r;
                abc += 1;
                result[abc] = (byte) g;
                abc += 1;
                result[abc] = (byte) b;
                abc += 1;

//                System.out.println("测试数据解析 = str3 = " + BmpUtils.bytes2HexString16TXT(result));

            }
        }

        result = bmp_24_16(result);

        return result;
    }

    public static byte[] getbitmapNewByte(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        byte[] result = new byte[width * height * 3];

        int abc = 0;
        for (int i = 0; i <  width; i++) {
            for (int j = 0; j < height; j++) {

//                int color = bitmap.getPixel(j, i);
                int color = bitmap.getPixel(i, j);
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);


                result[abc] = (byte) r;
                abc += 1;
                result[abc] = (byte) g;
                abc += 1;
                result[abc] = (byte) b;
                abc += 1;

//                System.out.println("测试数据解析 = str3 = " + BmpUtils.bytes2HexString16TXT(result));

            }
        }

        result = bmp_24_16(result);

        return result;
    }

    //==============测试转换代码==============


    //打印十六位数据
    public static String bytes2HexString16TXT(byte[] data) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        int i = 1;
        for (byte b : data) {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1)// 每个字节8为，转为16进制标志，2个16进制位
            {
                tmp = "0" + tmp;
            }


            if (i == data.length) {
                sb.append("0X" + tmp);
            } else {
                sb.append("0X" + tmp + ",");
            }


//            if (i % 2 == 0) {
//                sb.append(tmp + ",");
//            } else {
//                sb.append(tmp);
//            }

            i++;
        }
        return sb.toString();
    }



    //Uri 转 Bitmap
    public static Bitmap decodeUriAsBitmap(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }


    //Uri 转 Bitmap
    public static int getSendPageLenght(byte[] data) {
        return data.length / 20 + 1;
    }

    public static int getSendPageRemainder(byte[] data) {
        return data.length % 20;
    }





}

package com.zjw.apps3pluspro.utils;

import android.os.Environment;
import android.util.Log;

import com.zjw.apps3pluspro.utils.log.MyLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ThemeUtils {

    private static final String TAG = ThemeUtils.class.getSimpleName();

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }


//    public static String getPercentageStr(int Max, int Num) {
//
//
//        float bili = (float) (Num) / (float) Max * 100;
//
//        String str = String.valueOf(bili);
//
//        if (bili > 10) {
//            if (str.length() >= 5) {
//                str = str.substring(0, 5);
//            }
//        } else {
//            if (str.length() >= 4) {
//                str = str.substring(0, 4);
//            }
//        }
//
//        return str;
//
//    }

    public static String getPercentageStr(int Max, int Num) {

        float bili = ((float) (Num) / (float) Max) * 100;
        DecimalFormat df = new DecimalFormat("######00.00");
        return df.format(bili);

    }


    public static String getPercentageStrInt(int Max, int Num) {


        float bili = (float) (Num) / (float) Max * 100;
        int bli_int = (int) bili;
        String str = String.valueOf(bli_int);

        return str;

    }

    private void getFile() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.d("ATG", path);
        File file = new File(path + "/e8706cf83a2cda33dae5c40025922d75.apk");
        String md5 = getFileMD5(file);
        Log.d("ATG", md5);
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            MyLog.i("", "MD5 = 文件不存在");
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bytesToHexString(digest.digest());
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    /**
     * 获取指定目录内所有文件路径
     *
     * @param dirPath 需要查询的文件目录
     */
    public static boolean isFileExistence(String dirPath, String file_name) {


//        MyLog.i(TAG, "文件夹 dirPath = " + dirPath);

        File f = new File(dirPath);
        if (!f.exists()) {//判断路径是否存在
//             MyLog.i(TAG, "修改文件名 = 111111111");
//            MyLog.i(TAG, "文件夹 不存在");
            return false;
        }

        File[] files = f.listFiles();

        if (files == null) {//判断权限
//            MyLog.i(TAG, "文件将 files = null");
            return false;
        }
//        MyLog.i(TAG, "文件将 files = " + files.length);

        for (int i = 0; i < files.length; i++) {
//            MyLog.i(TAG, "文件将 i = " + i + " name = " + files[i].getName());
            if (files[i].getName() != null && !files[i].getName().equals("") && files[i].getName().equals(file_name)) {
                return true;
            }
        }


        return false;
    }

    /**
     * 获取指定目录内所有文件路径
     *
     * @param dirPath 需要查询的文件目录
     */
    public static boolean checkFileExistenceByMd5(String dirPath, String fileName, String fileMd5) {
        fileMd5 = fileMd5.toLowerCase();
        MyLog.i(TAG, "checkFileExistence file_md5 = " + fileMd5);
        MyLog.i(TAG, "checkFileExistence file_name = " + fileName);
        File f = new File(dirPath + fileName);
        if (!f.exists()) {//判断路径是否存在
            return false;
        }
        String nowMd5 = ThemeUtils.getFileMD5(f);
        return nowMd5 != null && nowMd5.length() > 0 && nowMd5.equalsIgnoreCase(fileMd5);
    }

    /**
     * 获得指定文件的byte数组
     */
    public static byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 获得指定文件的byte数组
     */
    public static byte[] getPhoneList(ArrayList<String> name_list, ArrayList<String> phone_list) {

        List<byte[]> byte_list = new ArrayList<>();

        int data_size = name_list.size();

        byte[] head = new byte[4];

        head[0] = (byte) 0x88;
        head[1] = (byte) 0x99;

        head[2] = (byte) ((data_size >> 8) & 0xff);
        head[3] = (byte) (data_size & 0xff);

        byte_list.add(head);

        for (int i = 0; i < data_size; i++) {


            byte[] name = getHexToName(name_list.get(i));
            byte[] num = getHexToNum(phone_list.get(i));

            byte_list.add(name);
            byte_list.add(num);

        }


        byte[] result_byte = BinUtils.byteMergerList(byte_list);

        return result_byte;

    }

    public static byte[] getHexToName(String name) {

        byte[] result_data = new byte[52];


        List<byte[]> byte_list = new ArrayList<>();

        char char_list[] = name.toCharArray();

        int len = char_list.length;

        if (len > 25) {
            len = 25;
        }
        for (int j = 0; j < len; j++) {

            int int_char_aaa = (int) (char_list[j]);

            byte[] hexCharaaa = intToBytes(int_char_aaa);

            byte_list.add(hexCharaaa);

        }

        byte[] all_byte = BinUtils.byteMergerList(byte_list);
        int all_len = all_byte.length;
        if (all_len > 50) {
            all_len = 50;
        }

        result_data[0] = (byte) ((all_len >> 8) & 0xff);
        result_data[1] = (byte) (all_len & 0xff);

        for (int i = 0; i < all_len; i++) {
            result_data[2 + i] = all_byte[i];
        }

        return result_data;


    }

    public static byte[] getHexToNum(String num) {

        byte[] result_data = new byte[21];


        byte[] all_byte = num.getBytes();

        int all_len = all_byte.length;
        if (all_len > 20) {
            all_len = 20;
        }

        result_data[0] = (byte) (all_len & 0xff);

        for (int i = 0; i < all_len; i++) {
            result_data[1 + i] = all_byte[i];
        }

        return result_data;

    }


    public static byte[] getHexChar(String str) {

        List<byte[]> byte_list = new ArrayList<>();

        char char_list[] = str.toCharArray();

        for (int j = 0; j < char_list.length; j++) {

            int int_char_aaa = (int) (char_list[j]);

            byte[] hexCharaaa = intToBytes(int_char_aaa);

            byte_list.add(hexCharaaa);

        }

        return BinUtils.byteMergerList(byte_list);

    }

//    /**
//     * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
//     *
//     * @param value 要转换的int值
//     * @return byte数组
//     */
//    public static byte[] intToBytes(int value) {
//        byte[] src = new byte[2];
//        src[0] = (byte) ((value >> 8) & 0xFF);
//        src[1] = (byte) (value & 0xFF);
//        return src;
//    }

    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
     *
     * @param value 要转换的int值
     * @return byte数组
     */
    public static byte[] intToBytes(int value) {
        byte[] src = new byte[2];
        src[0] = (byte) (value & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        return src;
    }

    public static String bytes2HexString2(byte[] data) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;

//        sb.append("0x");

        for (byte b : data) {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制

            tmp = Integer.toHexString(0xFF & b);

            if (tmp.length() == 1)// 每个字节8为，转为16进制标志，2个16进制位
            {
                tmp = "0" + tmp;
            }

//            tmp = "0x" + tmp;
//            tmp = tmp + ",";
            sb.append(tmp + " ");
        }
        return sb.toString();
    }

    public static String bytes2HexString3(byte[] data) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;

//        sb.append("0x");

        for (byte b : data) {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制

            tmp = Integer.toHexString(0xFF & b);

            if (tmp.length() == 1)// 每个字节8为，转为16进制标志，2个16进制位
            {
                tmp = "0" + tmp;
            }

//            tmp = "0x" + tmp;
//            tmp = tmp + ",";
            sb.append(tmp + "");
        }
        return sb.toString();
    }


}

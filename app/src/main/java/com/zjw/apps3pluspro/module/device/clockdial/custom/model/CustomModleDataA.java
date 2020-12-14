package com.zjw.apps3pluspro.module.device.clockdial.custom.model;


import android.graphics.Bitmap;


import com.zjw.apps3pluspro.bleservice.BleTools;
import com.zjw.apps3pluspro.module.device.clockdial.custom.CustomClockSubUtils;
import com.zjw.apps3pluspro.utils.BinUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.ArrayList;
import java.util.List;

//自定义表盘数据A
public class CustomModleDataA {
    private static final String TAG = CustomModleDataA.class.getSimpleName();
    private final int DataLen = 20;//数据长度

    int address;//地址
    int size;//大小
    int imgCount;//图片数量
    int coordinateX;//X坐标
    int coordinateY;//Y坐标
    int imgWidth;//图片宽度
    int imgHeight;//图片高度

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getImgCount() {
        return imgCount;
    }

    public void setImgCount(int imgCount) {
        this.imgCount = imgCount;
    }

    public int getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(int coordinateX) {
        this.coordinateX = coordinateX;
    }

    public int getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(int coordinateY) {
        this.coordinateY = coordinateY;
    }

    public int getImgWidth() {
        return imgWidth;
    }

    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }

    public int getImgHeight() {
        return imgHeight;
    }

    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }

    @Override
    public String toString() {
        return "CustomModleDataA{" +
                "address=" + address +
                ", size=" + size +
                ", imgCount=" + imgCount +
                ", coordinateX=" + coordinateX +
                ", coordinateY=" + coordinateY +
                ", imgWidth=" + imgWidth +
                ", imgHeight=" + imgHeight +
                '}';
    }

    public CustomModleDataA(byte[] data) {

        MyLog.i(TAG, "CustomModleDataA data = " + BleTools.printHexString(data) + " len = " + data.length);

        if (data.length == DataLen) {
            int pos = 0;

            int address = CustomClockSubUtils.getMyData(data, pos, 4);
//            MyLog.i(TAG, "CustomModleDataA pos = " + pos + " address = " + address);
            pos += 4;
            setAddress(address);

            int size = CustomClockSubUtils.getMyData(data, pos, 4);
//            MyLog.i(TAG, "CustomModleDataA pos = " + pos + " size = " + size);
            pos += 4;
            setSize(size);

            int imgCount = CustomClockSubUtils.getMyData(data, pos, 4);
//            MyLog.i(TAG, "CustomModleDataA pos = " + pos + " imgCount = " + imgCount);
            pos += 4;
            setImgCount(imgCount);

            int coordinateX = CustomClockSubUtils.getMyData(data, pos, 2);
//            MyLog.i(TAG, "CustomModleDataA pos = " + pos + " coordinateX = " + coordinateX);
            pos += 2;
            setCoordinateX(coordinateX);

            int coordinateY = CustomClockSubUtils.getMyData(data, pos, 2);
//            MyLog.i(TAG, "CustomModleDataA pos = " + pos + " coordinateY = " + coordinateY);
            pos += 2;
            setCoordinateY(coordinateY);

            int imgWidth = CustomClockSubUtils.getMyData(data, pos, 2);
//            MyLog.i(TAG, "CustomModleDataA pos = " + pos + " imgWidth = " + imgWidth);
            pos += 2;
            setImgWidth(imgWidth);

            int imgHeight = CustomClockSubUtils.getMyData(data, pos, 2);
//            MyLog.i(TAG, "CustomModleDataA pos = " + pos + " imgHeight = " + imgHeight);
            pos += 2;
            setImgHeight(imgHeight);


        }
    }

    public byte[] getColorData(int color_R, int color_G, int color_B, byte[] byte_data, Bitmap bg_bitmap) {

        List<byte[]> byte_color_data_list = new ArrayList<>();

//        byte[] now_data = CustomClockSubUtils.subBytes(byte_data, start_pos, newDataHeadLen);

        int address = this.address;
        int data_size = this.size;
        int img_num = this.imgCount;
        int img_x = this.coordinateX;
        int img_y = this.coordinateY;
        int img_w = this.imgWidth;
        int img_h = this.imgHeight;

        //补偿
//        address += INDEXS_SIZE;

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

            int img_size = img_w * img_h;

            for (int j = 0; j < img_num; j++) {

                int a_now_data2_start = address + img_size * j;
//                MyLog.i(TAG, "图片下标 j = " + j + " a_now_data2_start = " + a_now_data2_start + " img_size = " + img_size);

                byte[] a_now_data2 = CustomClockSubUtils.subBytes(byte_data, a_now_data2_start, img_size);
//                String img_str = BleTools.bytes2HexString(a_now_data2);
//                 MyLog.i(TAG, "图片下标 j = " + j + " img_str = " + img_str);

                //横向扫描
                Bitmap now_img_new_data_c = CustomClockSubUtils.getHorBitmap(bg_bitmap, a_now_data2, color_R, color_G, color_B, img_w, img_h, img_x, img_y);
//                Bitmap now_img_new_data_c = getVerBitmap(bg_bitmap, a_now_data2, color_R, color_G, color_B, img_w, img_h, img_x, img_y);
                my_data = CustomClockSubUtils.getbitmapByte(now_img_new_data_c);
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


}


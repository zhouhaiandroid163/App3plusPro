package com.zjw.apps3pluspro.module.device.clockdial.custom.model;


import com.zjw.apps3pluspro.bleservice.BleTools;

public class CustomDataModle {
    int dataType;//数据类型
    int imgType;//图片类型
    byte[] data;//A数据类型

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public int getImgType() {
        return imgType;
    }

    public void setImgType(int imgType) {
        this.imgType = imgType;
    }

    public void setData(byte[] data) {
        this.data = data;
    }


    public byte[] getData() {
        return data;
    }

    public CustomDataModle() {

    }

    public CustomDataModle(byte[] mData) {
        this.dataType = 0;
        this.imgType = 0;
        this.data = mData;
    }

    public byte[] getNewData() {
        byte[] result = new byte[data.length + 2];
        result[0] = (byte) (dataType & 0xff);
        result[1] = (byte) (imgType & 0xff);
        System.arraycopy(data, 0, result, 2, data.length);
        return result;
    }

    @Override
    public String toString() {
        return "CustomDataModle{" +
                "dataType=" + dataType +
                ", imgType=" + imgType +
                ", data=" + BleTools.printHexString(data) +
                '}';
    }
}


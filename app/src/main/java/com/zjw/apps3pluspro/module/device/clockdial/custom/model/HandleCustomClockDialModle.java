package com.zjw.apps3pluspro.module.device.clockdial.custom.model;



import com.zjw.apps3pluspro.module.device.clockdial.custom.CustomClockSubUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class HandleCustomClockDialModle {

    private final static String TAG = HandleCustomClockDialModle.class.getSimpleName();

    int Version;//文件名
    int targetFileSize;//目标文件大小

    //数据有效性
    boolean isIndexes;//索引
    boolean isData;//数据
    boolean isBgImg;//背景图
    boolean isThumbnailImg;//缩略图

    int indexReadAddress;//索引读取地址
    int indexReadSize;//索引大小
    int indexWriteAddress;//索引写入地址
    int indexWriteSize;//索引大小

    int dataReadAddress;//数据读取地址
    int dataReadSize;//数据读取大小
    int dataReadCount;//数据读取-个数

    int dataWriteAddress;//数据写入地址
    int dataWriteSize;//数据写入大小

    int bgImgType;//图片类型
    int bgImgWidth;//背景宽度
    int bgImgHeight;//背景高度
    int bgImgWriteAddress;//背景写入地址
    int bgImgWriteSize;//背景写入大小

    int thumbnailImgType;//图片类型
    int thumbnailImgWidth;//缩略图宽度
    int thumbnailImgHeight;//缩略图高度
    int thumbnailImgWriteAddress;//缩略图写入地址
    int thumbnailImgWriteSize;//缩略图写入大小


    ArrayList<CustomDataModle> dataList;
    byte[] imgData;


    public int getVersion() {
        return Version;
    }

    public void setVersion(int version) {
        Version = version;
    }

    public int getTargetFileSize() {
        return targetFileSize;
    }

    public void setTargetFileSize(int targetFileSize) {
        this.targetFileSize = targetFileSize;
    }

    public boolean isIndexes() {
        return isIndexes;
    }

    public void setIndexes(boolean indexes) {
        isIndexes = indexes;
    }

    public boolean isData() {
        return isData;
    }

    public void setData(boolean data) {
        isData = data;
    }

    public boolean isBgImg() {
        return isBgImg;
    }

    public void setBgImg(boolean bgImg) {
        isBgImg = bgImg;
    }

    public boolean isThumbnailImg() {
        return isThumbnailImg;
    }

    public void setThumbnailImg(boolean thumbnailImg) {
        isThumbnailImg = thumbnailImg;
    }

    public int getIndexReadAddress() {
        return indexReadAddress;
    }

    public void setIndexReadAddress(int indexReadAddress) {
        this.indexReadAddress = indexReadAddress;
    }

    public int getIndexReadSize() {
        return indexReadSize;
    }

    public void setIndexReadSize(int indexReadSize) {
        this.indexReadSize = indexReadSize;
    }

    public int getIndexWriteAddress() {
        return indexWriteAddress;
    }

    public void setIndexWriteAddress(int indexWriteAddress) {
        this.indexWriteAddress = indexWriteAddress;
    }

    public int getIndexWriteSize() {
        return indexWriteSize;
    }

    public void setIndexWriteSize(int indexWriteSize) {
        this.indexWriteSize = indexWriteSize;
    }

    public int getDataReadAddress() {
        return dataReadAddress;
    }

    public void setDataReadAddress(int dataReadAddress) {
        this.dataReadAddress = dataReadAddress;
    }

    public int getDataReadSize() {
        return dataReadSize;
    }

    public void setDataReadSize(int dataReadSize) {
        this.dataReadSize = dataReadSize;
    }

    public int getDataReadCount() {
        return dataReadCount;
    }

    public void setDataReadCount(int dataReadCount) {
        this.dataReadCount = dataReadCount;
    }

    public int getDataWriteAddress() {
        return dataWriteAddress;
    }

    public void setDataWriteAddress(int dataWriteAddress) {
        this.dataWriteAddress = dataWriteAddress;
    }

    public int getDataWriteSize() {
        return dataWriteSize;
    }

    public void setDataWriteSize(int dataWriteSize) {
        this.dataWriteSize = dataWriteSize;
    }

    public int getBgImgType() {
        return bgImgType;
    }

    public void setBgImgType(int bgImgType) {
        this.bgImgType = bgImgType;
    }

    public int getBgImgWidth() {
        return bgImgWidth;
    }

    public void setBgImgWidth(int bgImgWidth) {
        this.bgImgWidth = bgImgWidth;
    }

    public int getBgImgHeight() {
        return bgImgHeight;
    }

    public void setBgImgHeight(int bgImgHeight) {
        this.bgImgHeight = bgImgHeight;
    }

    public int getBgImgWriteAddress() {
        return bgImgWriteAddress;
    }

    public void setBgImgWriteAddress(int bgImgWriteAddress) {
        this.bgImgWriteAddress = bgImgWriteAddress;
    }

    public int getBgImgWriteSize() {
        return bgImgWriteSize;
    }

    public void setBgImgWriteSize(int bgImgWriteSize) {
        this.bgImgWriteSize = bgImgWriteSize;
    }

    public int getThumbnailImgType() {
        return thumbnailImgType;
    }

    public void setThumbnailImgType(int thumbnailImgType) {
        this.thumbnailImgType = thumbnailImgType;
    }

    public int getThumbnailImgWidth() {
        return thumbnailImgWidth;
    }

    public void setThumbnailImgWidth(int thumbnailImgWidth) {
        this.thumbnailImgWidth = thumbnailImgWidth;
    }

    public int getThumbnailImgHeight() {
        return thumbnailImgHeight;
    }

    public void setThumbnailImgHeight(int thumbnailImgHeight) {
        this.thumbnailImgHeight = thumbnailImgHeight;
    }

    public int getThumbnailImgWriteAddress() {
        return thumbnailImgWriteAddress;
    }

    public void setThumbnailImgWriteAddress(int thumbnailImgWriteAddress) {
        this.thumbnailImgWriteAddress = thumbnailImgWriteAddress;
    }


    public int getThumbnailImgWriteSize() {
        return thumbnailImgWriteSize;
    }

    public void setThumbnailImgWriteSize(int thumbnailImgWriteSize) {
        this.thumbnailImgWriteSize = thumbnailImgWriteSize;
    }

    public ArrayList<CustomDataModle> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<CustomDataModle> dataList) {
        this.dataList = dataList;
    }

    public byte[] getImgData() {
        return imgData;
    }

    public void setImgData(byte[] imgData) {
        this.imgData = imgData;
    }

    @Override
    public String toString() {
        return "HandleCustomClockDialModle{" +
                "Version=" + Version +
                ", targetFileSize=" + targetFileSize +
                ", isIndexes=" + isIndexes +
                ", isData=" + isData +
                ", isBgImg=" + isBgImg +
                ", isThumbnailImg=" + isThumbnailImg +
                ", indexReadAddress=" + indexReadAddress +
                ", indexReadSize=" + indexReadSize +
                ", indexWriteAddress=" + indexWriteAddress +
                ", indexWriteSize=" + indexWriteSize +
                ", dataReadAddress=" + dataReadAddress +
                ", dataReadSize=" + dataReadSize +
                ", dataReadCount=" + dataReadCount +
                ", dataWriteAddress=" + dataWriteAddress +
                ", dataWriteSize=" + dataWriteSize +
                ", bgImgType=" + bgImgType +
                ", bgImgWidth=" + bgImgWidth +
                ", bgImgHeight=" + bgImgHeight +
                ", bgImgWriteAddress=" + bgImgWriteAddress +
                ", bgImgWriteSize=" + bgImgWriteSize +
                ", thumbnailImgType=" + thumbnailImgType +
                ", thumbnailImgWidth=" + thumbnailImgWidth +
                ", thumbnailImgHeight=" + thumbnailImgHeight +
                ", thumbnailImgWriteAddress=" + thumbnailImgWriteAddress +
                ", thumbnailImgWriteSize=" + thumbnailImgWriteSize +
                ", dataList=" + dataList +
                ", imgData=" + Arrays.toString(imgData) +
                '}';
    }

    public HandleCustomClockDialModle(byte[] source_data) {


        int pos = 0;

        int version = CustomClockSubUtils.getMyData(source_data, pos, 1);
        MyLog.i(TAG, "getNewCustomClockDialData pos = " + pos + " version = " + version);
        pos += 1;

        setVersion(version);

        int targetFileSize = CustomClockSubUtils.getMyData(source_data, pos, 4);
        MyLog.i(TAG, "getNewCustomClockDialData pos = " + pos + " targetFileSize = " + targetFileSize);
        pos += 4;

        setTargetFileSize(targetFileSize);

//        int[] deviceParams1 = BinstrToIntArray(source_data[5]);
//        int[] deviceParams2 = BinstrToIntArray(source_data[6]);
//        int[] deviceParams3 = BinstrToIntArray(source_data[7]);
//        int[] deviceParams4 = BinstrToIntArray(source_data[8]);

        boolean is_index = BinstrToIntArray(source_data[5])[0] == 1;
        boolean is_data = BinstrToIntArray(source_data[5])[1] == 1;
        boolean is_bg_img = BinstrToIntArray(source_data[5])[2] == 1;
        boolean is_thumbnail_img = BinstrToIntArray(source_data[5])[3] == 1;
        pos += 4;

        setIndexes(is_index);
        setData(is_data);
        setBgImg(is_bg_img);
        setThumbnailImg(is_thumbnail_img);


        MyLog.i(TAG, "getNewCustomClockDialData is_index = " + is_index);
        MyLog.i(TAG, "getNewCustomClockDialData is_data = " + is_data);
        MyLog.i(TAG, "getNewCustomClockDialData is_bg_img = " + is_bg_img);
        MyLog.i(TAG, "getNewCustomClockDialData is_thumbnail_img = " + is_thumbnail_img);

        int indexesReadAddress = CustomClockSubUtils.getMyData(source_data, pos, 4);
        MyLog.i(TAG, "getNewCustomClockDialData pos = " + pos + " indexesReadAddress = " + indexesReadAddress);
        pos += 4;
        setIndexReadAddress(indexesReadAddress);

        int indexesReadSize = CustomClockSubUtils.getMyData(source_data, pos, 4);
        MyLog.i(TAG, "getNewCustomClockDialData pos = " + pos + " indexesReadSize = " + indexesReadSize);
        pos += 4;
        setIndexReadSize(indexesReadSize);

        int indexesWriteAddress = CustomClockSubUtils.getMyData(source_data, pos, 4);
        MyLog.i(TAG, "getNewCustomClockDialData pos = " + pos + " indexesWriteAddress = " + indexesWriteAddress);
        pos += 4;
        setIndexWriteAddress(indexesWriteAddress);

        int indexesWriteSize = CustomClockSubUtils.getMyData(source_data, pos, 4);
        MyLog.i(TAG, "getNewCustomClockDialData pos = " + pos + " indexesWriteSize = " + indexesWriteSize);
        pos += 4;
        setIndexWriteSize(indexesWriteSize);

        int dataReadAddress = CustomClockSubUtils.getMyData(source_data, pos, 4);
        MyLog.i(TAG, "getNewCustomClockDialData pos = " + pos + " dataReadAddress = " + dataReadAddress);
        pos += 4;
        setDataReadAddress(dataReadAddress);

        int dataReadSize = CustomClockSubUtils.getMyData(source_data, pos, 4);
        MyLog.i(TAG, "getNewCustomClockDialData pos = " + pos + " dataReadSize = " + dataReadSize);
        pos += 4;
        setDataReadSize(dataReadSize);

        int dataReadCount = CustomClockSubUtils.getMyData(source_data, pos, 4);
        MyLog.i(TAG, "getNewCustomClockDialData pos = " + pos + " dataReadCount = " + dataReadCount);
        pos += 4;
        setDataReadCount(dataReadCount);

        int dataWriteAddress = CustomClockSubUtils.getMyData(source_data, pos, 4);
        MyLog.i(TAG, "getNewCustomClockDialData pos = " + pos + " dataWriteAddress = " + dataWriteAddress);
        pos += 4;
        setDataWriteAddress(dataWriteAddress);

        int dataWriteSize = CustomClockSubUtils.getMyData(source_data, pos, 4);
        MyLog.i(TAG, "getNewCustomClockDialData pos = " + pos + " dataWriteSize = " + dataWriteSize);
        pos += 4;
        setDataWriteSize(dataWriteSize);

        int thumbnailFormat = CustomClockSubUtils.getMyData(source_data, pos, 1);
        MyLog.i(TAG, "getNewCustomClockDialData pos = " + pos + " thumbnailFormat = " + thumbnailFormat);
        pos += 1;
        setThumbnailImgType(thumbnailFormat);

        int thumbnailWidth = CustomClockSubUtils.getMyData(source_data, pos, 2);
        MyLog.i(TAG, "getNewCustomClockDialData pos = " + pos + " thumbnailWidth = " + thumbnailWidth);
        pos += 2;
        setThumbnailImgWidth(thumbnailWidth);

        int thumbnailHeight = CustomClockSubUtils.getMyData(source_data, pos, 2);
        MyLog.i(TAG, "getNewCustomClockDialData pos = " + pos + " thumbnailHeight = " + thumbnailHeight);
        pos += 2;
        setThumbnailImgHeight(thumbnailHeight);

        int thumbnailWriteAddress = CustomClockSubUtils.getMyData(source_data, pos, 4);
        MyLog.i(TAG, "getNewCustomClockDialData pos = " + pos + " thumbnailWriteAddress = " + thumbnailWriteAddress);
        pos += 4;
        setThumbnailImgWriteAddress(thumbnailWriteAddress);

        int thumbnailWriteSize = CustomClockSubUtils.getMyData(source_data, pos, 4);
        MyLog.i(TAG, "getNewCustomClockDialData pos = " + pos + " thumbnailWriteSize = " + thumbnailWriteSize);
        pos += 4;
        setThumbnailImgWriteSize(thumbnailWriteSize);

        int bgFormat = CustomClockSubUtils.getMyData(source_data, pos, 1);
        MyLog.i(TAG, "getNewCustomClockDialData pos = " + pos + " bgFormat = " + bgFormat);
        pos += 1;
        setBgImgType(bgFormat);

        int bgWidth = CustomClockSubUtils.getMyData(source_data, pos, 2);
        MyLog.i(TAG, "getNewCustomClockDialData pos = " + pos + " bgWidth = " + bgWidth);
        pos += 2;
        setBgImgWidth(bgWidth);

        int bgHeight = CustomClockSubUtils.getMyData(source_data, pos, 2);
        MyLog.i(TAG, "getNewCustomClockDialData pos = " + pos + " bgHeight = " + bgHeight);
        pos += 2;
        setBgImgHeight(bgHeight);

        int bgWriteAddress = CustomClockSubUtils.getMyData(source_data, pos, 4);
        MyLog.i(TAG, "getNewCustomClockDialData pos = " + pos + " bgWriteAddress = " + bgWriteAddress);
        pos += 4;
        setBgImgWriteAddress(bgWriteAddress);

        int bgWriteSize = CustomClockSubUtils.getMyData(source_data, pos, 4);
        MyLog.i(TAG, "getNewCustomClockDialData pos = " + pos + " bgWriteSize = " + bgWriteSize);
        pos += 4;
        setBgImgWriteSize(bgWriteSize);


        MyLog.i(TAG, "getNewCustomClockDialData 数据读取-地址 = " + dataReadAddress);
        MyLog.i(TAG, "getNewCustomClockDialData 数据读取-大小 = " + dataReadSize);

        byte[] mImgData = CustomClockSubUtils.subBytes(source_data, dataReadAddress, dataReadSize);
        setImgData(mImgData);

        MyLog.i(TAG, "getNewCustomClockDialData 数据格式 = " + dataReadCount);
        ArrayList<CustomDataModle> mCustomDataModleList = new ArrayList<>();

        int data_pos = 0;
        for (int i = 0; i < dataReadCount; i++) {

            CustomDataModle mCustomDataModle = new CustomDataModle();
            //数据格式
            int dataFormat = CustomClockSubUtils.getMyData(mImgData, data_pos, 1);
//            MyLog.i(TAG, "getNewCustomClockDialData data_pos = " + data_pos + " dataFormat = " + dataFormat);
            data_pos += 1;

            //图片格式
            int ImgFormat = CustomClockSubUtils.getMyData(mImgData, data_pos, 1);
//            MyLog.i(TAG, "getNewCustomClockDialData data_pos = " + data_pos + " ImgFormat = " + ImgFormat);
            data_pos += 1;

            mCustomDataModle.setDataType(dataFormat);
            mCustomDataModle.setImgType(ImgFormat);

            if (dataFormat == 0) {
                byte[] nowData = CustomClockSubUtils.subBytes(mImgData, data_pos, 20);
//                MyLog.i(TAG, "getNewCustomClockDialData data_pos = " + data_pos + " nowData = " + FileUtils.printHexString(nowData));
                data_pos += 20;
                mCustomDataModle.setData(nowData);
            }
            mCustomDataModleList.add(mCustomDataModle);

        }
        setDataList(mCustomDataModleList);


    }


    // 将二进制字符串转换成int数组
    public static int[] BinstrToIntArray(byte my_byte) {

        int[] result = new int[8];
        byte[] binStr = new byte[1];
        binStr[0] = my_byte;
        String byte_str = binary(binStr, 2);


        for (int i = byte_str.length() - 1; i >= 0; i--) {
            result[i + 8 - byte_str.length()] = Integer.valueOf(byte_str.substring(i, i + 1));
        }

        return result;
    }

    public static String binary(byte[] bytes, int radix) {
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }

}


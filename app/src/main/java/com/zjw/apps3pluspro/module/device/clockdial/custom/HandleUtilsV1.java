package com.zjw.apps3pluspro.module.device.clockdial.custom;

import android.graphics.Bitmap;


import com.zjw.apps3pluspro.module.device.clockdial.custom.model.CustomDataModle;
import com.zjw.apps3pluspro.module.device.clockdial.custom.model.CustomModleDataA;
import com.zjw.apps3pluspro.module.device.clockdial.custom.model.HandleCustomClockDialModle;
import com.zjw.apps3pluspro.utils.BinUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义表盘数据处理
 */
public class HandleUtilsV1 {

    private final static String TAG = HandleUtilsV1.class.getSimpleName();

    //=============新的方法==================
    public static byte[] getData(byte[] source_data, int color_R, int color_G, int color_B, Bitmap bg_bmp, Bitmap text_bitmp
    ) {
        MyLog.i(TAG, "getNewCustomClockDialData source_data len = " + source_data.length);

        byte[] targetData = null;

        HandleCustomClockDialModle mHandleCustomClockDialModle = new HandleCustomClockDialModle(source_data);
        MyLog.i(TAG, "getNewCustomClockDialData mHandleCustomClockDialModle len = " + mHandleCustomClockDialModle.toString());
        //目标大小
        targetData = new byte[(mHandleCustomClockDialModle.getTargetFileSize())];

        //是否包含索引
        if (mHandleCustomClockDialModle.isIndexes()) {
            System.arraycopy(
                    source_data,
                    mHandleCustomClockDialModle.getIndexReadAddress(),
                    targetData,
                    mHandleCustomClockDialModle.getIndexWriteAddress(), mHandleCustomClockDialModle.getIndexWriteSize()
            );
        }

        //是否包含数据
        if (mHandleCustomClockDialModle.isData()) {
            ArrayList<CustomDataModle> mDataList = mHandleCustomClockDialModle.getDataList();
            List<byte[]> byte_new_data_list = new ArrayList<>();
            if (mDataList != null && mDataList.size() > 0) {
                for (int i = 0; i < mDataList.size(); i++) {
                    CustomDataModle mCustomDataModle = mDataList.get(i);
//                    MyLog.i(TAG, "getNewCustomClockDialData i = " + i + " mCustomDataModle = " + mCustomDataModle.toString());
                    //数据格式等于A
                    if (mCustomDataModle.getDataType() == 0) {
                        //图片格式=BMP
                        if (mCustomDataModle.getImgType() == 0) {
                            CustomModleDataA mCustomModleDataA = new CustomModleDataA(mCustomDataModle.getData());
//                            MyLog.i(TAG, "getNewCustomClockDialData i = " + i + " mCustomModleDataA = " + mCustomModleDataA.toString());
                            byte[] new_data = mCustomModleDataA.getColorData(color_R, color_G, color_B, source_data, bg_bmp);
                            if (new_data != null) {
//                                    MyLog.i(TAG, "getNewCustomClockDialData 有数据");
                                //拼接-数据
                                byte_new_data_list.add(new_data);
                            } else {
//                                    MyLog.i(TAG, "getNewCustomClockDialData 没有数据");
                            }
                        }
                    }
                }
                byte[] endData = BinUtils.byteMergerList(byte_new_data_list);
                if (endData != null) {
                    MyLog.i(TAG, "getNewCustomClockDialData endData.len = " + endData.length);
                    System.arraycopy(
                            endData,
                            0,
                            targetData,
                            mHandleCustomClockDialModle.getDataWriteAddress(), mHandleCustomClockDialModle.getDataWriteSize()
                    );
                } else {
                    MyLog.i(TAG, "getNewCustomClockDialData endData.len = null");
                }
            }
        }

        //是否包含缩略图
        if (mHandleCustomClockDialModle.isThumbnailImg()) {
            int mThumbnailImgType = mHandleCustomClockDialModle.getThumbnailImgType();
            int mThumbnailImgWidth = mHandleCustomClockDialModle.getThumbnailImgWidth();
            int mThumbnailImgHeight = mHandleCustomClockDialModle.getThumbnailImgHeight();
            int mThumbnailImgWriteAddress = mHandleCustomClockDialModle.getThumbnailImgWriteAddress();
            int mThumbnailImgWriteSize = mHandleCustomClockDialModle.getThumbnailImgWriteSize();
            MyLog.i(TAG, "getNewCustomClockDialData 缩略图-类型 = " + mThumbnailImgType);
            MyLog.i(TAG, "getNewCustomClockDialData 缩略图-宽度 = " + mThumbnailImgWidth);
            MyLog.i(TAG, "getNewCustomClockDialData 缩略图-高度 = " + mThumbnailImgHeight);
            MyLog.i(TAG, "getNewCustomClockDialData 缩略图-写入-地址 = " + mThumbnailImgWriteAddress);
            MyLog.i(TAG, "getNewCustomClockDialData 缩略图-写入-大小 = " + mThumbnailImgWriteSize);

            //效果图
            Bitmap newEffectBitmap = MyCustomClockUtils.getCustomBEffectImg(source_data, color_R, color_G, color_B, bg_bmp, text_bitmp);
            //缩略图
            Bitmap newThumbnailBitmap = CustomClockSubUtils.zoomImg(newEffectBitmap, mThumbnailImgWidth, mThumbnailImgHeight);
            //BMP-图片
            if (mThumbnailImgType == 0) {
                byte[] thumbnailData = CustomClockSubUtils.getbitmapByte(newThumbnailBitmap);
                MyLog.i(TAG, "getNewCustomClockDialData thumbnail_data_len = " + thumbnailData.length);
                System.arraycopy(
                        thumbnailData,
                        0,
                        targetData,
                        mThumbnailImgWriteAddress, mThumbnailImgWriteSize
                );
            }
        }

        //是否包含背景图
        if (mHandleCustomClockDialModle.isBgImg()) {
            int mBgImgType = mHandleCustomClockDialModle.getBgImgType();
            int mBgImgWidth = mHandleCustomClockDialModle.getBgImgWidth();
            int mBgImgHeight = mHandleCustomClockDialModle.getBgImgHeight();
            int mBgImgWriteAddress = mHandleCustomClockDialModle.getBgImgWriteAddress();
            int mBgImgWriteSize = mHandleCustomClockDialModle.getBgImgWriteSize();
            MyLog.i(TAG, "getNewCustomClockDialData 背景-类型 = " + mBgImgType);
            MyLog.i(TAG, "getNewCustomClockDialData 背景-宽度 = " + mBgImgWidth);
            MyLog.i(TAG, "getNewCustomClockDialData 背景-高度 = " + mBgImgHeight);
            MyLog.i(TAG, "getNewCustomClockDialData 背景-写入-地址 = " + mBgImgWriteAddress);
            MyLog.i(TAG, "getNewCustomClockDialData 背景-写入-大小 = " + mBgImgWriteSize);

            //BMP格式
            if (mBgImgType == 0) {
                byte[] bgData = CustomClockSubUtils.getbitmapByte(bg_bmp);
                MyLog.i(TAG, "bg_data_len = " + bgData.length);
                System.arraycopy(
                        bgData,
                        0,
                        targetData,
                        mBgImgWriteAddress, mBgImgWriteSize
                );
            }
        }
        return targetData;

    }


}

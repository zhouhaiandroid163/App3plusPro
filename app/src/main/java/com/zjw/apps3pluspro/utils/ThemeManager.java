package com.zjw.apps3pluspro.utils;

import android.content.Context;


import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by android
 * on 2020/6/15.
 */
public class ThemeManager {

    private static final String TAG = ThemeManager.class.getSimpleName();

    private static ThemeManager themeManager;

    public static final String APP_NAME = "com.zjw.apps3pluspro";
    public static final String ACTION_CMD_APP_START = APP_NAME + "_ThemeManager" + "ACTION_CMD_START";
    public static final String ACTION_CMD_DEVICE_CONFIRM = APP_NAME + "__ThemeManager" + "ACTION_CMD_DEVICE_CONFIRM";
    public static final String ACTION_CMD_DEVICE_START = APP_NAME + "__ThemeManager" + "ACTION_CMD_DEVICE_START";
    public static final String ACTION_CMD_APP_CONFIRM = APP_NAME + "__ThemeManager" + "ACTION_CMD_APP_CONFIRM";
    public static final String ACTION_CMD_DEVICE_REISSUE_PACK = APP_NAME + "__ThemeManager" + "ACTION_CMD_DEVICE_REISSUE_PACK";

    public static ThemeManager getInstance() {
        if (themeManager == null) {
            themeManager = new ThemeManager();
        }
        return themeManager;
    }

    private ThemeManager() {
    }

    public int dataByteLength = 0;
    public int dataPackTotalPieceLength = 0;
    public byte[] dataByte;    // 发送的数据
    public int dataPieceMaxPack = 0;
    public int dataPieceEndPack = 0;
    public int onePackMaxDataLength = 0;
    public int dataPieceTotalByteLength = 0;

    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    public void initUpload(Context context, String type, byte[] fileName) {
//        int mtu = BleService.currentMtu;
        int mtu = mBleDeviceTools.get_device_mtu_num();
        int dataHead = 6;
        onePackMaxDataLength = mtu - dataHead;

        byte[] fileByte;

        byte[] valueByte1 = new byte[22];
        valueByte1[0] = (byte) 0; // 加密方式

        if ("watch".equalsIgnoreCase(type)) {
            fileByte = fileName;//299455
            valueByte1[1] = (byte) 0x10;  //类型
        } else {
            String SDCARD = Constants.UPDATE_DEVICE_FILE + "ota.bin";
            fileByte = getBytes(SDCARD);//299455
//            fileByte = getBytesByAssets(context, CommonAttributes.P_OTA_NAME);
            valueByte1[1] = (byte) 0x20;  //类型
        }
        for (int i = 0; i < 16; i++) {
            valueByte1[2 + i] = 0; // md5
        }
        valueByte1[18] = (byte) (fileByte.length & 0xFF);
        valueByte1[19] = (byte) ((fileByte.length >> 8) & 0xFF);
        valueByte1[20] = (byte) ((fileByte.length >> 16) & 0xFF);
        valueByte1[21] = (byte) ((fileByte.length >> 24) & 0xFF);

        byte[] check = new byte[valueByte1.length + fileByte.length];
        System.arraycopy(valueByte1, 0, check, 0, valueByte1.length);
        System.arraycopy(fileByte, 0, check, valueByte1.length, fileByte.length);
        byte[] CRC32 = CrcUtils.CRC32(check);

        dataByte = new byte[valueByte1.length + fileByte.length + CRC32.length];//299481
        dataByteLength = dataByte.length;
        System.arraycopy(valueByte1, 0, dataByte, 0, valueByte1.length);
        System.arraycopy(fileByte, 0, dataByte, valueByte1.length, fileByte.length);
        System.arraycopy(CRC32, 0, dataByte, valueByte1.length + fileByte.length, CRC32.length);

        // 每一个片最大4kB,最大发包数16，一片的最大发送字节数据3868
        dataPieceMaxPack = 4000 / mtu;
        dataPieceTotalByteLength = (dataPieceMaxPack - 1) * (mtu - 2) + (mtu - dataHead);

        // 计算有多少数据片  78
        if (dataByteLength % dataPieceTotalByteLength == 0) {
            dataPackTotalPieceLength = dataByteLength / dataPieceTotalByteLength;
        } else {
            dataPackTotalPieceLength = dataByteLength / dataPieceTotalByteLength + 1;
        }
        // 计算最后一片数据的字节1645，占用多少数据包7
        int endPieceTotalByteLength = dataByte.length - (dataPackTotalPieceLength - 1) * dataPieceTotalByteLength;
        if (endPieceTotalByteLength <= (mtu - dataHead)) {
            dataPieceEndPack = 1;
        } else {
            final int paceNum = (endPieceTotalByteLength - (mtu - dataHead)) / (mtu - 2);
            if ((endPieceTotalByteLength - (mtu - dataHead)) % (mtu - 2) == 0) {
                dataPieceEndPack = 1 + paceNum;
            } else {
                dataPieceEndPack = 1 + paceNum + 1;
            }
        }
    }

    private byte[] getBytes(String filePath) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    private byte[] getBytesByAssets(Context context, String name) {
        byte[] buffer = null;
        try {
            InputStream fis = context.getAssets().open(name);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }
}


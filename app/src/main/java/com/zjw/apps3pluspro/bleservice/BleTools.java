package com.zjw.apps3pluspro.bleservice;

import android.content.Context;
import android.util.Log;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.module.home.entity.SleepData;
import com.zjw.apps3pluspro.module.home.entity.SleepModel;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.entity.SleepInfo;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.DialMarketManager;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.NewTimeUtils;
import com.zjw.apps3pluspro.utils.PageManager;
import com.zjw.apps3pluspro.utils.ThemeUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by zjw on 2018/3/9.
 */

public class BleTools {
    private static final String TAG = BleTools.class.getSimpleName();

    // 将指定byte数组以16进制的形式打印
    public static String printHexString(byte[] b) {


        String result = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            result += hex.toUpperCase() + "-";
        }
        return result;
    }

    public static float hexToFloat(String str) {
        return Float.intBitsToFloat(new BigInteger(str, 16).intValue());
    }


    /**
     * byte[] 转 十六进制字符串-为了打印原始数据
     *
     * @param data
     * @return
     */
    public static String bytes2HexString(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length);
        if (data != null && data.length > 0) {
            for (byte byteChar : data)
                sb.append(String.format("%02X ", byteChar));
        }
        return sb.toString();
    }

    /**
     * 将十六进制的字符串转换成字节
     *
     * @param commandStr 7E 18 00 07 00 04 01 02 03 04 00 05 00 1A 7E
     * @return
     * @throws NumberFormatException
     */
    public static byte[] hexString2bytes(String commandStr) throws NumberFormatException {
        String[] tempStr = commandStr.split(" ");
        byte[] commands = new byte[tempStr.length];
        for (int i = 0; i < tempStr.length; i++) {
            try {
                commands[i] = (byte) Integer.parseInt(tempStr[i], 16);
            } catch (Exception o_o) {
                commands[i] = 00;
                Log.e("命令转换出错", tempStr[i]);
            }
        }
        return commands;
    }

    public static float byte2float(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }

    public static float getFloat(byte[] b) {
        int accum = 0;
        accum = accum | (b[0] & 0xff) << 0;
        accum = accum | (b[1] & 0xff) << 8;
        accum = accum | (b[2] & 0xff) << 16;
        accum = accum | (b[3] & 0xff) << 24;
        System.out.println(accum);
        return Float.intBitsToFloat(accum);
    }

    public static byte[] float2byte(float f) {
        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }
        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }
        return dest;
    }

    /**
     * 将一个int数字转换为二进制的字符串形式。
     *
     * @param num    需要转换的int类型数据
     * @param digits 要转换的二进制位数，位数不足则在前面补0
     * @return 二进制的字符串形式
     */
    public static String toBinary(int num, int digits) {
        int value = 1 << digits | num;
        String bs = Integer.toBinaryString(value); //0x20 | 这个是为了保证这个string长度是6位数
        return bs.substring(1);
    }


    public static boolean isRightfulnessTime(String date) {
        boolean isRightfulnessTime = false;
        long time = 0;
        try {
            if (date.length() == NewTimeUtils.TIME_YYYY_MM_DD.length()) {
                time = NewTimeUtils.getLongTime(date, NewTimeUtils.TIME_YYYY_MM_DD);
            } else if (date.length() == NewTimeUtils.TIME_YYYY_MM_DD_HHMMSS.length()) {
                time = NewTimeUtils.getLongTime(date, NewTimeUtils.TIME_YYYY_MM_DD_HHMMSS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (time == 0) {
            return true;
        } else {
            long curTime = System.currentTimeMillis();
            if (curTime - time < 30 * 24 * 3600 * 1000L && curTime - time > (-24 * 3600 * 1000L)) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * /当解析年份小于2017或者月小于等于0或者日小于等于0的时候，强制变成这个默认日期
     * 否则 则 返回正确日期
     *
     * @param data
     * @return
     */
    public static String getDate(byte[] data) {
        int Ayear, Amon, Aday;
        Ayear = ((data[13] & 0x7E) >> 1);
        Amon = ((data[13] & 0x1) << 3) | ((data[14] & 0xE0) >> 5);
        Aday = data[14] & 0x1F;

        MyLog.i(TAG, "getDate 数据解析 = Ayear = " + Ayear + "  Amon = " + Amon + "  Aday = " + Aday);

        if (Ayear >= 17 && Amon > 0 && Aday > 0) {
            String AmonStr, AdayStr;
            if (Aday < 10) {
                AdayStr = "0" + Aday;
            } else {
                AdayStr = "" + Aday;
            }
            if (Amon < 10) {
                AmonStr = "0" + Amon;
            } else {
                AmonStr = "" + Amon;
            }
            return "20" + Ayear + "-" + AmonStr + "-" + AdayStr;
        } else {
            return Constants.DEVICE_DEFULT_DATE;
        }
    }

    /**
     * 解析日期
     *
     * @param data
     * @return
     */
    public static String geBpTime(byte[] data) {
        int Ayear = ((data[0] & 0xfc) >> 2);
        int Amon = ((data[0] & 0x03) << 2) | ((data[1] & 0xc0) >> 6);
        int Aday = ((data[1] & 0x3e) >> 1);
        int Ahour = ((data[1] & 0x01) << 4) | ((data[2] & 0xf0) >> 4);
        int Amin = ((data[2] & 0x0f) << 2) | ((data[3] & 0xc0) >> 6);
        int Asec = data[3] & 0x3f;

        String AYearStr, AmonStr, AdayStr, AhourStr, AminStr, AsecStr;

        if (Ayear < 10) {
            AYearStr = "0" + String.valueOf(Ayear);
        } else {
            AYearStr = String.valueOf(Ayear);
        }

        if (Aday < 10) {
            AdayStr = "0" + String.valueOf(Aday);
        } else {
            AdayStr = String.valueOf(Aday);
        }
        if (Amon < 10) {
            AmonStr = "0" + String.valueOf(Amon);
        } else {
            AmonStr = String.valueOf(Amon);
        }

        if (Ahour < 10) {
            AhourStr = "0" + String.valueOf(Ahour);
        } else {
            AhourStr = String.valueOf(Ahour);
        }

        if (Amin < 10) {
            AminStr = "0" + String.valueOf(Amin);
        } else {
            AminStr = String.valueOf(Amin);
        }

        if (Asec < 10) {
            AsecStr = "0" + String.valueOf(Asec);
        } else {
            AsecStr = String.valueOf(Asec);
        }

        return "20" + AYearStr + "-" + AmonStr + "-" + AdayStr + " " + AhourStr + ":" + AminStr + ":" + AsecStr;
    }


    /**
     * 老的距离算法
     *
     * @param sportStep
     * @return
     */
    public static float getOldDistance(int sportStep) {

        float height = DefaultVale.USER_HEIGHT;

        float bleDistance = 0;

        bleDistance = (float) (height * 32 * sportStep * 0.00001 * 10 * 0.001);
//        bleDistance = (float) ((int) (height * 32 * sportStep * 0.00001)) / 100;

        return bleDistance;
    }

    /**
     * 距离算法1
     *
     * @param height
     * @param sportStep
     * @return
     */
    public static float getOneDistance(float height, int sportStep) {
        float bleDistance = 0;

        bleDistance = (float) (height * 32 * sportStep * 0.00001 * 10 * 0.001);
//        bleDistance = (float) ((int) (height * 32 * sportStep * 0.00001)) / 100;

        return bleDistance;
    }


    /**
     * 距离算法2
     *
     * @param height
     * @param sportStep
     * @return
     */
    public static float getTwoDistance(float height, int sportStep) {
        float bleDistance = 0;
        bleDistance = (float) (height * 41 * sportStep * 0.00001 * 10 * 0.001);
        return bleDistance;
    }


    //老的卡路里算法
    public static float getOldCalory(int sportStep) {
        float height = DefaultVale.USER_HEIGHT;
        float weight = DefaultVale.USER_WEIGHT;
        float bleCalory = 0;
        bleCalory = (float) (weight * 1.036 * height * 0.32 * sportStep * 0.00001);
        return bleCalory;
    }

    //卡路里算法1
    public static float getOneCalory(float height, float weight, int sportStep) {
        float bleCalory = 0;
        bleCalory = (float) (weight * 1.036 * height * 0.32 * sportStep * 0.00001);
        return bleCalory;
    }

    /**
     * 卡路里算法2
     *
     * @param height
     * @param weight
     * @param sportStep
     * @return
     */
    public static float getTwoCalory(float height, float weight, int sportStep) {
        float bleCalory = 0;
        bleCalory = (float) (weight * 1.036 * height * 0.41 * sportStep * 0.00001);
        return bleCalory;
    }


    /**
     * 获取英制距离
     *
     * @param sportStep_str
     * @return
     */
    public static String getBritishSystem(String sportStep_str) {

        int sportStep = Integer.valueOf(sportStep_str);

        UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

        int height = 170;
        if (mUserSetTools.get_user_height() != 0) {
            height = mUserSetTools.get_user_height();
        }

        String Distance = "0";

        if (mBleDeviceTools.get_step_algorithm_type() == 1) {
            MyLog.i(TAG, "getBritishSystem 固件返回值 = 算法1");
//            Distance = AppUtils.GetFormat(3, BleTools.getOneDistance(height, sportStep) - 0.005f);
//            Distance = String.valueOf(BleTools.getOneDistance(height, sportStep) - 0.005f);
//            Distance = String.valueOf(BleTools.getOneDistance(height, sportStep));
            Distance = AppUtils.GetTwoFormat(BleTools.getOneDistance(height, sportStep) / 1.61f);
        } else if (mBleDeviceTools.get_step_algorithm_type() == 2) {
            MyLog.i(TAG, "getBritishSystem 固件返回值 = 算法2");
//            Distance = AppUtils.GetFormat(3, BleTools.getTwoDistance(height, sportStep) - 0.005f);
//            Distance = String.valueOf(BleTools.getTwoDistance(height, sportStep) - 0.005f);
//            Distance = String.valueOf(BleTools.getTwoDistance(height, sportStep));
            Distance = AppUtils.GetTwoFormat(BleTools.getTwoDistance(height, sportStep) / 1.61f);
        } else {
            MyLog.i(TAG, "getBritishSystem 固件返回值 = 算法0");
//            Distance = AppUtils.GetFormat(3, BleTools.getOldDistance(sportStep) - 0.005f);
//            Distance = AppUtils.GetFormat(3, BleTools.getOldDistance(sportStep) - 0.005f);
//            Distance = String.valueOf(BleTools.getOldDistance(sportStep) - 0.005f);
//            Distance = String.valueOf(BleTools.getOldDistance(sportStep));
            Distance = AppUtils.GetTwoFormat(BleTools.getOldDistance(sportStep) / 1.61f);
        }

        return Distance;

    }

    /**
     * 体表温度算法一（摄氏度）
     *
     * @param tempValue 温度参数
     * @return
     */
    public static float getCentigradeBody(int tempValue) {
        float bleBodyTemp = 0;

        int value1 = 300 + tempValue;
        bleBodyTemp = (float) (value1) / 10;

        return bleBodyTemp;
    }

    /**
     * 体表温度算法一（华氏度）
     *
     * @param tempValue 温度参数
     * @return
     */
    public static float getFahrenheitBody(int tempValue) {
        float bleBodyTemp = 0;

        int value1 = 300 + tempValue;
        int value2 = (value1) * 9 / 5 + 320;
        bleBodyTemp = (float) value2 / 10;

        return bleBodyTemp;
    }


    /**
     * 判断睡眠是否合法2
     *
     * @param mSleepInfo
     * @return
     */
    public static boolean isSleepSuccessTwo(SleepInfo mSleepInfo) {
        boolean result = true;

        mSleepInfo.getData();


        if (mSleepInfo != null) {
            if (!JavaUtil.checkIsNull(mSleepInfo.getData())) {

                SleepModel mSleepModel = new SleepModel(mSleepInfo);

                List<SleepData> list = mSleepModel.getSleepListData();


                if (list != null && list.size() >= 1) {

                    if (list.size() <= 10) {

                        for (int i = 0; i < list.size() - 1; i++) {

                            if (list.get(i).getSleep_type().equals("3")) {

                                String time = MyTime.TotalTime2(list.get(i).getStartTime(), list.get(i + 1).getStartTime());

                                if (Integer.valueOf(time) >= 240) {
                                    result = false;
                                }
                            }
                        }
                    }


                }
            }
        }


        return result;

    }


    public static String FiltrationOne(String str) {
        String result = "";
        int lenght = 14;
        char c[] = str.toCharArray();

        for (int i = 0; i < c.length; i++) {

            byte[] hexCharaaa = String.valueOf(c[i]).getBytes();
            lenght += hexCharaaa.length;
//           MyLog.i(TAG,"新方法长度 = i = " + i + "  abc = " + hexCharaaa.length + " str = " + String.valueOf(c[i]));

            if (lenght > 100) {
                break;
            }

            result += String.valueOf(c[i]);

        }

//       MyLog.i(TAG,"新方法长度 lenght = " + lenght);
//       MyLog.i(TAG,"新方法长度 result = " + result);

        return result;

    }

    public static String FiltrationTwo(String str) {
        String result = "";
        int lenght = 14;
        char c[] = str.toCharArray();

        for (int i = 0; i < c.length; i++) {

            byte[] hexCharaaa = String.valueOf(c[i]).getBytes();
            lenght += hexCharaaa.length;
//           MyLog.i(TAG,"新方法长度 = i = " + i + "  abc = " + hexCharaaa.length + " str = " + String.valueOf(c[i]));

            if (lenght > 250) {
                break;
            }

            result += String.valueOf(c[i]);

        }

//       MyLog.i(TAG,"新方法长度 lenght = " + lenght);
//       MyLog.i(TAG,"新方法长度 result = " + result);

        return result;

    }

    //================校验检查=====================

    /**
     * 校验收到的完整数据包是否正确
     *
     * @param mBtRecData
     * @return
     */
    public static boolean checkData(byte[] mBtRecData) {
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
//        if (mBleDeviceTools.getIsSupportBigPage()) {
//            return checkBigData(mBtRecData);
//        } else {
//            return checkOldData(mBtRecData);
//        }
        if (mBleDeviceTools.getSupportNewDeviceCrc()) {
            return checkBigData(mBtRecData);
        } else {
            return checkOldData(mBtRecData);
        }
    }


    /**
     * 校验收到的完整数据包是否正确
     *
     * @param mBtRecData
     * @return
     */
    public static boolean checkOldData(byte[] mBtRecData) {
        byte checkNum = mBtRecData[4];
        int start = 13;
        byte crc = 0;
        int length = (mBtRecData[3] & 0xFF) - 5;
        for (int i = start; i < start + length; i++) {
            crc = getCheckNum(mBtRecData[i], crc);
        }
//        MyLog.i(TAG, "校验结果 length:" + length);
//        MyLog.i(TAG, "校验结果 checkNum:" + BleTools.byteTo16(checkNum));
//        MyLog.i(TAG, "校验结果 crc:" + BleTools.byteTo16(crc));
        if (checkNum == crc) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 校验收到的完整数据包是否正确
     *
     * @param mBtRecData
     * @return
     */
    public static boolean checkNewData(byte[] mBtRecData) {
        byte checkNum = mBtRecData[4];
        int start = 13;
        byte crc = 0;
        int length = (mBtRecData[3] & 0xFF) - 5;
        for (int i = start; i < start + length; i++) {
            crc = getCheckNum(mBtRecData[i], crc);
        }
//        MyLog.i(TAG, "校验结果 length:" + length);
//        MyLog.i(TAG, "校验结果 checkNum:" + BleTools.byteTo16(checkNum));
//        MyLog.i(TAG, "校验结果 crc:" + BleTools.byteTo16(crc));
        if (checkNum == crc) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 根据校验规则，得到一个校验码
     *
     * @param value
     * @param crc
     * @return
     */
    public static byte getCheckNum(byte value, byte crc) {
        byte polynomial = (byte) 0x97;
        crc ^= value;

        for (int i = 0; i < 8; i++) {
            if ((crc & 0x80) != 0) {
                crc <<= 1;
                crc ^= polynomial;
            } else {
                crc <<= 1;
            }
        }
        return crc;
    }

    public static byte[] getCrcData(byte[] buf) {
        int len = ((buf[2] & 0xff) << 8) | (buf[3] & 0xff) + 8;
        buf = ThemeUtils.subBytes(buf, 0, len);
        len = buf.length;
        byte[] data = new byte[len - 4];
        for (int i = 0; i < data.length - 4; i++) {
            data[i] = buf[i + 8];
        }
        data[len - 8] = buf[4];
        data[len - 7] = buf[5];
        data[len - 6] = buf[6];
        data[len - 5] = buf[7];
        return data;
    }

    /**
     * 新校验方法
     *
     * @param buf
     * @return
     */
    public static boolean checkBigData(byte[] buf) {
        byte[] data = getCrcData(buf);
//        MyLog.i(TAG, "checkBigData = data = " + BleTools.printHexString(data));
        int crc32 = 0xFFFFFFFF;
        for (int i = 0; i < data.length; i++) {
            byte b = data[i];
            for (int j = 0; j < 8; j++) {
                int need_xor = ((crc32 & 0x80000000) >>> 31) ^ ((b & 0x80) >>> 7);
                crc32 <<= 1;
                if (need_xor == 1) {
                    crc32 ^= 0x14C11DB7; // CRC32-CCITT polynomial
                }
                b <<= 1;
            }
        }
        if (crc32 == 0) {
            return true;
        } else {
            return false;
        }
    }


    public static String getDeviceVersionName(BleDeviceTools mBleDeviceTools) {

        String result = "--";

        int device_type = mBleDeviceTools.get_ble_device_type();
        int device_version = mBleDeviceTools.get_ble_device_version();

        String qianzui = "N";

        if (device_type > 10000 && device_type < 20000) {
            device_type = device_type - 10000;
            qianzui = "R";
        } else if (device_type > 20000 && device_type < 30000) {
            device_type = device_type - 20000;
            qianzui = "D";
        }


        if (device_type >= 1 && device_version >= 1) {
            String re0 = String.valueOf(device_type);
            String re1 = String.valueOf(1 + device_version / 10);
            String re2 = String.valueOf(device_version % 10);
//            result = "V" + re0 + "." + re1 + "." + re2;
            result = qianzui + re0 + "." + re1 + "." + re2;
        }

        return result;

    }

    /**
     * Ecg数据解析
     *
     * @param data
     * @return
     */
    public static int[] getSnData(byte[] data) {


        int[] str01 = new int[(data.length - 2) / 2];

        for (int i = 2; i < data.length; i += 2) {

            str01[(i / 2 - 1)] = (int) ((data[i] & 0xFF) << 8) | ((data[i + 1] & 0xFF));

        }


        return str01;
    }

    public static String getMacAddress(byte[] data) {

        if (data != null && data.length == 6) {


            StringBuilder sb = new StringBuilder();
            String tmp = null;
            for (int i = 0; i < data.length; i++) {
                // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
                tmp = Integer.toHexString(0xFF & data[i]);
                if (tmp.length() == 1)// 每个字节8为，转为16进制标志，2个16进制位
                {
                    tmp = "0" + tmp;
                }

                if (i == data.length - 1) {
                    sb.append(tmp);
                } else {
                    sb.append(tmp + ":");
                }


            }
            return sb.toString();

        } else {
            return "";
        }
    }

    public static String getBleName(byte[] data) {
        String nRcvString = "";

        if (data != null && data.length > 0) {

            int tRecvCount = data.length;

            StringBuffer tStringBuf = new StringBuffer();

            char[] tChars = new char[tRecvCount];

            for (int i = 0; i < tRecvCount; i++) {
                tChars[i] = (char) data[i];
            }

            tStringBuf.append(tChars);

            nRcvString = tStringBuf.toString();
        }


        return nRcvString;

    }

    public static long t1 = 0;

    public static boolean checkContinuousClick() {
        boolean rsult = true;

        if (t1 == 0) {//第一次单击，初始化为本次单击的时间
            t1 = (new Date()).getTime();
        } else {
            long curTime = (new Date()).getTime();//本地单击的时间

            long differenceValue = curTime - t1;
            differenceValue = Math.abs(differenceValue);
            if (differenceValue > 500) {
                //间隔5秒允许点击，可以根据需要修改间隔时间
                t1 = curTime;//当前单击事件变为上次时间
                MyLog.i(TAG, "找设备 == 111");
                rsult = true;
            } else {
                MyLog.i(TAG, "找设备 == 222");
                rsult = false;
            }
        }
        return rsult;
    }
}

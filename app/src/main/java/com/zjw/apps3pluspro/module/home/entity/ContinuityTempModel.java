package com.zjw.apps3pluspro.module.home.entity;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.bleservice.BleTools;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sql.entity.ContinuityTempInfo;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.log.MyLog;

public class ContinuityTempModel {

    private static final String TAG = ContinuityTempModel.class.getSimpleName();

    String ContinuityTempDate;//日期
    String ContinuityTempData;//原始数据
    String ContinuityTempDayBodyMax;//全天最大体温
    String ContinuityTempDayBodyMin;//全天最小体温

    public String getContinuityTempDate() {
        return ContinuityTempDate;
    }

    public void setContinuityTempDate(String continuityTempDate) {
        ContinuityTempDate = continuityTempDate;
    }

    public String getContinuityTempData() {
        return ContinuityTempData;
    }

    public void setContinuityTempData(String continuityTempData) {
        ContinuityTempData = continuityTempData;
    }

    public String getContinuityTempDayBodyMax() {
        return ContinuityTempDayBodyMax;
    }

    public void setContinuityTempDayBodyMax(String continuityTempDayBodyMax) {
        ContinuityTempDayBodyMax = continuityTempDayBodyMax;
    }

    public String getContinuityTempDayBodyMin() {
        return ContinuityTempDayBodyMin;
    }

    public void setContinuityTempDayBodyMin(String continuityTempDayBodyMin) {
        ContinuityTempDayBodyMin = continuityTempDayBodyMin;
    }




    //连续体温
    public ContinuityTempModel(ContinuityTempInfo mContinuityTempInfo) {
        super();
        String data = mContinuityTempInfo.getData();

        String[] data_list = data.split(",");

        int day_max = 0;
        int day_min = 255;


        for (int i = 0; i < data_list.length; i++) {

            int number = Integer.valueOf(data_list[i]);

            if (number > 0) {


                if (number > day_max) {
                    day_max = number;
                }

                if (number < day_min) {
                    day_min = number;
                }

            }
        }


        MyLog.i("ContinuityTempModel", " day_max= " + day_max);
        MyLog.i("ContinuityTempModel", " day_min= " + day_min);

        setContinuityTempData(mContinuityTempInfo.getData());
        setContinuityTempDate(mContinuityTempInfo.getDate());


        if (day_max != 0) {
            setContinuityTempDayBodyMax(String.valueOf(getBodyValue(day_max)));
        } else {
            setContinuityTempDayBodyMax("0");
        }

        if (day_min != 255) {
            setContinuityTempDayBodyMin(String.valueOf(getBodyValue(day_min)));
        } else {
            setContinuityTempDayBodyMin("0");
        }

    }




    public static String getBodyValue(int valueNumber) {

        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

        String result = "0";

        if (mBleDeviceTools.getTemperatureType() == 1) {
            result = String.valueOf(BleTools.getFahrenheitBody(valueNumber));
        } else {
            result = String.valueOf(BleTools.getCentigradeBody(valueNumber));

        }

        return result;

    }

    public String getBodyListData() {

        String result = "";

        if (!JavaUtil.checkIsNull(ContinuityTempData)) {
            String[] list = ContinuityTempData.split(",");
            for (int i = 0; i < list.length; i++) {

                int valueNumber = 0;

                try {
                    valueNumber = Integer.valueOf(list[i]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (valueNumber > 0) {
                    result += getBodyValue(valueNumber);
                } else {
                    result += "0";
                }
                if (i != list.length - 1) {
                    result += ",";
                }
            }
        }
        return result;
    }


    public String getLastBodyValue() {


        String result = "0";

        String data = getBodyListData();

        MyLog.i(TAG, "getLastBodyValue data = " + data);

        if (!JavaUtil.checkIsNull(data)) {


            String[] list = data.split(",");
//            String[] list = ContinuityTempData.split(",");

            for (int i = list.length - 1; i >= 0; i--) {

                float valueNumber = 0;

                try {
                    valueNumber = Float.valueOf(list[i]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (valueNumber > 0) {
                    result = String.valueOf(valueNumber);
                    break;
                }


            }

        }

        return result;
    }


    @Override
    public String toString() {
        return "ContinuityTempModel{" +
                "ContinuityTempDate='" + ContinuityTempDate + '\'' +
                ", ContinuityTempData='" + ContinuityTempData + '\'' +
                ", ContinuityTempDayBodyMax='" + ContinuityTempDayBodyMax + '\'' +
                ", ContinuityTempDayBodyMin='" + ContinuityTempDayBodyMin + '\'' +
                '}';
    }
}

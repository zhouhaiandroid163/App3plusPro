package com.zjw.apps3pluspro.module.home.entity;

import com.zjw.apps3pluspro.sql.entity.HeartInfo;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

public class HeartModel {

    String HeartDate;//日期
    String HeartData;//原始数据
    String HeartDayMax;//全天最大
    String HeartDayMin;//全天最小
    String HeartDayAverage;//全天平均心率
    String HeartSleepAverage;//睡眠平均心率

    public HeartModel() {
        super();
    }

    public String getHeartDate() {
        return HeartDate;
    }

    public void setHeartDate(String heartDate) {
        HeartDate = heartDate;
    }

    public String getHeartData() {
        return HeartData;
    }

    public void setHeartData(String heartData) {
        HeartData = heartData;
    }

    public String getHeartDayMax() {
        return HeartDayMax;
    }

    public void setHeartDayMax(String heartDayMax) {
        HeartDayMax = heartDayMax;
    }

    public String getHeartDayMin() {
        return HeartDayMin;
    }

    public void setHeartDayMin(String heartDayMin) {
        HeartDayMin = heartDayMin;
    }

    public String getHeartDayAverage() {
        return HeartDayAverage;
    }

    public void setHeartDayAverage(String heartDayAverage) {
        HeartDayAverage = heartDayAverage;
    }

    public String getHeartSleepAverage() {
        return HeartSleepAverage;
    }

    public void setHeartSleepAverage(String heartSleepAverage) {
        HeartSleepAverage = heartSleepAverage;
    }

    public HeartModel(HeartInfo mHeartInfo) {
        super();
        String po_heart_data = mHeartInfo.getData();

        String[] po_heart_list = po_heart_data.split(",");
        List day_list = new ArrayList();
        List sleep_list = new ArrayList();

        int day_max = 0;
        int day_min = 255;
        int day_avg = 0;
        int sleep_avg = 0;

        int sleep_heart_size = 8;

        if (mHeartInfo.getData_type().equals("0")) {
            sleep_heart_size = 8;
        } else if (mHeartInfo.getData_type().equals("1")) {
            sleep_heart_size = 96;
        }


        for (int i = 0; i < po_heart_list.length; i++) {

            int number = Integer.valueOf(po_heart_list[i]);

            if (number > 0) {
                if (i < sleep_heart_size) {
                    sleep_list.add(number);

                }

                if (number > day_max) {
                    day_max = number;
                }

                if (number != 0) {

                    if (number < day_min) {
                        day_min = number;
                    }
                }


                day_list.add(number);
            }

        }


        day_avg = MyUtils.getHeartAvg(day_list);
        sleep_avg = MyUtils.getHeartAvg(sleep_list);

        setHeartDate(mHeartInfo.getDate());
        setHeartData(po_heart_data);
        setHeartDayAverage(String.valueOf(day_avg));
        setHeartSleepAverage(String.valueOf(sleep_avg));
        setHeartDayMax(String.valueOf(day_max));
        if (day_min != 255) {
            setHeartDayMin(String.valueOf(day_min));
        } else {
            setHeartDayMin("0");
        }

    }

    public String getLastHeart() {

        String result = "0";
        if (!JavaUtil.checkIsNull(HeartData)) {

            boolean is_null = true;

            String[] list = HeartData.split(",");
            for (int i = list.length - 1; i >= 0; i--) {
                if (Integer.valueOf(list[i]) > 0) {
                    is_null = false;
//                    tv_home_last_heart.setText();
                    result = list[i];
                    break;
                }
            }

        }

        return result;
    }

    public int getLastHeartIndex() {
        int result = -1;
        if (!JavaUtil.checkIsNull(HeartData)) {
            String[] list = HeartData.split(",");
            for (int i = list.length - 1; i >= 0; i--) {
                if (Integer.valueOf(list[i]) > 0) {
                    result = i;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "HeartModel{" +
                "HeartDate='" + HeartDate + '\'' +
                ", HeartData='" + HeartData + '\'' +
                ", HeartDayMax='" + HeartDayMax + '\'' +
                ", HeartDayMin='" + HeartDayMin + '\'' +
                ", HeartDayAverage='" + HeartDayAverage + '\'' +
                ", HeartSleepAverage='" + HeartSleepAverage + '\'' +
                '}';
    }
}

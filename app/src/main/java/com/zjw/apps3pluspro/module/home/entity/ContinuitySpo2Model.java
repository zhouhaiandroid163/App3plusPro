package com.zjw.apps3pluspro.module.home.entity;

import com.zjw.apps3pluspro.sql.entity.ContinuitySpo2Info;
import com.zjw.apps3pluspro.utils.JavaUtil;

public class ContinuitySpo2Model {

    String ContinuitySpo2Date;//日期
    String ContinuitySpo2Data;//原始数据
    String ContinuitySpo2DayMax;//全天最大
    String ContinuitySpo2DayMin;//全天最小


    public String getContinuitySpo2Date() {
        return ContinuitySpo2Date;
    }

    public void setContinuitySpo2Date(String continuitySpo2Date) {
        ContinuitySpo2Date = continuitySpo2Date;
    }

    public String getContinuitySpo2Data() {
        return ContinuitySpo2Data;
    }

    public void setContinuitySpo2Data(String continuitySpo2Data) {
        ContinuitySpo2Data = continuitySpo2Data;
    }

    public String getContinuitySpo2DayMax() {
        return ContinuitySpo2DayMax;
    }

    public void setContinuitySpo2DayMax(String continuitySpo2DayMax) {
        ContinuitySpo2DayMax = continuitySpo2DayMax;
    }

    public String getContinuitySpo2DayMin() {
        return ContinuitySpo2DayMin;
    }

    public void setContinuitySpo2DayMin(String continuitySpo2DayMin) {
        ContinuitySpo2DayMin = continuitySpo2DayMin;
    }

    public ContinuitySpo2Model(ContinuitySpo2Info mContinuitySpo2Info) {
        super();
        String data = mContinuitySpo2Info.getData();

        String[] data_list = data.split(",");

        int day_max = 0;
        int day_min = 255;


        for (int i = 0; i < data_list.length; i++) {

            int number = Integer.valueOf(data_list[i]);

            if (number > 0) {


                if (number > day_max) {
                    day_max = number;
                }

                if (number != 0) {
                    if (number < day_min) {
                        day_min = number;
                    }
                }

            }

        }

        setContinuitySpo2Date(mContinuitySpo2Info.getDate());
        setContinuitySpo2Data(data);
        setContinuitySpo2DayMax(String.valueOf(day_max));

        if (day_min != 255) {
            setContinuitySpo2DayMin(String.valueOf(day_min));
        } else {
            setContinuitySpo2DayMin("0");
        }

    }

    public String getLastValue() {

        String result = "0";
        if (!JavaUtil.checkIsNull(ContinuitySpo2Data)) {

            boolean is_null = true;

            String[] list = ContinuitySpo2Data.split(",");
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


    @Override
    public String toString() {
        return "ContinuitySpo2Model{" +
                "ContinuitySpo2Date='" + ContinuitySpo2Date + '\'' +
                ", ContinuitySpo2Data='" + ContinuitySpo2Data + '\'' +
                ", ContinuitySpo2DayMax='" + ContinuitySpo2DayMax + '\'' +
                ", ContinuitySpo2DayMin='" + ContinuitySpo2DayMin + '\'' +
                '}';
    }
}

package com.zjw.apps3pluspro.module.home.entity;

import com.zjw.apps3pluspro.sql.entity.MeasureTempInfo;

public class MeasureTempModel {

    String MeasureTempTime;//测量时间
    String MeasureTempDayBody;//体温


    public String getMeasureTempTime() {
        return MeasureTempTime;
    }

    public void setMeasureTempTime(String measureTempTime) {
        MeasureTempTime = measureTempTime;
    }

    public String getMeasureTempDayBody() {
        return MeasureTempDayBody;
    }

    public void setMeasureTempDayBody(String measureTempDayBody) {
        MeasureTempDayBody = measureTempDayBody;
    }




    public MeasureTempModel(MeasureTempInfo mMeasureTempInfo) {
        super();
        int day_value = Integer.valueOf(mMeasureTempInfo.getMeasure_wrist_temp());
        setMeasureTempDayBody(ContinuityTempModel.getBodyValue(day_value));
        setMeasureTempTime(String.valueOf(mMeasureTempInfo.getMeasure_time()));
    }


    @Override
    public String toString() {
        return "MeasureTempModel{" +
                "MeasureTempTime='" + MeasureTempTime + '\'' +
                ", MeasureTempDayBody='" + MeasureTempDayBody + '\'' +
                '}';
    }
}

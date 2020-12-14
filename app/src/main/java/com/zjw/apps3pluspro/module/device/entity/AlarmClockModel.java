package com.zjw.apps3pluspro.module.device.entity;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.zjw.apps3pluspro.module.device.reminde.RemindeUtils;



public class AlarmClockModel implements Parcelable {


    public int id;
    public String timeAlarm;
    public int repeat;
    public String weekDay;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimeAlarm() {
        return timeAlarm;
    }

    public void setTimeAlarm(String timeAlarm) {
        this.timeAlarm = timeAlarm;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }


    public AlarmClockModel() {
        super();
    }


    @Override
    public String toString() {
        return "AlarmClockModel{" +
                "id=" + id +
                ", timeAlarm='" + timeAlarm + '\'' +
                ", repeat=" + repeat +
                ", weekDay='" + weekDay + '\'' +
                '}';
    }

    /**
     * 获取默认闹钟
     * 仅一次=打开
     * @param context
     * @return
     */
    public static AlarmClockModel getDefultAlarmClockModel(Context context) {
        String isTime = RemindeUtils.getDefultTime();
        AlarmClockModel mAlarmClockModel = new AlarmClockModel();
        mAlarmClockModel.setWeekDay("10000000");
        mAlarmClockModel.setRepeat(128);
        mAlarmClockModel.setId(RemindeUtils.getDataId(context));
        mAlarmClockModel.setTimeAlarm(isTime);

        return mAlarmClockModel;
    }


    //===========================为了传输对象做的===================

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {


        dest.writeInt(id);
        dest.writeString(timeAlarm);
        dest.writeInt(repeat);
        dest.writeString(weekDay);


    }

    protected AlarmClockModel(Parcel in) {


        id = in.readInt();
        timeAlarm = in.readString();
        repeat = in.readInt();
        weekDay = in.readString();
    }


    public static final Parcelable.Creator<AlarmClockModel> CREATOR = new Creator<AlarmClockModel>() {
        @Override
        public AlarmClockModel createFromParcel(Parcel in) {
            return new AlarmClockModel(in);
        }

        @Override
        public AlarmClockModel[] newArray(int size) {
            return new AlarmClockModel[size];
        }
    };


}

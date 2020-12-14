package com.zjw.apps3pluspro.module.device.entity;

import android.os.Parcel;
import android.os.Parcelable;


public class ClockDialCustomModel implements Parcelable {


    String binName = "";
    String bgName = "";
    String textName = "";

    public String getBinName() {
        return binName;
    }

    public void setBinName(String binName) {
        this.binName = binName;
    }

    public String getBgName() {
        return bgName;
    }

    public void setBgName(String bgName) {
        this.bgName = bgName;
    }

    public String getTextName() {
        return textName;
    }

    public void setTextName(String textName) {
        this.textName = textName;
    }

    public ClockDialCustomModel() {
        super();
    }

    public ClockDialCustomModel(String bin_name, String bg_name, String text_name) {
        super();
        setBinName(bin_name);
        setBgName(bg_name);
        setTextName(text_name);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(binName);
        dest.writeString(bgName);
        dest.writeString(textName);
    }

    protected ClockDialCustomModel(Parcel in) {

        binName = in.readString();
        bgName = in.readString();
        textName = in.readString();
    }


    public static final Parcelable.Creator<ClockDialCustomModel> CREATOR = new Creator<ClockDialCustomModel>() {
        @Override
        public ClockDialCustomModel createFromParcel(Parcel in) {
            return new ClockDialCustomModel(in);
        }

        @Override
        public ClockDialCustomModel[] newArray(int size) {
            return new ClockDialCustomModel[size];
        }
    };


}

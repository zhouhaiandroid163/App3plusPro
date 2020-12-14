package com.zjw.apps3pluspro.network.entity;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.MyUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户数据，上传后台需要
 */
public class UserData implements Parcelable {
    private String userId;//用户ID
    private String nikname;//用户名
    private String height;//身高
    private String weight;//体重
    private String birthday;//生日
    private String sex;//性别
    private String skinColor;//肤色。
    private String city;//城市名
    private String appName;//APP名称
    private String appVersion;//APP版本


    public UserData() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getNikname() {
        return nikname;
    }

    public void setNikname(String nikname) {
        this.nikname = nikname;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }


    public String getSkinColor() {
        return skinColor;
    }

    public void setSkinColor(String skinColor) {
        this.skinColor = skinColor;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public static Creator<UserData> getCREATOR() {
        return CREATOR;
    }

    public static JSONObject getUserData(UserData myUserData) {
        JSONObject myJSONObject = new JSONObject();

        //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        String jsonTest = gson.toJson(myUserData, UserData.class);

        try {
            myJSONObject = new JSONObject(jsonTest);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return myJSONObject;


    }


    public UserData(UserSetTools mUserSetTools) {
        setUserId(BaseApplication.getUserId());
        setNikname(mUserSetTools.get_user_nickname());
        setHeight(String.valueOf(mUserSetTools.get_user_height()));
        setWeight(String.valueOf(mUserSetTools.get_user_weight()));
        setSex(String.valueOf(mUserSetTools.get_user_sex()));
        setBirthday(mUserSetTools.get_user_birthday());
        setAppName(MyUtils.getAppName());
        setAppVersion(MyUtils.getAppInfo());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {


        dest.writeString(userId);
        dest.writeString(nikname);
        dest.writeString(height);
        dest.writeString(weight);
        dest.writeString(birthday);
        dest.writeString(sex);
        dest.writeString(skinColor);
    }

    protected UserData(Parcel in) {
        userId = in.readString();
        nikname = in.readString();
        height = in.readString();
        weight = in.readString();
        birthday = in.readString();
        sex = in.readString();
        skinColor = in.readString();

    }


    public static final Creator<UserData> CREATOR = new Creator<UserData>() {
        @Override
        public UserData createFromParcel(Parcel in) {
            return new UserData(in);
        }

        @Override
        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };
}

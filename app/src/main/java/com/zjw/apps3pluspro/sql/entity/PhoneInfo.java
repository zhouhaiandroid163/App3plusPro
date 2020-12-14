package com.zjw.apps3pluspro.sql.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 通讯录表
 * 备注：需要的字段
 * 1.用户ID
 * 2.手机号
 * 3.联系人
 * 4.插入数据库的时间(unix时间戳)
 */
@Entity
public class PhoneInfo implements Parcelable {
    private static final String TAG = PhoneInfo.class.getSimpleName();
    @Id(autoincrement = true)
    private Long _id;
    @NotNull
    /**
     *1.用户ID
     */
    private String user_id;
    /**
     * 2.手机号
     */
    private String phone_number;
    /**
     * 3.联系人
     */
    private String phone_name;
    /**
     * 4.插入数据库的时间(unix时间戳)
     */
    private String warehousing_time;

    @Generated(hash = 1888616992)
    public PhoneInfo(Long _id, @NotNull String user_id, String phone_number,
                     String phone_name, String warehousing_time) {
        this._id = _id;
        this.user_id = user_id;
        this.phone_number = phone_number;
        this.phone_name = phone_name;
        this.warehousing_time = warehousing_time;
    }

    @Generated(hash = 1353546919)
    public PhoneInfo() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPhone_number() {
        return this.phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPhone_name() {
        return this.phone_name;
    }

    public void setPhone_name(String phone_name) {
        this.phone_name = phone_name;
    }

    @Override
    public String toString() {
        return "PhoneInfo{" +
                "_id=" + _id +
                ", user_id='" + user_id + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", phone_name='" + phone_name + '\'' +
                ", warehousing_time='" + warehousing_time + '\'' +
                '}';
    }


    //===================Parcel相关==================

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(phone_number);
        dest.writeString(phone_name);
    }

    public String getWarehousing_time() {
        return this.warehousing_time;
    }

    public void setWarehousing_time(String warehousing_time) {
        this.warehousing_time = warehousing_time;
    }


    protected PhoneInfo(Parcel in) {
        user_id = in.readString();
        phone_number = in.readString();
        phone_name = in.readString();
    }


    public static final Parcelable.Creator<PhoneInfo> CREATOR = new Creator<PhoneInfo>() {
        @Override
        public PhoneInfo createFromParcel(Parcel in) {
            return new PhoneInfo(in);
        }

        @Override
        public PhoneInfo[] newArray(int size) {
            return new PhoneInfo[size];
        }
    };

}

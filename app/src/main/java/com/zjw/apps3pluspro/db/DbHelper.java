package com.zjw.apps3pluspro.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.zjw.apps3pluspro.application.BaseApplication;

public class DbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "benxing.db";
    private static final int DB_VERSION = 1;
    public static String ALARM_TABLE;
    public static String GPS_SPORT_TABLE;
    private static String uid;

    private static DbHelper mInstance = null;

    public synchronized static DbHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DbHelper(context, DB_NAME, null, DB_VERSION);
            uid = BaseApplication.getUserId();
            ALARM_TABLE = "alarm_table" + uid;
            GPS_SPORT_TABLE = "gps_sport_table" + uid;
        }
        return mInstance;
    }

    ;

    public DbHelper(Context context, String name, CursorFactory factory,
                    int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }


    /**
     * @param db
     */


    public void createAlarmTable(SQLiteDatabase db) {
        String sql = "create table " + ALARM_TABLE
                + "(id int,time varchar(16),repeat int,weekDay varchar(30))";
        db.execSQL(sql);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void  creatGpsSportTable(SQLiteDatabase db) {
        String sql = "create table "
                + GPS_SPORT_TABLE
                + "(uid varchar(15),step_value int,calories_value varchar(10),distance varchar(10)," +
                "sport_time varchar(20),with_speed varchar(10),point_data STRING,time date)";
        db.execSQL(sql);
    }



}

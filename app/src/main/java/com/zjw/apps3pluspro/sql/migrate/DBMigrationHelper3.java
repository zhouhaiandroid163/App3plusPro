package com.zjw.apps3pluspro.sql.migrate;

import com.zjw.apps3pluspro.utils.SysUtils;

import org.greenrobot.greendao.database.Database;

/**
 * Created by android
 * on 2020/5/29.
 */
public class DBMigrationHelper3 extends AbstractDBMigratorHelper {

    private static final String TAG = DBMigrationHelper3.class.getSimpleName();

    @Override
    public void onUpgrade(Database db) {

        String constraint = " IF NOT EXISTS ";

        db.execSQL("CREATE TABLE " + constraint + "\"CONTINUITY_SPO2_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"USER_ID\" TEXT NOT NULL ," + // 1: user_id
                "\"DATE\" TEXT," + // 2: date
                "\"DATA\" TEXT," + // 3: data
                "\"WAREHOUSING_TIME\" TEXT," + // 4: warehousing_time
                "\"SYNC_STATE\" TEXT);"); // 5: sync_state


        db.execSQL("CREATE TABLE " + constraint + "\"CONTINUITY_TEMP_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"USER_ID\" TEXT NOT NULL ," + // 1: user_id
                "\"DATE\" TEXT," + // 2: date
                "\"DATA\" TEXT," + // 3: data
                "\"TEMP_DIFFERENCE\" TEXT," + // 4: temp_difference
                "\"WAREHOUSING_TIME\" TEXT," + // 5: warehousing_time
                "\"SYNC_STATE\" TEXT);"); // 6: sync_state

        db.execSQL("CREATE TABLE " + constraint + "\"MEASURE_SPO2_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"USER_ID\" TEXT NOT NULL ," + // 1: user_id
                "\"MEASURE_TIME\" TEXT," + // 2: measure_time
                "\"MEASURE_SPO2\" TEXT," + // 3: measure_spo2
                "\"WAREHOUSING_TIME\" TEXT," + // 4: warehousing_time
                "\"SYNC_STATE\" TEXT);"); // 5: sync_state


        db.execSQL("CREATE TABLE " + constraint + "\"MEASURE_TEMP_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"USER_ID\" TEXT NOT NULL ," + // 1: user_id
                "\"MEASURE_TIME\" TEXT," + // 2: measure_time
                "\"MEASURE_WRIST_TEMP\" TEXT," + // 3: measure_wrist_temp
                "\"MEASURE_TEMP_DIFFERENCE\" TEXT," + // 4: measure_temp_difference
                "\"WAREHOUSING_TIME\" TEXT," + // 5: warehousing_time
                "\"SYNC_STATE\" TEXT);"); // 6: sync_state


        SysUtils.logContentE(TAG, "数据库升级完成，版本 2 升级至 3");
    }
}

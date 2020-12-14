package com.zjw.apps3pluspro.sql.migrate;

import com.zjw.apps3pluspro.utils.SysUtils;

import org.greenrobot.greendao.database.Database;

/**
 * Created by android
 * on 2020/5/29.
 */
public class DBMigrationHelper2 extends AbstractDBMigratorHelper {

    private static final String TAG = DBMigrationHelper2.class.getSimpleName();

    @Override
    public void onUpgrade(Database db) {

        String constraint = " IF NOT EXISTS ";


        db.execSQL("CREATE TABLE " + constraint + "\"PHONE_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"USER_ID\" TEXT NOT NULL ," + // 1: user_id
                "\"PHONE_NUMBER\" TEXT," + // 2: phone_number
                "\"PHONE_NAME\" TEXT);"); //  3: phone_name


        SysUtils.logContentE(TAG, "数据库升级完成，版本 1 升级至 2");
    }
}

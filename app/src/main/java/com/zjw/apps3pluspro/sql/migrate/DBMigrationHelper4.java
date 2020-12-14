package com.zjw.apps3pluspro.sql.migrate;

import com.zjw.apps3pluspro.utils.SysUtils;

import org.greenrobot.greendao.database.Database;

/**
 * Created by android
 * on 2020/5/29.
 */
public class DBMigrationHelper4 extends AbstractDBMigratorHelper {

    private static final String TAG = DBMigrationHelper4.class.getSimpleName();

    @Override
    public void onUpgrade(Database db) {


        //健康表
        DbMigrationUtils.addColumn(db, "HEALTH_INFO", "WAREHOUSING_TIME");
        //心率表
        DbMigrationUtils.addColumn(db, "HEART_INFO", "WAREHOUSING_TIME");
        //运动表-活动记录
        DbMigrationUtils.addColumn(db, "MOVEMENT_INFO", "WAREHOUSING_TIME");
        DbMigrationUtils.addColumn(db, "MOVEMENT_INFO", "HEIGHT");
        DbMigrationUtils.addColumn(db, "MOVEMENT_INFO", "WEIGHT");
        DbMigrationUtils.addColumn(db, "MOVEMENT_INFO", "STEP_ALGORITHM_TYPE");

        String constraint = " IF NOT EXISTS ";
        db.execSQL("CREATE TABLE " + constraint + "\"PHONE_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"USER_ID\" TEXT NOT NULL ," + // 1: user_id
                "\"PHONE_NUMBER\" TEXT," + // 2: phone_number
                "\"PHONE_NAME\" TEXT);"); // 4: warehousing_time

        //通讯录-常用联系人表
        DbMigrationUtils.addColumn(db, "PHONE_INFO", "WAREHOUSING_TIME");
        //睡眠表
        DbMigrationUtils.addColumn(db, "SLEEP_INFO", "WAREHOUSING_TIME");
        //多运动表
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "WAREHOUSING_TIME");

        SysUtils.logContentE(TAG, "数据库升级完成，版本 3 升级至 4");
    }


}

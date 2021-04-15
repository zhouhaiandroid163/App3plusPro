package com.zjw.apps3pluspro.sql.migrate;

import com.zjw.apps3pluspro.utils.SysUtils;

import org.greenrobot.greendao.database.Database;

/**
 * Created by android
 * on 2020/8/11.
 */
public class DBMigrationHelper6 extends AbstractDBMigratorHelper {

    private static final String TAG = DBMigrationHelper6.class.getSimpleName();

    @Override
    public void onUpgrade(Database db) {
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_GPS_ENCRYPTION");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_GPS_VALID1");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "RECORD_GPS_TIME");
        SysUtils.logContentE(TAG, "数据库升级完成，版本 5 升级至 6");
    }
}

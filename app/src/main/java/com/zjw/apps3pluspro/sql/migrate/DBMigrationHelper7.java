package com.zjw.apps3pluspro.sql.migrate;

import com.zjw.apps3pluspro.utils.SysUtils;

import org.greenrobot.greendao.database.Database;

/**
 * Created by android
 * on 2021/3/23.
 */
public class DBMigrationHelper7 extends AbstractDBMigratorHelper {

    private static final String TAG = DBMigrationHelper7.class.getSimpleName();

    @Override
    public void onUpgrade(Database db) {
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_TOTAL_SWIM_NUM");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_SWIM_STYLE");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_MAX_SWIM_FREQUENCY");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_FACE_ABOUT_NUM");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_AVG_SWOLF");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_OPTIMAL_SWOLF");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_POOL_WIDTH");
        SysUtils.logContentE(TAG, "数据库升级完成，版本 6 升级至 7");
    }
}

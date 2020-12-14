package com.zjw.apps3pluspro.sql.migrate;

import com.zjw.apps3pluspro.utils.SysUtils;

import org.greenrobot.greendao.database.Database;

/**
 * Created by android
 * on 2020/8/11.
 */
public class DBMigrationHelper5 extends AbstractDBMigratorHelper {

    private static final String TAG = DBMigrationHelper5.class.getSimpleName();

    @Override
    public void onUpgrade(Database db) {
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "DATA_SOURCE_TYPE");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "RECORD_POINT_DATA_ID");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "RECORD_POINT_ID_TIME");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "RECORD_POINT_TIME_ZONE");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "RECORD_POINT_VERSION");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "RECORD_POINT_TYPE_DESCRIPTION");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "RECORD_POINT_SPORT_TYPE");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "RECORD_POINT_DATA_TYPE");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "RECORD_POINT_ENCRYPTION");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "RECORD_POINT_DATA_VALID1");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "RECORD_POINT_DATA_VALID2");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "RECORD_POINT_SPORT_DATA");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_ENCRYPTION");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_DATA_VALID1");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_DATA_VALID2");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_DATA_VALID3");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_DATA_VALID4");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_SPORT_START_TIME");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_SPORT_END_TIME");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_DURATION");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_DISTANCE");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_CAL");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_FAST_PACE");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_SLOWEST_PACE");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_FAST_SPEED");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_TOTAL_STEP");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_MAX_STEP_SPEED");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_AVG_HEART");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_MAX_HEART");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_MIN_HEART");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_CUMULATIVE_RISE");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_CUMULATIVE_DECLINE");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_AVG_HEIGHT");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_MAX_HEIGHT");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_MIN_HEIGHT");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_TRAINING_EFFECT");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_MAX_OXYGEN_INTAKE");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_ENERGY_CONSUMPTION");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_RECOVERY_TIME");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_HEART_LIMIT_TIME");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_HEART_ANAEROBIC");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_HEART_AEROBIC");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_HEART_FAT_BURNING");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "REPORT_HEART_WARM_UP");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "SERVICE_ID");
        DbMigrationUtils.addColumn(db, "SPORT_MODLE_INFO", "DEVICE_MAC");
        SysUtils.logContentE(TAG, "数据库升级完成，版本 4 升级至 5");
    }


}

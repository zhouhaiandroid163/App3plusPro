package com.zjw.apps3pluspro.sql.utils;

/**
 * Created by zjw on 2017/9/9.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zjw.apps3pluspro.sql.gen.DaoMaster;
import com.zjw.apps3pluspro.sql.migrate.AbstractDBMigratorHelper;
import com.zjw.apps3pluspro.utils.SysUtils;

import org.greenrobot.greendao.database.Database;


/**
 * Created by wbxu on 2017/6/19.
 * 自定义  MySQLiteOpenHelper集成  DaoMaster.OpenHelper 重写更新数据库的方法
 * 当app下的build.gradle  的schemaVersion数据库的版本号改变时，，创建数据库会调用onUpgrade更细数据库的方法
 */

public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {
    private static final String TAG = MySQLiteOpenHelper.class.getSimpleName();
    private static final String DB_UPGRADE = "com.zjw.apps3pluspro.sql.migrate.DBMigrationHelper";

    /**
     * @param context 上下文
     * @param name    原来定义的数据库的名字   新旧数据库一致
     * @param factory 可以null
     */


    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    /**
     * @param db
     * @param oldVersion
     * @param newVersion 更新数据库的时候自己调用
     */
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
//        Log.d("flag", "-----调用了");
        SysUtils.logContentI(TAG, "oldVersion = " + oldVersion + " newVersion = " + newVersion);

        //具体的数据转移在MigrationHelper2类中

//        /**
//         *  将db传入 将gen目录下的所有的Dao.类传入
//         */
        //要按照DaoMaster 的顺序来传入
//        MigrationHelper.migrate(db, HealthInfoDao.class, MovementInfoDao.class, SleepInfoDao.class, HeartInfoDao.class, SportModleInfoDao.class
//                , PhoneInfoDao.class, ContinuitySpo2InfoDao.class, ContinuityTempInfoDao.class, MeasureSpo2InfoDao.class, MeasureTempInfoDao.class);


        SQLiteDatabase sqLiteDatabase = (SQLiteDatabase) db.getRawDatabase();
        sqLiteDatabase.beginTransaction();
        for (int i = oldVersion; i < newVersion; i++) {
            int pos = i + 1;
            try {
                SysUtils.logContentI(TAG, "pos = " + pos);
                /* New instance of the class that migrates from i version to i++ version named DBMigratorHelper{version that the db has on this moment} */
                AbstractDBMigratorHelper migratorHelper = (AbstractDBMigratorHelper) Class.forName(DB_UPGRADE + pos).newInstance();
                /* Upgrade de db */
                migratorHelper.onUpgrade(db);
            } catch (Exception e) {
                String s = "升级数据库失败，oldVersion:" + oldVersion + " newVersion:" + newVersion + " currentUpdatingOldVersion:" + pos + " e = " + e;
                throw new IllegalStateException(s);
            }
        }
        //结束事务
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();

    }


}

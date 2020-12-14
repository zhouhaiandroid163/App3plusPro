package com.zjw.apps3pluspro.sql.migrate;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;

/**
 * Created by android
 * on 2020/5/29.
 */
public abstract class AbstractDBMigratorHelper {
    private static final String TAG = "DBMigrationHelper";

    public abstract void onUpgrade(Database db);

    /**
     * 获取一个 Table 所有的列的名字
     */
    public static String[] getTableAllColumn(SQLiteDatabase sqLiteDatabase, String tableName) {
        Cursor dbCursor = sqLiteDatabase.query(tableName, null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();
        dbCursor.close();
        return columnNames;
    }

    protected void logToLogcatAndFile(String s) {
    }

    public boolean isTableExists(String tableName, SQLiteDatabase sqLiteDatabase) {

//        SQLiteDatabase sqLiteDatabase = (SQLiteDatabase) db.getRawDatabase();

        boolean isExist = false;
        Cursor cursor = sqLiteDatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                isExist = true;
            }
            cursor.close();
        }
        return isExist;
    }
}

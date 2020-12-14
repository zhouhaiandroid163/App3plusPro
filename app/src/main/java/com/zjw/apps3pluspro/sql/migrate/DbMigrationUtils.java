package com.zjw.apps3pluspro.sql.migrate;

import org.greenrobot.greendao.database.Database;

public class DbMigrationUtils {

    static void addColumn(Database db, String tabName, String columuName) {
        db.execSQL("ALTER TABLE " + tabName + " ADD " + columuName + " TEXT default '' ");
    }
}

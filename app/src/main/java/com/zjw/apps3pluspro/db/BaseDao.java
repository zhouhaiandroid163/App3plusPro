package com.zjw.apps3pluspro.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BaseDao {
	public DbHelper dbHelper;
	public SQLiteDatabase db;
//	protected String uid;
	public BaseDao(Context context) {
//		dbHelper = new DbHelper(context);
		dbHelper = DbHelper.getInstance(context);
		db = dbHelper.getWritableDatabase();
//		uid = SharedPreferencesUitls.getStringData(context, "uid", "");
	}

	
	public boolean tabbleIsExist(String tableName){
		boolean result = false;
		if(tableName == null){
			return false;
		}
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = dbHelper.getReadableDatabase();
			//这里表名可以是Sqlite_master
			String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"+tableName.trim()+"' ";
			cursor = db.rawQuery(sql, null);
			if(cursor.moveToNext()){
				int count = cursor.getInt(0);
				if(count>0){
					result = true;
				}
			}
		} catch (Exception e) {
			
		}                
		return result;
	}
}

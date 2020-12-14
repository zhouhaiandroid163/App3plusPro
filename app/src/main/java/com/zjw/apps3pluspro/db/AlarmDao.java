package com.zjw.apps3pluspro.db;

import java.util.ArrayList;
import java.util.List;

import com.zjw.apps3pluspro.module.device.entity.AlarmClockItem;
import com.zjw.apps3pluspro.module.device.entity.AlarmClockModel;
import com.zjw.apps3pluspro.utils.log.MyLog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * 闹钟数据库
 */
public class AlarmDao extends BaseDao {

    private static final String TAG = "AlarmDao";

    public AlarmDao(Context context) {
        super(context);
    }


    public void insert(AlarmClockModel clockItems) {
        if (db.isOpen()) {
            if (tabbleIsExist(DbHelper.ALARM_TABLE)) {
                insertValues(clockItems);
            } else {
                dbHelper.createAlarmTable(db);
                insertValues(clockItems);
            }
        }
    }

    private void insertValues(AlarmClockModel clockItems) {
        ContentValues values = new ContentValues();
        values.put("id", clockItems.getId());
        values.put("time", clockItems.getTimeAlarm());
        values.put("repeat", clockItems.getRepeat());
        values.put("weekDay", clockItems.getWeekDay());
        db.insert(DbHelper.ALARM_TABLE, null, values);
        MyLog.i(TAG, "插入一条闹钟数据" + clockItems.toString());
    }


    public List<AlarmClockItem> query() {


        List<AlarmClockItem> alarmClockItems = new ArrayList<>();
        if (db.isOpen()) {
            if (tabbleIsExist(DbHelper.ALARM_TABLE)) {
                Cursor cursor = db.query(DbHelper.ALARM_TABLE, new String[]{
                        "id", "time", "repeat", "weekDay"}, null, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        AlarmClockItem alarmClockItem = new AlarmClockItem();
                        alarmClockItem.setId(cursor.getInt(0));
                        alarmClockItem.setTimeAlarm(cursor.getString(1));
                        alarmClockItem.setRepeat(cursor.getInt(2));
                        alarmClockItem.setWeekDay(cursor.getString(3));
                        alarmClockItems.add(alarmClockItem);
                        // MyLog.i(TAG,
                        // "查询到一条闹钟数据"+alarmClockItem.toString());
                    }
                    cursor.close();
                }
            }
        }
        return alarmClockItems;
    }

    public List<AlarmClockModel> query2() {

        List<AlarmClockModel> alarmClockItems = new ArrayList<AlarmClockModel>();
        if (db.isOpen()) {
            if (tabbleIsExist(DbHelper.ALARM_TABLE)) {
                Cursor cursor = db.query(DbHelper.ALARM_TABLE, new String[]{
                        "id", "time", "repeat", "weekDay"}, null, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        AlarmClockModel alarmClockItem = new AlarmClockModel();
                        alarmClockItem.setId(cursor.getInt(0));
                        alarmClockItem.setTimeAlarm(cursor.getString(1));
                        alarmClockItem.setRepeat(cursor.getInt(2));
                        alarmClockItem.setWeekDay(cursor.getString(3));
                        alarmClockItems.add(alarmClockItem);
                        // MyLog.i(TAG,
                        // "查询到一条闹钟数据"+alarmClockItem.toString());
                    }
                    cursor.close();
                }
            }
        }
        return alarmClockItems;
    }


    public void update(AlarmClockItem item) {
        if (db.isOpen()) {
            if (tabbleIsExist(DbHelper.ALARM_TABLE)) {
                ContentValues values = new ContentValues();
                values.put("repeat", item.getRepeat());
                String whereClause = "id = ?";
                db.update(DbHelper.ALARM_TABLE, values, whereClause,
                        new String[]{item.getId() + ""});
            }
        }
    }


    public void updateClock(AlarmClockModel mAlarmClockModel) {
        if (db.isOpen()) {
            if (tabbleIsExist(DbHelper.ALARM_TABLE)) {
                ContentValues values = new ContentValues();
                values.put("repeat", mAlarmClockModel.getRepeat());
                values.put("time", mAlarmClockModel.getTimeAlarm());
                values.put("weekDay", mAlarmClockModel.getWeekDay());
                String whereClause = "id = ?";
                db.update(DbHelper.ALARM_TABLE, values, whereClause, new String[]{mAlarmClockModel.getId() + ""});
            }
        }
    }


    public void delete(AlarmClockModel item) {
        if (db.isOpen()) {
            if (tabbleIsExist(DbHelper.ALARM_TABLE)) {
                String whereClause = "id = ?";
                db.delete(DbHelper.ALARM_TABLE, whereClause,
                        new String[]{item.getId() + ""});
            }
        }
    }
}

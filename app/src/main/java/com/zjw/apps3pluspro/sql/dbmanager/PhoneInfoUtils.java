package com.zjw.apps3pluspro.sql.dbmanager;

import android.content.Context;

import com.zjw.apps3pluspro.sql.entity.PhoneInfo;
import com.zjw.apps3pluspro.sql.gen.PhoneInfoDao;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;


public class PhoneInfoUtils {
    private final String TAG = PhoneInfoUtils.class.getSimpleName();
    private DaoManager mManager;

    public PhoneInfoUtils(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

//    /**
//     * 查询数据
//     *
//     * @param info
//     * @return boolean
//     */
//    public boolean MyQuestDataInNull(PhoneInfo info) {
//        boolean flag = false;
//
//        List<PhoneInfo> info_list = queryToDateAll(info.getUser_id(), info.getDate(), info.getData());
//
//        if (info_list.size() == 0) {
//            flag = true;
//        } else {
//            flag = false;
//        }
//        return flag;
//    }


//    /**
//     * 插入一条数据
//     *
//     * @param info
//     * @return
//     */
//    public boolean MyUpdateData(PhoneInfo info) {
//        boolean flag = false;
//
//        List<PhoneInfo> info_list = queryToDate(info.getUser_id(), info.getDate());
//
//        if (info_list.size() >= 1) {
//            MyLog.i(TAG, "有，更新数据");
//            info.set_id(info_list.get(0).get_id());
//            flag = updateData(info);
//        } else {
//
//            MyLog.i(TAG, "没有，插入一条");
//            flag = insertData(info);
//        }
//        return flag;
//    }

        /**
     * 插入一条数据
     *
     * @param info
     * @return
     */
    public boolean MyUpdateData(PhoneInfo info) {
        boolean flag = false;
        flag = insertData(info);
        return flag;
    }


//    /**
//     * 根据日期查询
//     *
//     * @return PhoneInfo
//     */
//    public PhoneInfo MyQueryToDate(String user_id) {
//
//        List<PhoneInfo> info_list = queryToDate(user_id);
//        if (info_list.size() >= 1) {
//            return info_list.get(0);
//        } else {
//            return null;
//        }
//    }

    /**
     * 根据日期查询
     *
     * @return PhoneInfo
     */
    public  List<PhoneInfo> MyQueryToDate(String user_id) {

        List<PhoneInfo> info_list = queryToDate(user_id);
        if (info_list.size() >= 1) {
            return info_list;
        } else {
            return null;
        }
    }

//    /**
//     * 根据用户ID 和 时间周期查询
//     *
//     * @param user_id
//     * @return List<PhoneInfo>
//     */
//    public List<PhoneInfo> MyQueryToPeriodTime(String user_id, String start_time, String end_time) {
//
//        QueryBuilder<PhoneInfo> queryBuilder = mManager.getDaoSession().queryBuilder(PhoneInfo.class);
//        return queryBuilder
//                .where(PhoneInfoDao.Properties.User_id.eq(user_id))
//                //大于等于
//                .where(PhoneInfoDao.Properties.Date.ge(start_time))
//                //小于等于
//                .where(PhoneInfoDao.Properties.Date.le(end_time))
//                //升序
//                .orderAsc(PhoneInfoDao.Properties.Date)
//                .list();
//
//
//    }


//    /**
//     * 根据用户ID 和 日期 查询周数据
//     *
//     * @param user_id
//     * @return List<PhoneInfo>
//     */
//    public List<PhoneInfo> MyQueryToWeek(String user_id, String date, int count) {
//
//        String last_week_date = MyTime.GetLastWeektDate(date, count);
//        MyLog.i(TAG, "week1 = " + date);
//        MyLog.i(TAG, "week2 = " + last_week_date);
//
//
//        QueryBuilder<PhoneInfo> queryBuilder = mManager.getDaoSession().queryBuilder(PhoneInfo.class);
//
//
//        if (count < 0) {
//            return queryBuilder
//                    .where(PhoneInfoDao.Properties.User_id.eq(user_id))
//                    //大于等于
//                    .where(PhoneInfoDao.Properties.Date.ge(last_week_date))
//                    //小于等于
//                    .where(PhoneInfoDao.Properties.Date.le(date))
//                    //升序
//                    .orderAsc(PhoneInfoDao.Properties.Date)
//                    .list();
//        } else {
//            return queryBuilder
//                    .where(PhoneInfoDao.Properties.User_id.eq(user_id))
//                    //小于等于
//                    .where(PhoneInfoDao.Properties.Date.le(last_week_date))
//                    //大于等于
//                    .where(PhoneInfoDao.Properties.Date.ge(date))
//                    //升序
//                    .orderAsc(PhoneInfoDao.Properties.Date)
//                    .list();
//        }
//
//    }

//    /**
//     * 根据用户ID 和 日期 查询周数据
//     *
//     * @param user_id
//     * @return List<PhoneInfo>
//     */
//    public List<PhoneInfo> MyQueryToMonth(String user_id, String date) {
//
//
//        QueryBuilder<PhoneInfo> queryBuilder = mManager.getDaoSession().queryBuilder(PhoneInfo.class);
//        return queryBuilder
//                .where(PhoneInfoDao.Properties.User_id.eq(user_id))
//                //大于等于
//                .where(PhoneInfoDao.Properties.Date.ge(date + "-01"))
//                //小于等于
//                .where(PhoneInfoDao.Properties.Date.le(date + "-31"))
//                //升序
//                .orderAsc(PhoneInfoDao.Properties.Date)
//                .list();
//
//    }

//    //==================修改后台状态==============
//
//
//    /**
//     * 根据用户ID来查询 服务状态为0的前多少条数据
//     *
//     * @param user_id
//     * @return List<SleepInfo>
//     */
//    public List<PhoneInfo> MyQueryToSyncToNumber(String user_id, int count) {
//
//        QueryBuilder<PhoneInfo> queryBuilder = mManager.getDaoSession().queryBuilder(PhoneInfo.class);
//
//
//        List<PhoneInfo> info_list = queryBuilder.where(PhoneInfoDao.Properties.User_id.eq(user_id), PhoneInfoDao.Properties.Sync_state.eq("0")).list();
//
//        List<PhoneInfo> result_list = new ArrayList<>();
//
//        if (info_list.size() > count) {
//
//            for (int i = 0; i < count; i++) {
//                result_list.add(info_list.get(i));
//            }
//        } else {
//            result_list = info_list;
//        }
//
//        return result_list;
//
//    }

//    /**
//     * 根据用户ID来查询 服务状态为0的所有数据
//     *
//     * @param user_id
//     * @return List<PhoneInfo>
//     */
//    public List<PhoneInfo> MyQueryToSyncAll(String user_id) {
//
//        QueryBuilder<PhoneInfo> queryBuilder = mManager.getDaoSession().queryBuilder(PhoneInfo.class);
//
//        List<PhoneInfo> info_list = queryBuilder.where(PhoneInfoDao.Properties.User_id.eq(user_id), PhoneInfoDao.Properties.Sync_state.eq("0")).list();
//
//
//        return info_list;
//
//    }

//    /**
//     * 根据用户ID 和 时间点
//     *
//     * @param user_id
//     * @return
//     */
//    public boolean MyupdateToSyncDataId(String user_id, String date) {
//        boolean flag = false;
//
//        PhoneInfo info = MyQueryToDate(user_id, date);
//
//        if (info != null) {
//            MyLog.i(TAG, "有，更新数据");
//            info.setSync_state("1");
//            flag = updateData(info);
//        }
//        return flag;
//    }


    //==================基础方法==============


    /**
     * 插入数据
     *
     * @param info
     * @return
     */
    public boolean insertData(PhoneInfo info) {
        info.setWarehousing_time(String.valueOf(System.currentTimeMillis()));
        MyLog.i(TAG, "插入数据 = info = " + info.toString());
        boolean flag = mManager.getDaoSession().getPhoneInfoDao().insert(info) == -1 ? false : true;
        return flag;
    }

    /**
     * 修改一条数据,根据系统ID来修改
     *
     * @param info
     * @return
     */
    public boolean updateData(PhoneInfo info) {
        info.setWarehousing_time(String.valueOf(System.currentTimeMillis()));
        boolean flag = false;
        try {
            mManager.getDaoSession().update(info);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 根据用户ID
     *
     * @param user_id
     * @return List<PhoneInfo>
     */
    public List<PhoneInfo> queryToDate(String user_id) {

        QueryBuilder<PhoneInfo> queryBuilder = mManager.getDaoSession().queryBuilder(PhoneInfo.class);
        return queryBuilder.where(PhoneInfoDao.Properties.User_id.eq(user_id)).list();

    }

//    /**
//     * 根据用户ID 和 日期 ，卡路里 ， 距离，步数
//     *
//     * @param user_id
//     * @return List<PhoneInfo>
//     */
//    public List<PhoneInfo> queryToDateAll(String user_id, String date, String data) {
//
//        QueryBuilder<PhoneInfo> queryBuilder = mManager.getDaoSession().queryBuilder(PhoneInfo.class);
//        return queryBuilder.where(PhoneInfoDao.Properties.User_id.eq(user_id)
//                , PhoneInfoDao.Properties.Date.eq(date)
//                , PhoneInfoDao.Properties.Data.eq(data)).list();
//
//    }

//    /**
//     * 根据用户ID来查询 服务状态为零的所有数据
//     *
//     * @param user_id
//     * @return List<PhoneInfo>
//     */
//    public List<PhoneInfo> queryToSync(String user_id) {
//
//        QueryBuilder<PhoneInfo> queryBuilder = mManager.getDaoSession().queryBuilder(PhoneInfo.class);
//
//        return queryBuilder.where(PhoneInfoDao.Properties.User_id.eq(user_id), PhoneInfoDao.Properties.Sync_state.eq("0")).list();
//
//    }


    //==================暂时不用==============

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<PhoneInfo> queryAllData() {
        return mManager.getDaoSession().loadAll(PhoneInfo.class);
    }

    /**
     * 删除所有记录
     *
     * @return
     */
    public boolean deleteAllData() {
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().deleteAll(PhoneInfo.class);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 插入数据
     *
     * @param info
     * @return
     */
    public void deleteData(PhoneInfo info) {

//        MyLog.i(TAG, "插入数据 = info = " + info.toString());
//        boolean flag = mManager.getDaoSession().getPhoneInfoDao().insert(info) == -1 ? false : true;
//        return flag;

        mManager.getDaoSession().getPhoneInfoDao().delete(info);

    }

    /**
     * 根据用户ID
     *
     * @param user_id
     * @return List<PhoneInfo>
     */
    public List<PhoneInfo> queryInfoList(String user_id) {

        QueryBuilder<PhoneInfo> queryBuilder = mManager.getDaoSession().queryBuilder(PhoneInfo.class);
        return queryBuilder.where(PhoneInfoDao.Properties.User_id.eq(user_id)).list();

    }

    /**
     * 插入多条数据，在子线程操作
     *
     * @param infoList
     * @return
     */
    public boolean insertInfoList(final List<PhoneInfo> infoList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (PhoneInfo info : infoList) {
                        MyLog.i(TAG, "插入多条数据 = " + info.toString());
                        MyUpdateData(info);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


}
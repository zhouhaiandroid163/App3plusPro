package com.zjw.apps3pluspro.sql.dbmanager;

import android.content.Context;

import com.zjw.apps3pluspro.sql.entity.SleepInfo;
import com.zjw.apps3pluspro.sql.gen.SleepInfoDao;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.utils.MyTime;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjw on 2017/12/6.
 */

public class SleepInfoUtils {
    private final String TAG = SleepInfoUtils.class.getSimpleName();
    private DaoManager mManager;

    public SleepInfoUtils(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 查询数据
     *
     * @param info
     * @return boolean
     */
    public boolean MyQuestDataInNull(SleepInfo info) {
        boolean flag = false;

        List<SleepInfo> info_list = queryToDateAll(info.getUser_id(), info.getDate(), info.getData());

        if (info_list.size() == 0) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

//    /**
//     * 插入一条数据
//     *
//     * @param info
//     * @return
//     */
//    public boolean MyUpdateDataTest(SleepInfo info) {
//        return insertData(info);
//    }

    /**
     * 插入一条数据
     *
     * @param info
     * @return
     */
    public boolean MyUpdateData(SleepInfo info) {
        boolean flag = false;

        List<SleepInfo> info_list = queryToDate(info.getUser_id(), info.getDate());

        if (info_list.size() >= 1) {
            MyLog.i(TAG, "有，更新数据");
            info.set_id(info_list.get(0).get_id());
            flag = updateData(info);
        } else {
            MyLog.i(TAG, "没有，插入一条");
            flag = insertData(info);
        }
        return flag;
    }


    /**
     * 根据日期查询
     *
     * @return SleepInfo
     */
    public SleepInfo MyQueryToDate(String user_id, String date) {

        List<SleepInfo> info_list = queryToDate(user_id, date);
        if (info_list.size() >= 1) {
            return info_list.get(0);
        } else {
            return null;
        }
    }

    /**
     * 根据用户ID 和 时间周期查询
     *
     * @param user_id
     * @return List<SleepInfo>
     */
    public List<SleepInfo> MyQueryToPeriodTime(String user_id, String start_time, String end_time) {

        QueryBuilder<SleepInfo> queryBuilder = mManager.getDaoSession().queryBuilder(SleepInfo.class);

        return queryBuilder
                .where(SleepInfoDao.Properties.User_id.eq(user_id))
                //大于等于
                .where(SleepInfoDao.Properties.Date.ge(start_time))
                //小于等于
                .where(SleepInfoDao.Properties.Date.le(end_time))
                //升序
                .orderAsc(SleepInfoDao.Properties.Date)
                .list();

    }


    /**
     * 根据用户ID 和 日期 查询周数据
     *
     * @param user_id
     * @return List<SleepInfo>
     */
    public List<SleepInfo> MyQueryToWeek(String user_id, String date, int count) {

        String last_week_date = MyTime.GetLastWeektDate(date, count);
        MyLog.i(TAG, "week1 = " + date);
        MyLog.i(TAG, "week2 = " + last_week_date);


        QueryBuilder<SleepInfo> queryBuilder = mManager.getDaoSession().queryBuilder(SleepInfo.class);


        if (count < 0) {
            return queryBuilder
                    .where(SleepInfoDao.Properties.User_id.eq(user_id))
                    //大于等于
                    .where(SleepInfoDao.Properties.Date.ge(last_week_date))
                    //小于等于
                    .where(SleepInfoDao.Properties.Date.le(date))
                    //升序
                    .orderAsc(SleepInfoDao.Properties.Date)
                    .list();
        } else {
            return queryBuilder
                    .where(SleepInfoDao.Properties.User_id.eq(user_id))
                    //小于等于
                    .where(SleepInfoDao.Properties.Date.le(last_week_date))
                    //大于等于
                    .where(SleepInfoDao.Properties.Date.ge(date))
                    //升序
                    .orderAsc(SleepInfoDao.Properties.Date)
                    .list();
        }

    }

    /**
     * 根据用户ID 和 日期 查询周数据
     *
     * @param user_id
     * @return List<SleepInfo>
     */
    public List<SleepInfo> MyQueryToMonth(String user_id, String date) {


        QueryBuilder<SleepInfo> queryBuilder = mManager.getDaoSession().queryBuilder(SleepInfo.class);
        return queryBuilder
                .where(SleepInfoDao.Properties.User_id.eq(user_id))
                //大于等于
                .where(SleepInfoDao.Properties.Date.ge(date + "-01"))
                //小于等于
                .where(SleepInfoDao.Properties.Date.le(date + "-31"))
                //升序
                .orderAsc(SleepInfoDao.Properties.Date)
                .list();

    }

    //==================修改后台状态==============


    /**
     * 根据用户ID来查询 服务状态为0的前多少条数据
     *
     * @param user_id
     * @return List<SleepInfo>
     */
    public List<SleepInfo> MyQueryToSyncToNumber(String user_id, int count) {

        QueryBuilder<SleepInfo> queryBuilder = mManager.getDaoSession().queryBuilder(SleepInfo.class);


        List<SleepInfo> info_list = queryBuilder.where(SleepInfoDao.Properties.User_id.eq(user_id), SleepInfoDao.Properties.Sync_state.eq("0")).list();

        List<SleepInfo> result_list = new ArrayList<>();

        if (info_list.size() > count) {

            for (int i = 0; i < count; i++) {
                result_list.add(info_list.get(i));
            }
        } else {
            result_list = info_list;
        }

        return result_list;

    }

    /**
     * 根据用户ID来查询 服务状态为0的所有数据
     *
     * @param user_id
     * @return List<SleepInfo>
     */
    public List<SleepInfo> MyQueryToSyncAll(String user_id) {

        QueryBuilder<SleepInfo> queryBuilder = mManager.getDaoSession().queryBuilder(SleepInfo.class);

        List<SleepInfo> info_list = queryBuilder.where(SleepInfoDao.Properties.User_id.eq(user_id), SleepInfoDao.Properties.Sync_state.eq("0"))
                .orderDesc(SleepInfoDao.Properties.Date)
                .orderDesc(SleepInfoDao.Properties.Warehousing_time)
                .limit(10)
                .list();
        return info_list;
    }

    public void deleteDataSleepInfo(SleepInfo sleepInfo) {
        try {
            mManager.getDaoSession().getSleepInfoDao().delete(sleepInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据用户ID 和 时间点
     *
     * @param user_id
     * @return
     */
    public boolean MyupdateToSyncDataId(String user_id, String date) {
        List<SleepInfo> info_list = queryToDate(user_id, date);
        return MyUpdateToSync(info_list);
    }

    /**
     * 修改列表的网络状态为1
     *
     * @param infoList
     * @return
     */
    public boolean MyUpdateToSync(final List<SleepInfo> infoList) {
        boolean flag = true;
        if (infoList != null && infoList.size() > 0) {
            for (SleepInfo info : infoList) {
                info.setSync_state("1");
            }
            try {
                mManager.getDaoSession().getSleepInfoDao().updateInTx(infoList);
            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
            }
        }
        return flag;
    }

    /**
     * 根据用户ID 修改所有服务器状态为1
     *
     * @param user_id
     * @return
     */
    public boolean MyChangeUserInfoToSync(String user_id) {
        boolean flag = false;
        List<SleepInfo> info_list = queryToSync(user_id);
        if (info_list.size() >= 1) {
            MyLog.i(TAG, "有数据，需要处理");
            MyUpdateToSync(info_list);
        } else {
            MyLog.i(TAG, "没有数据，不处理");
            flag = false;
        }
        return flag;
    }


    //==================基础方法==============


    /**
     * 插入数据
     *
     * @param info
     * @return
     */
    public boolean insertData(SleepInfo info) {
        info.setWarehousing_time(String.valueOf(System.currentTimeMillis()));
        MyLog.i(TAG, "插入数据 = info = " + info.toString());
        boolean flag = mManager.getDaoSession().getSleepInfoDao().insert(info) == -1 ? false : true;
        return flag;
    }

    /**
     * 修改一条数据,根据系统ID来修改
     *
     * @param info
     * @return
     */
    public boolean updateData(SleepInfo info) {
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
     * 根据用户ID 和 日期
     *
     * @param user_id
     * @return List<SleepInfo>
     */
    public List<SleepInfo> queryToDate(String user_id, String date) {

        QueryBuilder<SleepInfo> queryBuilder = mManager.getDaoSession().queryBuilder(SleepInfo.class);
        return queryBuilder.where(SleepInfoDao.Properties.User_id.eq(user_id), SleepInfoDao.Properties.Date.eq(date)).list();

    }

    /**
     * 根据用户ID 和 日期 ，卡路里 ， 距离，步数
     *
     * @param user_id
     * @return List<SleepInfo>
     */
    public List<SleepInfo> queryToDateAll(String user_id, String date, String data) {

        QueryBuilder<SleepInfo> queryBuilder = mManager.getDaoSession().queryBuilder(SleepInfo.class);
        return queryBuilder.where(SleepInfoDao.Properties.User_id.eq(user_id)
                , SleepInfoDao.Properties.Date.eq(date)
                , SleepInfoDao.Properties.Data.eq(data)).list();

    }

    /**
     * 根据用户ID来查询 服务状态为零的所有数据
     *
     * @param user_id
     * @return List<SleepInfo>
     */
    public List<SleepInfo> queryToSync(String user_id) {

        QueryBuilder<SleepInfo> queryBuilder = mManager.getDaoSession().queryBuilder(SleepInfo.class);

        return queryBuilder.where(SleepInfoDao.Properties.User_id.eq(user_id), SleepInfoDao.Properties.Sync_state.eq("0")).list();

    }


    //==================暂时不用==============

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<SleepInfo> queryAllData() {
        return mManager.getDaoSession().loadAll(SleepInfo.class);
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
            mManager.getDaoSession().deleteAll(SleepInfo.class);
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
     * @return List<SleepInfo>
     */
    public List<SleepInfo> queryInfoList(String user_id) {

        QueryBuilder<SleepInfo> queryBuilder = mManager.getDaoSession().queryBuilder(SleepInfo.class);
        return queryBuilder.where(SleepInfoDao.Properties.User_id.eq(user_id)).list();

    }

    /**
     * 插入多条数据，在子线程操作
     *
     * @param infoList
     * @return
     */
    public boolean insertInfoList(final List<SleepInfo> infoList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (SleepInfo info : infoList) {
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
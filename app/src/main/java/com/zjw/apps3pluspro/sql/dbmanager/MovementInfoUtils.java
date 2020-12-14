package com.zjw.apps3pluspro.sql.dbmanager;

import android.content.Context;


import com.zjw.apps3pluspro.sql.entity.MovementInfo;
import com.zjw.apps3pluspro.sql.gen.MovementInfoDao;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjw on 2017/12/6.
 */

public class MovementInfoUtils {
    private final String TAG = MovementInfoUtils.class.getSimpleName();
    private DaoManager mManager;

    public MovementInfoUtils(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }


    /**
     * 查询数据
     *
     * @param info
     * @return boolean
     */
    public boolean MyQuestDataInNull(MovementInfo info) {
        boolean flag = false;

        List<MovementInfo> info_list = queryToDateAll(info.getUser_id(), info.getDate(), info.getTotal_step(), info.getCalorie(), info.getDisance());

        if (info_list.size() == 0) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }


    /**
     * 插入一条数据
     *
     * @param info
     * @return boolean
     */
    public boolean MyUpdateData(MovementInfo info) {
        boolean flag = false;

        List<MovementInfo> info_list = queryToDate(info.getUser_id(), info.getDate());

        if (info_list.size() >= 1) {

            try {
                if (info_list.size() > 1) {
                    for (int i = 1; i < info_list.size(); i++) {
                        MyLog.i(TAG, "有，删除数据 i = " + i + " info = " + info_list.get(i).toString());
                        deleteDataMovementInfo(info_list.get(i));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            MyLog.i(TAG, "有，更新数据");
            info.set_id(info_list.get(0).get_id());
            flag = updateData(info);

        } else {
            MyLog.i(TAG, "没有，插入一条");
            flag = insertData(info);
        }
        return flag;
    }

//    /**
//     * 插入一条数据
//     *
//     * @param info
//     * @return boolean
//     */
//    public boolean MyUpdateDataTest(MovementInfo info) {
//        boolean flag = false;
//        MyLog.i(TAG, "直接插入一条");
//        flag = insertData(info);
//        return flag;
//    }

    /**
     * 根据日期查询
     *
     * @return MovementInfo
     */
    public MovementInfo MyQueryToDate(String user_id, String date) {

        List<MovementInfo> info_list = queryToDate(user_id, date);
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
     * @return List<MovementInfo>
     */
    public List<MovementInfo> MyQueryToPeriodTime(String user_id, String start_time, String end_time) {


        QueryBuilder<MovementInfo> queryBuilder = mManager.getDaoSession().queryBuilder(MovementInfo.class);

        return queryBuilder
                .where(MovementInfoDao.Properties.User_id.eq(user_id))
                //大于等于
                .where(MovementInfoDao.Properties.Date.ge(start_time))
                //小于等于
                .where(MovementInfoDao.Properties.Date.le(end_time))
                //升序
                .orderAsc(MovementInfoDao.Properties.Date)
                .list();


    }


    /**
     * 根据用户ID 和 日期 查询周数据
     *
     * @param user_id
     * @return List<MovementInfo>
     */
    public List<MovementInfo> MyQueryToMonth(String user_id, String date) {


        QueryBuilder<MovementInfo> queryBuilder = mManager.getDaoSession().queryBuilder(MovementInfo.class);
        return queryBuilder
                .where(MovementInfoDao.Properties.User_id.eq(user_id))
                //大于等于
                .where(MovementInfoDao.Properties.Date.ge(date + "-01"))
                //小于等于
                .where(MovementInfoDao.Properties.Date.le(date + "-31"))
                //升序
                .orderAsc(MovementInfoDao.Properties.Date)
                .list();

    }


    //==================修改后台状态==============


    /**
     * 根据用户ID来查询 服务状态为0的前多少条数据
     *
     * @param user_id
     * @return List<MovementInfo>
     */
    public List<MovementInfo> MyQueryToSyncToNumber(String user_id, int count) {

        QueryBuilder<MovementInfo> queryBuilder = mManager.getDaoSession().queryBuilder(MovementInfo.class);

        List<MovementInfo> info_list = queryBuilder.where(MovementInfoDao.Properties.User_id.eq(user_id), MovementInfoDao.Properties.Sync_state.eq("0")).list();

        List<MovementInfo> result_list = new ArrayList<>();

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
     * @return List<MovementInfo>
     */
    public List<MovementInfo> MyQueryToSyncAll(String user_id) {

        QueryBuilder<MovementInfo> queryBuilder = mManager.getDaoSession().queryBuilder(MovementInfo.class);

        List<MovementInfo> info_list = queryBuilder.where(MovementInfoDao.Properties.User_id.eq(user_id), MovementInfoDao.Properties.Sync_state.eq("0"))
                .orderDesc(MovementInfoDao.Properties.Date)
                .orderDesc(MovementInfoDao.Properties.Warehousing_time)
                .limit(10)
                .list();
        return info_list;

    }

    /**
     * 根据用户ID 和 时间点
     *
     * @param user_id
     * @return
     */
    public boolean MyupdateToSyncDataId(String user_id, String date) {
        List<MovementInfo> info_list = queryToDate(user_id, date);
        return MyUpdateToSync(info_list);
    }


    /**
     * 修改列表的网络状态为1
     *
     * @param infoList
     * @return
     */
    public boolean MyUpdateToSync(final List<MovementInfo> infoList) {
        boolean flag = true;
        if (infoList != null && infoList.size() > 0) {
            for (MovementInfo info : infoList) {
                info.setSync_state("1");
            }
            try {
                mManager.getDaoSession().getMovementInfoDao().updateInTx(infoList);
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

        List<MovementInfo> info_list = queryToSync(user_id);
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
    public boolean insertData(MovementInfo info) {
        info.setWarehousing_time(String.valueOf(System.currentTimeMillis()));
        MyLog.i(TAG, "插入数据 = info = " + info.toString());
        boolean flag = mManager.getDaoSession().getMovementInfoDao().insert(info) == -1 ? false : true;
        return flag;
    }

    /**
     * 更新一条数据（系统ID）
     *
     * @param info
     * @return
     */
    public boolean updateData(MovementInfo info) {
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
     * @return List<MovementInfo>
     */
    public List<MovementInfo> queryToDate(String user_id, String date) {

        QueryBuilder<MovementInfo> queryBuilder = mManager.getDaoSession().queryBuilder(MovementInfo.class);
        return queryBuilder
                .where(MovementInfoDao.Properties.User_id.eq(user_id), MovementInfoDao.Properties.Date.eq(date))
                .orderDesc(MovementInfoDao.Properties.Warehousing_time)
                .list();

    }

    /**
     * 根据用户ID 和 日期 ，卡路里 ， 距离，步数
     *
     * @param user_id
     * @return List<MovementInfo>
     */
    public List<MovementInfo> queryToDateAll(String user_id, String date, String step, String calories, String distance) {

        QueryBuilder<MovementInfo> queryBuilder = mManager.getDaoSession().queryBuilder(MovementInfo.class);
        return queryBuilder.where(MovementInfoDao.Properties.User_id.eq(user_id)
                , MovementInfoDao.Properties.Date.eq(date)
                , MovementInfoDao.Properties.Total_step.eq(step)
                , MovementInfoDao.Properties.Calorie.eq(calories)
                , MovementInfoDao.Properties.Disance.eq(distance)).list();

    }

    /**
     * 根据用户ID来查询 服务状态为零的所有数据
     *
     * @param user_id
     * @return List<MovementInfo>
     */
    public List<MovementInfo> queryToSync(String user_id) {

        QueryBuilder<MovementInfo> queryBuilder = mManager.getDaoSession().queryBuilder(MovementInfo.class);

        return queryBuilder.where(MovementInfoDao.Properties.User_id.eq(user_id), MovementInfoDao.Properties.Sync_state.eq("0")).list();

    }

    /**
     * 删除所有记录
     *
     * @return
     */
    public boolean deleteDataMovementInfo(MovementInfo info) {
        boolean flag = false;
        try {
            mManager.getDaoSession().getMovementInfoDao().delete(info);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    //==================暂时不用==============

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<MovementInfo> queryAllData() {
        return mManager.getDaoSession().loadAll(MovementInfo.class);
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
            mManager.getDaoSession().deleteAll(MovementInfo.class);
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
     * @return List<MovementInfo>
     */
    public List<MovementInfo> queryInfoList(String user_id) {

        QueryBuilder<MovementInfo> queryBuilder = mManager.getDaoSession().queryBuilder(MovementInfo.class);
        return queryBuilder.where(MovementInfoDao.Properties.User_id.eq(user_id)).list();

    }


    /**
     * 插入多条数据，在子线程操作
     *
     * @param infoList
     * @return
     */
    public boolean insertInfoList(final List<MovementInfo> infoList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (MovementInfo info : infoList) {
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
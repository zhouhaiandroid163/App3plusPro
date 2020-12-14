package com.zjw.apps3pluspro.sql.dbmanager;

import android.content.Context;

import com.zjw.apps3pluspro.sql.entity.ContinuityTempInfo;
import com.zjw.apps3pluspro.sql.gen.ContinuityTempInfoDao;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjw on 2017/12/6.
 */

public class ContinuityTempInfoUtils {
    private final String TAG = ContinuityTempInfoUtils.class.getSimpleName();
    private DaoManager mManager;

    public ContinuityTempInfoUtils(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }


    /**
     * 查询数据
     *
     * @param info
     * @return boolean
     */
    public boolean MyQuestDataInNull(ContinuityTempInfo info) {
        boolean flag = false;

        List<ContinuityTempInfo> info_list = queryToDateAll(info.getUser_id(), info.getDate(), info.getData());

        if (info_list.size() == 0) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }


    /**
     * 插入数据，条件是，日期和ID
     *
     * @param info
     * @return
     */
    public boolean MyUpdateData(ContinuityTempInfo info) {
        boolean flag = false;

        List<ContinuityTempInfo> info_list = queryToDate(info.getUser_id(), info.getDate());

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
     * @return ContinuityTempInfo
     */
    public ContinuityTempInfo MyQueryToDate(String user_id, String date) {

        List<ContinuityTempInfo> info_list = queryToDate(user_id, date);
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
    public List<ContinuityTempInfo> MyQueryToPeriodTime(String user_id, String start_time, String end_time) {


        QueryBuilder<ContinuityTempInfo> queryBuilder = mManager.getDaoSession().queryBuilder(ContinuityTempInfo.class);

        return queryBuilder
                //等于用户ID
                .where(ContinuityTempInfoDao.Properties.User_id.eq(user_id))
                //大于等于
                .where(ContinuityTempInfoDao.Properties.Date.ge(start_time))
                //小于等于
                .where(ContinuityTempInfoDao.Properties.Date.le(end_time))
                //升序
                .orderAsc(ContinuityTempInfoDao.Properties.Date)
                .list();


    }


    /**
     * 根据用户ID 和 日期 查询周数据
     *
     * @param user_id
     * @return List<ContinuityTempInfo>
     */
    public List<ContinuityTempInfo> MyQueryToWeek(String user_id, String date) {

        String last_week_date = MyTime.GetLastWeektDate(date);

        QueryBuilder<ContinuityTempInfo> queryBuilder = mManager.getDaoSession().queryBuilder(ContinuityTempInfo.class);
        return queryBuilder
                .where(ContinuityTempInfoDao.Properties.User_id.eq(user_id))
                .where(ContinuityTempInfoDao.Properties.Date.ge(last_week_date))
                .where(ContinuityTempInfoDao.Properties.Date.le(date))
                .orderDesc(ContinuityTempInfoDao.Properties.Date)
                .list();

    }

    /**
     * 根据用户ID 和 日期 查询周数据
     *
     * @param user_id
     * @return List<ContinuityTempInfo>
     */
    public List<ContinuityTempInfo> MyQueryToMonth(String user_id, String date) {

        String now_month = MyTime.GetNowMonthtDate(date);
        String next_month = MyTime.GetNextMonthtDate(date);


        QueryBuilder<ContinuityTempInfo> queryBuilder = mManager.getDaoSession().queryBuilder(ContinuityTempInfo.class);
        return queryBuilder
                .where(ContinuityTempInfoDao.Properties.User_id.eq(user_id))
                .where(ContinuityTempInfoDao.Properties.Date.ge(now_month))
                .where(ContinuityTempInfoDao.Properties.Date.le(next_month))
                .orderDesc(ContinuityTempInfoDao.Properties.Date)
                .list();

    }


    //==================修改后台状态==============

    /**
     * 根据用户ID来查询 服务状态为0的前多少条数据
     *
     * @param user_id
     * @return List<ContinuityTempInfo>
     */
    public List<ContinuityTempInfo> MyQueryToSyncToNumber(String user_id, int count) {

        QueryBuilder<ContinuityTempInfo> queryBuilder = mManager.getDaoSession().queryBuilder(ContinuityTempInfo.class);


        List<ContinuityTempInfo> info_list = queryBuilder.where(ContinuityTempInfoDao.Properties.User_id.eq(user_id), ContinuityTempInfoDao.Properties.Sync_state.eq("0")).list();

        List<ContinuityTempInfo> result_list = new ArrayList<>();

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
     * @return List<ContinuityTempInfo>
     */
    public List<ContinuityTempInfo> MyQueryToSyncAll(String user_id) {
        QueryBuilder<ContinuityTempInfo> queryBuilder = mManager.getDaoSession().queryBuilder(ContinuityTempInfo.class);
        List<ContinuityTempInfo> info_list = queryBuilder.where(ContinuityTempInfoDao.Properties.User_id.eq(user_id), ContinuityTempInfoDao.Properties.Sync_state.eq("0"))
                .orderDesc(ContinuityTempInfoDao.Properties.Date)
                .orderDesc(ContinuityTempInfoDao.Properties.Warehousing_time)
                .limit(10)
                .list();
        return info_list;
    }

    public void deleteDataContinuityTempInfo(ContinuityTempInfo continuityTempInfo) {
        try {
            mManager.getDaoSession().getContinuityTempInfoDao().delete(continuityTempInfo);
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
        List<ContinuityTempInfo> info_list = queryToDate(user_id, date);
        return MyUpdateToSync(info_list);
    }

    /**
     * 修改列表的网络状态为1
     *
     * @param infoList
     * @return
     */
    public boolean MyUpdateToSync(final List<ContinuityTempInfo> infoList) {
        boolean flag = true;
        if (infoList != null && infoList.size() > 0) {
            try {
                for (ContinuityTempInfo info : infoList) {
                    info.setSync_state("1");
                }
                mManager.getDaoSession().getContinuityTempInfoDao().updateInTx(infoList);
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

        List<ContinuityTempInfo> info_list = queryToSync(user_id);

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
    public boolean insertData(ContinuityTempInfo info) {
        info.setWarehousing_time(String.valueOf(System.currentTimeMillis()));
        MyLog.i(TAG, "插入数据 = info = " + info.toString());
        boolean flag = mManager.getDaoSession().getContinuityTempInfoDao().insert(info) == -1 ? false : true;
        return flag;
    }

    /**
     * 更新一条数据（系统ID）
     *
     * @param info
     * @return
     */
    public boolean updateData(ContinuityTempInfo info) {
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
     * @return List<ContinuityTempInfo>
     */
    public List<ContinuityTempInfo> queryToDate(String user_id, String date) {

        QueryBuilder<ContinuityTempInfo> queryBuilder = mManager.getDaoSession().queryBuilder(ContinuityTempInfo.class);
        return queryBuilder.where(ContinuityTempInfoDao.Properties.User_id.eq(user_id), ContinuityTempInfoDao.Properties.Date.eq(date)).list();

    }


    /**
     * 根据用户ID 和 日期
     *
     * @param user_id
     * @return List<ContinuityTempInfo>
     */
    public List<ContinuityTempInfo> queryToDate(String user_id, String date, String data_type) {

        QueryBuilder<ContinuityTempInfo> queryBuilder = mManager.getDaoSession().queryBuilder(ContinuityTempInfo.class);
        return queryBuilder.where(ContinuityTempInfoDao.Properties.User_id.eq(user_id), ContinuityTempInfoDao.Properties.Date.eq(date)).list();

    }


    /**
     * 根据用户ID 和 日期
     *
     * @param user_id
     * @return List<ContinuityTempInfo>
     */
    public List<ContinuityTempInfo> queryToDateAll(String user_id, String date, String data) {

        QueryBuilder<ContinuityTempInfo> queryBuilder = mManager.getDaoSession().queryBuilder(ContinuityTempInfo.class);
        return queryBuilder.where(ContinuityTempInfoDao.Properties.User_id.eq(user_id)
                , ContinuityTempInfoDao.Properties.Date.eq(date)
                , ContinuityTempInfoDao.Properties.Data.eq(data)
        ).list();

    }

    /**
     * 根据用户ID来查询 服务状态为零的所有数据
     *
     * @param user_id
     * @return List<ContinuityTempInfo>
     */
    public List<ContinuityTempInfo> queryToSync(String user_id) {

        QueryBuilder<ContinuityTempInfo> queryBuilder = mManager.getDaoSession().queryBuilder(ContinuityTempInfo.class);

        return queryBuilder.where(ContinuityTempInfoDao.Properties.User_id.eq(user_id), ContinuityTempInfoDao.Properties.Sync_state.eq("0")).list();

    }

    //==================暂时不用==============

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<ContinuityTempInfo> queryAllData() {
        return mManager.getDaoSession().loadAll(ContinuityTempInfo.class);
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
            mManager.getDaoSession().deleteAll(ContinuityTempInfo.class);
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
     * @return List<ContinuityTempInfo>
     */
    public List<ContinuityTempInfo> queryInfoList(String user_id) {

        QueryBuilder<ContinuityTempInfo> queryBuilder = mManager.getDaoSession().queryBuilder(ContinuityTempInfo.class);
        return queryBuilder.where(ContinuityTempInfoDao.Properties.User_id.eq(user_id)).list();

    }


    /**
     * 插入多条数据，在子线程操作
     *
     * @param ContinuityTempInfoList
     * @return
     */
    public boolean insertInfoList(final List<ContinuityTempInfo> ContinuityTempInfoList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (ContinuityTempInfo info : ContinuityTempInfoList) {
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
package com.zjw.apps3pluspro.sql.dbmanager;

import android.content.Context;

import com.zjw.apps3pluspro.sql.entity.MeasureTempInfo;
import com.zjw.apps3pluspro.sql.gen.MeasureTempInfoDao;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjw on 2017/12/6.
 */

public class MeasureTempInfoUtils {
    private final String TAG = MeasureTempInfoUtils.class.getSimpleName();
    private DaoManager mManager;

    public MeasureTempInfoUtils(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }


    /**
     * 插入一条数据
     *
     * @param info
     * @return boolean
     */
    public boolean MyUpdateData(MeasureTempInfo info) {
        boolean flag = false;

        List<MeasureTempInfo> info_list = queryToTime(info.getUser_id(), info.getMeasure_time());

        if (info_list.size() >= 1) {
            MyLog.i(TAG, "有，更新数据");
            if (info_list.get(0).getMeasure_wrist_temp().equals(info.getMeasure_wrist_temp())
            ) {
                MyLog.i(TAG, "有，数据重复不处理");
            } else {
//                MyLog.i(TAG, "有，数据不重复，更新数据");
//                info.set_id(info_list.get(0).get_id());
//                flag = updateData(info);
            }

        } else {
            MyLog.i(TAG, "没有，插入一条");
            flag = insertData(info);
        }
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     *
     * @param infoList
     * @return
     */
    public boolean insertInfoList(final List<MeasureTempInfo> infoList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (MeasureTempInfo info : infoList) {
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


    /**
     * 根据时间点查询健康数据
     *
     * @param time
     * @return boolean
     */
    public MeasureTempInfo MyQueryToTime(String user_id, String time) {

        MeasureTempInfo info = new MeasureTempInfo();

        List<MeasureTempInfo> info_list = queryToTime(user_id, time);

        if (info_list.size() >= 1) {
            info = info_list.get(0);
            return info;
        } else {

            return null;
        }

    }

    /**
     * 查询某一天最新的一条数据
     *
     * @param user_id
     * @return
     */
    public MeasureTempInfo MyQueryToDate(String user_id, String date) {

        List<MeasureTempInfo> info_list = queryToDate(user_id, date);

        if (info_list.size() >= 1) {
            MyLog.i(TAG, "有，更新数据");
            return info_list.get(0);
        } else {

            return null;
        }
    }


    /**
     * 查询某一天的所有数据
     *
     * @param user_id
     * @return
     */
    public List<MeasureTempInfo> queryToDate(String user_id, String date) {

        QueryBuilder<MeasureTempInfo> queryBuilder = mManager.getDaoSession().queryBuilder(MeasureTempInfo.class);

        return queryBuilder
                .where(MeasureTempInfoDao.Properties.User_id.eq(user_id))
                //大于等于
                .where(MeasureTempInfoDao.Properties.Measure_time.ge(date + " 00:00:00"))
                //小于等于
                .where(MeasureTempInfoDao.Properties.Measure_time.le(date + " 23:59:59"))
                //降序
                .orderDesc(MeasureTempInfoDao.Properties.Measure_time).list();

    }

    /**
     * 查询某时间段的所有数据
     *
     * @param user_id
     * @return
     */
    public List<MeasureTempInfo> queryToDateList(String user_id, String start_date, String end_date) {

        QueryBuilder<MeasureTempInfo> queryBuilder = mManager.getDaoSession().queryBuilder(MeasureTempInfo.class);


        return queryBuilder
                .where(MeasureTempInfoDao.Properties.User_id.eq(user_id))
                //大于等于
                .where(MeasureTempInfoDao.Properties.Measure_time.ge(start_date + " 00:00:00"))
                //小于等于
                .where(MeasureTempInfoDao.Properties.Measure_time.le(end_date + " 23:59:59"))
                //降序
                .orderDesc(MeasureTempInfoDao.Properties.Measure_time)
                .list();


    }


    /**
     * 查询每一天最新的一条
     *
     * @return
     */
    public List<MeasureTempInfo> queryToDayLastDateList(String user_id, ArrayList<String> week_list) {

        List<MeasureTempInfo> health_list = new ArrayList<>();
        for (int i = 0; i < week_list.size(); i++) {

            MeasureTempInfo mMeasureTempInfo = MyQueryToDate(user_id, week_list.get(i));

            if (mMeasureTempInfo != null) {
                health_list.add(mMeasureTempInfo);
            }

        }
        return health_list;

    }


    //==================修改后台状态==============

    /**
     * 根据用户ID来查询 服务状态为0的前多少条数据
     *
     * @param user_id
     * @return List<MeasureTempInfo>
     */
    public List<MeasureTempInfo> MyQueryToSync(String user_id, int count) {

        QueryBuilder<MeasureTempInfo> queryBuilder = mManager.getDaoSession().queryBuilder(MeasureTempInfo.class);


        List<MeasureTempInfo> movement_list = queryBuilder.where(MeasureTempInfoDao.Properties.User_id.eq(user_id), MeasureTempInfoDao.Properties.Sync_state.eq("0"))
                .orderDesc(MeasureTempInfoDao.Properties.Measure_time)//降序
                .list();


        List<MeasureTempInfo> result_list = new ArrayList<>();

        if (movement_list.size() > count) {

            for (int i = 0; i < count; i++) {
                result_list.add(movement_list.get(i));
            }
        } else {
            result_list = movement_list;
        }

        return result_list;

    }


    /**
     * 根据用户ID来查询 服务状态为0的所有数据
     *
     * @param user_id
     * @return List<MeasureTempInfo>
     */
    public List<MeasureTempInfo> MyQueryToSyncAll(String user_id) {
        QueryBuilder<MeasureTempInfo> queryBuilder = mManager.getDaoSession().queryBuilder(MeasureTempInfo.class);
        List<MeasureTempInfo> info_list = queryBuilder.where(MeasureTempInfoDao.Properties.User_id.eq(user_id), MeasureTempInfoDao.Properties.Sync_state.eq("0"))
                .limit(20)
                .list();
        return info_list;

    }

    public boolean MyupdateToSyncDataId(String user_id, String measure_time) {
        List<MeasureTempInfo> infoList = queryToTime(user_id, measure_time);
        return MyUpdateToSync(infoList);
    }

    /**
     * 修改列表的网络状态为1
     *
     * @param infoList
     * @return
     */

    public boolean MyUpdateToSync(final List<MeasureTempInfo> infoList) {
        boolean flag = true;
        if (infoList != null && infoList.size() > 0) {
            for (MeasureTempInfo info : infoList) {
                info.setSync_state("1");
            }
            try {
                mManager.getDaoSession().getMeasureTempInfoDao().updateInTx(infoList);
            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
            }
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
    public boolean insertData(MeasureTempInfo info) {
        info.setWarehousing_time(String.valueOf(System.currentTimeMillis()));
        MyLog.i(TAG, "插入数据 = info = " + info.toString());
        boolean flag = mManager.getDaoSession().getMeasureTempInfoDao().insert(info) == -1 ? false : true;
        return flag;
    }


    /**
     * 更新一条数据（系统ID）
     *
     * @param info
     * @return
     */
    public boolean updateData(MeasureTempInfo info) {
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
     * 根据用户ID 和 时间点
     *
     * @param user_id
     * @return
     */
    public List<MeasureTempInfo> queryToTime(String user_id, String time) {

        QueryBuilder<MeasureTempInfo> queryBuilder = mManager.getDaoSession().queryBuilder(MeasureTempInfo.class);
        return queryBuilder.where(MeasureTempInfoDao.Properties.User_id.eq(user_id), MeasureTempInfoDao.Properties.Measure_time.eq(time)).list();

    }


    //==================暂时不用==============

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<MeasureTempInfo> queryAllData() {
        return mManager.getDaoSession().loadAll(MeasureTempInfo.class);
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
            mManager.getDaoSession().deleteAll(MeasureTempInfo.class);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 删除一条记录
     *
     * @return
     */
    public boolean deleteData(MeasureTempInfo info) {
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(info);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 插入多条数据，在子线程操作
     *
     * @param infoList
     * @return
     */
    public boolean MyInsertOherData(final List<MeasureTempInfo> infoList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (MeasureTempInfo info : infoList) {
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
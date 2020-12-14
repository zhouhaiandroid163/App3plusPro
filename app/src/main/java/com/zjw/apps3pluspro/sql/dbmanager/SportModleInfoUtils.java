package com.zjw.apps3pluspro.sql.dbmanager;

import android.content.Context;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.module.home.sport.DeviceSportManager;
import com.zjw.apps3pluspro.sql.entity.SportModleInfo;
import com.zjw.apps3pluspro.sql.gen.SportModleInfoDao;
import com.zjw.apps3pluspro.utils.NewTimeUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;
import java.util.TimeZone;


/**
 * Created by zjw on 2017/12/6.
 */

public class SportModleInfoUtils {
    private final String TAG = SportModleInfoUtils.class.getSimpleName();
    private DaoManager mManager;

    public SportModleInfoUtils(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 插入一条数据
     *
     * @param info
     * @return boolean
     */
    public boolean MyUpdateData(SportModleInfo info) {
        boolean flag = false;

        List<SportModleInfo> info_list = queryToTime(info.getUser_id(), info.getTime());

        if (info_list.size() >= 1) {
            MyLog.i(TAG, "有，更新数据");
            if (info_list.get(0).getMap_data() != null
                    && info_list.get(0).getMap_data().equals(info.getMap_data())
                    && info_list.get(0).getSport_duration() != null
                    && info_list.get(0).getSport_duration().equals(info.getSport_duration())) {

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
    public boolean insertInfoList(final List<SportModleInfo> infoList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (SportModleInfo info : infoList) {
                        MyLog.i(TAG, "插入多条数据 = " + info.toString());
                        MyUpdateData(info);
                    }
                    DeviceSportManager.Companion.getInstance().uploadMoreSportData();
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 查询所有最新的一条数据
     *
     * @param user_id
     * @return
     */
    public SportModleInfo MyQueryToAll(String user_id) {

        List<SportModleInfo> info_list = queryAllTime(user_id);

        if (info_list.size() >= 1) {
            MyLog.i(TAG, "有，更新数据");
            SportModleInfo info = info_list.get(0);
            return info;
        } else {

            return null;
        }
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
            mManager.getDaoSession().deleteAll(SportModleInfo.class);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 删除一条记录
     *
     * @param info
     * @return
     */
    public boolean deleteDataToDate(SportModleInfo info) {
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


    //==================基础方法==============

    /**
     * 插入数据
     *
     * @param info
     * @return
     */
    public boolean insertData(SportModleInfo info) {
        if (info.getDataSourceType() == 0) {
            info.setRecordPointIdTime(NewTimeUtils.getLongTime(info.getTime(), NewTimeUtils.TIME_YYYY_MM_DD_HHMMSS));
            int timeZone = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / (3600 * 1000);
            info.setRecordPointTimeZone(timeZone);
        }
        info.setDeviceMac(BaseApplication.getBleDeviceTools().get_ble_mac());
        info.setWarehousing_time(String.valueOf(System.currentTimeMillis()));
        MyLog.i(TAG, "插入数据 = info = " + info.toString());
        boolean flag = mManager.getDaoSession().getSportModleInfoDao().insert(info) == -1 ? false : true;
        return flag;
    }

    /**
     * 更新一条数据（系统ID）
     *
     * @param info
     * @return
     */
    public boolean updateData(SportModleInfo info) {
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
    public List<SportModleInfo> queryToTime(String user_id, String time) {
        QueryBuilder<SportModleInfo> queryBuilder = mManager.getDaoSession().queryBuilder(SportModleInfo.class);
        return queryBuilder.where(SportModleInfoDao.Properties.User_id.eq(user_id), SportModleInfoDao.Properties.Time.eq(time)).list();
    }

    /**
     * 根据用户ID 查询所有的-降序
     *
     * @param user_id
     * @return
     */
    public List<SportModleInfo> queryAllTime(String user_id) {
        QueryBuilder<SportModleInfo> queryBuilder = mManager.getDaoSession().queryBuilder(SportModleInfo.class);
        return queryBuilder.where(SportModleInfoDao.Properties.User_id.eq(user_id))
                //降序
                .orderDesc(SportModleInfoDao.Properties.RecordPointIdTime)
                .list();
    }

    public List<SportModleInfo> queryByRecordPointDataId(String user_id, String recordPointDataId) {
        QueryBuilder<SportModleInfo> queryBuilder = mManager.getDaoSession().queryBuilder(SportModleInfo.class);
        return queryBuilder.where(SportModleInfoDao.Properties.User_id.eq(user_id), SportModleInfoDao.Properties.RecordPointDataId.eq(recordPointDataId)).list();
    }
    public List<SportModleInfo> queryByRecordPointIdTime(String user_id, long recordPointIdTime) {
        QueryBuilder<SportModleInfo> queryBuilder = mManager.getDaoSession().queryBuilder(SportModleInfo.class);
        return queryBuilder.where(SportModleInfoDao.Properties.User_id.eq(user_id), SportModleInfoDao.Properties.RecordPointIdTime.eq(recordPointIdTime)).list();
    }

    public List<SportModleInfo> queryByTime(long startTime, long endTime) {
        QueryBuilder<SportModleInfo> queryBuilder = mManager.getDaoSession().queryBuilder(SportModleInfo.class);
        return queryBuilder.where(SportModleInfoDao.Properties.User_id.eq(BaseApplication.getUserId()))
                .where(SportModleInfoDao.Properties.RecordPointIdTime.ge(startTime))
                .where(SportModleInfoDao.Properties.RecordPointIdTime.le(endTime))
                .where(SportModleInfoDao.Properties.RecordPointSportType.notEq("9"))
                .where(SportModleInfoDao.Properties.RecordPointSportType.notEq("10"))
                .orderDesc(SportModleInfoDao.Properties.RecordPointIdTime)
                .list();
    }

    public List<SportModleInfo> queryNoUploadData(int dataSourceType) {
        QueryBuilder<SportModleInfo> queryBuilder = mManager.getDaoSession().queryBuilder(SportModleInfo.class);
        return queryBuilder.where(SportModleInfoDao.Properties.User_id.eq(BaseApplication.getUserId()))
                .where(SportModleInfoDao.Properties.DataSourceType.eq(dataSourceType))
                .whereOr(SportModleInfoDao.Properties.Sync_state.isNull(), SportModleInfoDao.Properties.Sync_state.eq("0"))
                .orderDesc(SportModleInfoDao.Properties.RecordPointIdTime)
                .limit(10)
                .list();
    }

    public void updateNoUploadData(List<SportModleInfo> sportModleInfoList) {
        for (SportModleInfo sportModleInfo : sportModleInfoList) {
            sportModleInfo.setSync_state("1");
        }
        mManager.getDaoSession().getSportModleInfoDao().updateInTx(sportModleInfoList);
    }
    public List<SportModleInfo> queryByServerId(long serverId) {
        QueryBuilder<SportModleInfo> queryBuilder = mManager.getDaoSession().queryBuilder(SportModleInfo.class);
        return queryBuilder.where(SportModleInfoDao.Properties.ServiceId.eq(serverId))
                .limit(1)
                .orderDesc(SportModleInfoDao.Properties.RecordPointIdTime)
                .list();
    }

    public List<SportModleInfo> queryOldSportData() {
        QueryBuilder<SportModleInfo> queryBuilder = mManager.getDaoSession().queryBuilder(SportModleInfo.class);
        return queryBuilder
                .where(SportModleInfoDao.Properties.User_id.eq(BaseApplication.getUserId()))
                .where(SportModleInfoDao.Properties.DataSourceType.eq(""))
                .where(SportModleInfoDao.Properties.RecordPointIdTime.eq(""))
                .list();
    }

    public void updateOldSportData(List<SportModleInfo> sportModleInfoList) {
        for (SportModleInfo sportModleInfo : sportModleInfoList) {
            if (sportModleInfo.getDataSourceType() == 0) {
                sportModleInfo.setRecordPointIdTime(NewTimeUtils.getLongTime(sportModleInfo.getTime(), NewTimeUtils.TIME_YYYY_MM_DD_HHMMSS));
                int timeZone = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / (3600 * 1000);
                sportModleInfo.setRecordPointTimeZone(timeZone);
            }
        }
        mManager.getDaoSession().getSportModleInfoDao().updateInTx(sportModleInfoList);
    }

}
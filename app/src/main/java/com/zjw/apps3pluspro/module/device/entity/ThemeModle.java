package com.zjw.apps3pluspro.module.device.entity;


import com.zjw.apps3pluspro.utils.ThemeUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.io.Serializable;


public class ThemeModle implements Serializable {

    byte[] data;


    int MTU = 182; //单次传输最大长度
    int DataLenth = MTU - 2;//每个包的有效数据长度
    int BlockSize = 4 * 1024; //块的大小
    int BlockTotalNum = 0;//一共需要多少块
    int EndBolckSize = 0;    //最后一块的大小
    int PageDataMax = 0; //一块多少个包,最后一个除外
    int TotalPageSize = 0; //总包数-SN需要用到

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getMTU() {
        return MTU;
    }

    public void setMTU(int MTU) {
        this.MTU = MTU;
    }

    public int getDataLenth() {
        return DataLenth;
    }

    public void setDataLenth(int dataLenth) {
        DataLenth = dataLenth;
    }

    public int getBlockSize() {
        return BlockSize;
    }

    public void setBlockSize(int blockSize) {
        BlockSize = blockSize;
    }

    public int getBlockTotalNum() {
        return BlockTotalNum;
    }

    public void setBlockTotalNum(int blockTotalNum) {
        BlockTotalNum = blockTotalNum;
    }

    public int getEndBolckSize() {
        return EndBolckSize;
    }

    public void setEndBolckSize(int endBolckSize) {
        EndBolckSize = endBolckSize;
    }

    public int getPageDataMax() {
        return PageDataMax;
    }

    public void setPageDataMax(int pageDataMax) {
        PageDataMax = pageDataMax;
    }

    public int getTotalPageSize() {
        return TotalPageSize;
    }

    public void setTotalPageSize(int totalPageSize) {
        TotalPageSize = totalPageSize;
    }

    public ThemeModle() {
        super();
    }

    public static final String TAG = "ThemeModle";

    @Override
    public String toString() {
        return "ThemeModle{" +
//                "data=" + Arrays.toString(data) +
                "data.lenght=" + data.length +
                ", MTU=" + MTU +
                ", DataLenth=" + DataLenth +
                ", BlockSize=" + BlockSize +
                ", BlockTotalNum=" + BlockTotalNum +
                ", EndBolckSize=" + EndBolckSize +
                ", PageDataMax=" + PageDataMax +
                ", TotalPageSize=" + TotalPageSize +
                '}';
    }

    public ThemeModle(byte[] data, int mtu) {
        super();

        int block_total_num = 0;
        int block_size = 4 * 1024;
        int page_data_max = 0;
        int data_lenth = 0;
        int total_page_size = 0;
        int end_bolck_size = 0;
        int end_page_size = 0;

        data_lenth = mtu - 2;

        String log = "";

        //计算需要多少块
        //取余=0，整除
        if (data.length % block_size == 0) {

            block_total_num = data.length / block_size;

            MyLog.i(TAG, "主题数据 block_total_num1 = " + block_total_num);

            end_bolck_size = BlockSize;

            MyLog.i(TAG, "主题数据 end_bolck_size2 = " + end_bolck_size);


            if (block_size % data_lenth == 0) {
                page_data_max = block_size / data_lenth;
                MyLog.i(TAG, "主题数据 page_data_max1 = " + block_total_num);
            } else {
                page_data_max = block_size / data_lenth + 1;
                MyLog.i(TAG, "主题数据 page_data_max2 = " + block_total_num);
            }

            total_page_size = block_total_num * page_data_max;

            MyLog.i(TAG, "主题数据 total_page_size = " + total_page_size);


        }
        //不整除
        else {
            block_total_num = data.length / block_size + 1;

            MyLog.i(TAG, "主题数据 block_total_num2 = " + block_total_num);


            end_bolck_size = data.length % block_size;

            MyLog.i(TAG, "主题数据 end_bolck_size2 = " + end_bolck_size);

            if (block_size % data_lenth == 0) {
                page_data_max = block_size / data_lenth;
            } else {
                page_data_max = block_size / data_lenth + 1;
            }

            total_page_size = (block_total_num - 1) * page_data_max;


            if (end_bolck_size % data_lenth == 0) {
                end_page_size = end_bolck_size / data_lenth;
            } else {
                end_page_size = end_bolck_size / data_lenth + 1;
            }

            total_page_size = total_page_size + end_page_size;

        }


        setData(data);
        setMTU(mtu);
        setDataLenth(mtu - 2);
        setBlockSize(block_size);
        setBlockTotalNum(block_total_num);
        setEndBolckSize(end_bolck_size);
        setPageDataMax(page_data_max);
        setTotalPageSize(total_page_size);
        MyLog.i(TAG, "主题数据 2= " + toString());

    }


    public String getLog() {

        String log = "总大小 = " + (float) (data.length) / 1024 + "K(" + data.length + ")" + "\n"
                + "总块数 = " + BlockTotalNum + "\n"
                + "每一块包数 = " + PageDataMax + "\n"
                + "总包数 = " + TotalPageSize + "\n"
                + "MTU = " + MTU + "\n"
                + "DataLenth = " + DataLenth + "\n";

        return log;
    }

    //最后一块？
    public boolean isLastBlock(int now_block) {
        if (now_block >= BlockTotalNum) {
            return true;
        } else {
            return false;
        }

    }

    //最后一块最后一个包
    public boolean isLastPage(int now_sn) {
        if (now_sn >= TotalPageSize) {
            return true;
        } else {
            return false;
        }
    }

    //当前最后一个包
    public boolean isNowBlockLastPage(int now_page) {

        if (now_page >= PageDataMax) {
            return true;
        } else {
            return false;
        }

    }

    //获取当前包的长度
    public int getNowPageNumber(int now_page, int sn_num) {

//        MyLog.i(TAG, "主题数据 now_page = " + now_page + "  sn_num = " + sn_num);

        //最后一块最后一个包
        if (isLastPage(sn_num)) {
//            MyLog.i(TAG, "主题数据 now_page = X1");
            //取余=0，整除

            if (EndBolckSize % DataLenth == 0) {
//                MyLog.i(TAG, "主题数据 now_page = X1 整除");
                return DataLenth;
            } else {
//                MyLog.i(TAG, "主题数据 now_page = X1 不整除");
                return EndBolckSize % DataLenth;
            }
        } else if (isNowBlockLastPage(now_page)) {
//            MyLog.i(TAG, "主题数据 now_page = X2");
            //取余=0，整除
            if (BlockSize % DataLenth == 0) {
                return DataLenth;
            } else {
                return BlockSize % DataLenth;
            }

        } else {
//            MyLog.i(TAG, "主题数据 now_page = X3");
            return DataLenth;
        }


    }

    //获取需要发送的数据-c裁剪数据
    public byte[] getSendData(int now_block, int now_page, int sn_num) {

        int data_lan = data.length;
        int now_page_number = getNowPageNumber(now_page, sn_num);
        int start_pos = (now_block - 1) * BlockSize + (now_page - 1) * DataLenth;
//        MyLog.i(TAG, "主题数据 sn_num = " + sn_num + " start_pos = " + start_pos + " now_page_number = " + now_page_number);
//        MyLog.i(TAG, "主题数据 sn_num = " + sn_num + " data_lan = " + data_lan);

        int now_data_len = start_pos + now_page_number;

        if (start_pos >= 0 && start_pos <= data_lan && now_data_len >= 0 && now_data_len <= data_lan) {
//            MyLog.i(TAG, "主题数据 sn_num = " + sn_num + " 数据通过");
            return ThemeUtils.subBytes(data, start_pos, now_page_number);
        } else {
//            MyLog.i(TAG, "主题数据 sn_num = " + sn_num + " 数据不通过");
            return null;
        }

    }

}

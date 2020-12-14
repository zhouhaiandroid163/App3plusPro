package com.zjw.apps3pluspro.utils;

import java.util.List;

public class BinUtils {

    //System.arraycopy()方法
    //数据拼接
    public static byte[] byteMerger(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    //System.arraycopy()方法
    public static byte[] byteMergerList(List<byte[]> byte_list) {
        byte[] rsult = null;
        if (byte_list.size() > 0) {
            if (byte_list.size() > 1) {
                rsult = byteMerger(byte_list.get(0), byte_list.get(1));
                for (int i = 2; i < byte_list.size(); i++) {
                    rsult = byteMerger(rsult, byte_list.get(i));
                }
            } else {
                rsult = byte_list.get(0);
            }
        }
        return rsult;
    }


}

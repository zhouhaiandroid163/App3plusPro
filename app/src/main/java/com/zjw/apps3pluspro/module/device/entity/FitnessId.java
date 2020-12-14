package com.zjw.apps3pluspro.module.device.entity;

/**
 * Created by android
 * on 2020/11/5
 */
public class FitnessId {
    public long time;
    public byte[] id;

    public FitnessId(long time, byte[] id) {
        this.id = id;
        this.time = time;
    }
}

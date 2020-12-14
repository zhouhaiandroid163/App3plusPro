package com.zjw.apps3pluspro.module.home.entity;

import java.io.Serializable;

public class PageItem implements Serializable {
    public int order;// 设备给的序号 0 不可移动 10 - 99 展示隐藏
    public int position;
    public String name;
    public int index;   // index name 一一对应
    public boolean isMark = false;

    public PageItem(int index, boolean isMark) {
        this.index = index;
        this.isMark = isMark;
    }

    public PageItem() {
    }
}

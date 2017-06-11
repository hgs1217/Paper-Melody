package com.papermelody.model;

/**
 * Created by 潘宇杰 on 2017-6-11 0011.
 */

public class HistoryMusic {
    private String name;
    private long createTime;
    private long size;

    public HistoryMusic(String name, long createTime, long size) {
        this.name = name;
        this.createTime = createTime;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getSize() {
        return size;
    }
}

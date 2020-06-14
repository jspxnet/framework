package com.github.jspxnet.util;

/**
 * Created by ChenYuan on 2017/6/2.
 * 一个可变的整数来避免创建太多个Long对象
 */
public final class MutableLong {
    private long val;
    private long lastTimeMillis = System.currentTimeMillis();

    public MutableLong(long val) {

        this.val = val;
    }

    public long get() {
        return this.val;
    }

    public void set(long val) {
        this.val = val;
    }

    public long getLastTimeMillis() {
        return lastTimeMillis;
    }

    public void setLastTimeMillis(long lastTimeMillis) {
        this.lastTimeMillis = lastTimeMillis;
    }

    // 为了方便打印
    @Override
    public String toString() {
        return Long.toString(val);
    }
}

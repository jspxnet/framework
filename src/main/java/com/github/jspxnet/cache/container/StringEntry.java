package com.github.jspxnet.cache.container;

import com.github.jspxnet.utils.DateUtil;

import java.io.Serializable;

public class StringEntry implements Serializable, Cloneable {
    public StringEntry() {

    }

    //@RId
    private String key;
    /**
     * 缓存对象
     */
    private String value;

    /**
     * 创建时间
     */
    private final long createTime = System.currentTimeMillis();

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isExpired(int second) {
        return System.currentTimeMillis() - createTime > second * DateUtil.SECOND;
    }

}

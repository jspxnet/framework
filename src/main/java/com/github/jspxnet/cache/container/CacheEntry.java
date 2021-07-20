/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.cache.container;

import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-12
 * Time: 18:11:09
 */
@Slf4j
public class CacheEntry implements Serializable, Cloneable {

    private static final int ONE_SECOND = DateUtil.SECOND;
    private static final int MAX_KEY_LENGTH = 250;

    public CacheEntry() {

    }

    /**
     * 缓存关键字 key.
     */
    //@RId
    private String key;
    /**
     * 缓存对象
     */
    private Object value;
    /**
     * 创建时间
     */
    private final long createTime = System.currentTimeMillis();
    /**
     * 最后访问时间The last access time.
     */
    private long lastAccessTime = System.currentTimeMillis();
    /**
     * 提取次数 The number of times the element was hit.
     */
    private long hitCount = 0;
    /**
     * 最后替换时间
     */
    private long lastUpdateTime = System.currentTimeMillis();

    /**
     * 生命周期
     */
    private int timeToLive = 0;


    public boolean isAccessKeep() {
        return accessKeep;
    }

    public void setAccessKeep(boolean accessKeep) {
        this.accessKeep = accessKeep;
    }

    /**
     * 更新后继续
     */
    private boolean accessKeep = false;

    public String getKey() {
        return key;
    }

    public void setKey(String key) throws Exception {
        if (key == null) {
            String error = "cache key not is null";
            throw new Exception(error);
        }
        if (StringUtil.getLength(key) > MAX_KEY_LENGTH) {
            log.error("cache key max " + MAX_KEY_LENGTH + ",this key length is " + StringUtil.getLength(key));
            String error = "cache key max " + MAX_KEY_LENGTH + ",this key length is " + StringUtil.getLength(key);
            throw new Exception(error);
        }
        this.key = key;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime() {
        this.lastAccessTime = System.currentTimeMillis();
    }

    public long getHitCount() {
        return hitCount;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime() {
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public int getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    public long getExpirationTime() {
        if (timeToLive <= 0) {
            return Long.MAX_VALUE;
        }
        long expirationTime = 0;
        if (accessKeep) {
            expirationTime = lastAccessTime + timeToLive * ONE_SECOND;
        } else {
            expirationTime = createTime + timeToLive * ONE_SECOND;
        }
        return expirationTime;
    }

    //是过期的，等待删除
    public boolean isExpired() {

        return System.currentTimeMillis() > getExpirationTime();
    }

    public Object getValue() {
        hitCount++;
        return value;
    }

    public long getSerializedSize() {
        return ObjectUtil.getSerializedSize(value);
    }


}
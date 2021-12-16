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
import lombok.Data;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-12
 * Time: 18:11:09
 */
@Data
public class CacheEntry implements Serializable, Cloneable {


    private static final int MAX_KEY_LENGTH = 512;

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
    private long accessTime = System.currentTimeMillis();

    /**
     * 最后替换时间
     */
    private long lastTime = System.currentTimeMillis();

    /**
     * 生命周期
     */
    private int live = 0;


    /**
     * 更新后继续
     */
    private boolean keep = false;

    public void setKey(String key) throws Exception {
        if (key == null) {
            String error = "cache key not is null";
            throw new Exception(error);
        }
        if (StringUtil.getLength(key) > MAX_KEY_LENGTH) {
            String error = "cache key max " + MAX_KEY_LENGTH + ",this key length is " + StringUtil.getLength(key);
            throw new Exception(error);
        }
        this.key = key;
    }

    public long getExpirationTime() {
        if (live <= 0) {
            return Long.MAX_VALUE;
        }
        long expirationTime = 0;
        if (keep) {
            expirationTime = accessTime + live * DateUtil.SECOND;
        } else {
            expirationTime = createTime + live * DateUtil.SECOND;
        }
        return expirationTime;
    }

    //是过期的，等待删除
    public boolean isExpired() {
        return System.currentTimeMillis() > getExpirationTime();
    }

    public long getSerializedSize() {
        return ObjectUtil.getSerializedSize(value);
    }


}
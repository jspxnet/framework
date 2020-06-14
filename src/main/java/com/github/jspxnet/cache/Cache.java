/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.cache;

import com.github.jspxnet.cache.container.CacheEntry;
import com.github.jspxnet.cache.event.CacheEventListener;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-12
 * Time: 18:09:24
 */
public interface Cache extends Serializable, Cloneable {

    boolean registeredEventListeners(CacheEventListener eventListener);

    void put(com.github.jspxnet.cache.container.CacheEntry entry) throws IllegalArgumentException, IllegalStateException,
            CacheException;

    String getDiskStorePath();

    com.github.jspxnet.cache.container.CacheEntry get(String key) throws IllegalStateException, CacheException;

    Set<String> getKeys() throws IllegalStateException, CacheException;

    boolean remove(String key) throws IllegalStateException;

    void removeAll() throws IllegalStateException, CacheException;

    void flush() throws IllegalStateException, CacheException;

    long getSize() throws IllegalStateException, CacheException;

    long calculateInMemorySize() throws IllegalStateException, CacheException;

    String getName();

    void setName(String name);

    void setEternal(boolean eternal);


    boolean isEternal();


    int getMaxElements();

    void setMaxElements(int maxElements);

    @Override
    String toString();

    boolean isExpired(com.github.jspxnet.cache.container.CacheEntry entry) throws IllegalStateException, NullPointerException;

    boolean isElementOnDisk(String key);


    void setDiskStorePath(String diskStorePath) throws CacheException;

    void setSecond(int second);

    int getSecond();

    void dispose() throws IllegalStateException;

    void init() throws Exception;

    boolean replace(CacheEntry entry);

    boolean isFull();

    void destroy();

    int getEventSize();

    IStore getStore();
}
/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.cache.core;

import com.github.jspxnet.cache.Cache;
import com.github.jspxnet.cache.CacheManager;
import com.github.jspxnet.cache.IStore;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.cache.container.CacheEntry;
import com.github.jspxnet.cache.event.CacheEventListener;
import com.github.jspxnet.cache.store.MemoryStore;
import com.github.jspxnet.cache.store.SingleRedissonStore;
import com.github.jspxnet.sioc.annotation.Destroy;
import com.github.jspxnet.sioc.annotation.Init;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-13
 * Time: 9:42:07
 *
 */
@Slf4j
public class JSCache implements Runnable, Cache {
    private final Set<CacheEventListener> cacheEventListeners = new CopyOnWriteArraySet<>();
    //存储容器
    private IStore store = null;

    @Override
    public IStore getStore() {
        return store;
    }

    public void setStore(IStore store) {
        this.store = store;
        this.store.setName(name);
    }

    /**
     * 生命周期 以秒为单位
     */
    private int second = 0;

    /**
     * 这个cache 是否为永远的
     */
    private boolean eternal;

    /**
     * 最大允许长度空间
     */
    private int maxElements = 50000;

    /**
     * 缓存文件保存路径
     */
    private String diskStorePath;

    private String name = null;

    /**
     * @return 等待秒
     */
    @Override
    public int getSecond() {
        return second;
    }

    /**
     * @param second 等待秒
     */
    @Override
    public void setSecond(int second) {
        this.second = second;
        store.setSecond(second);
    }

    /**
     * @return 最大元素
     */
    @Override
    public int getMaxElements() {
        return maxElements;
    }

    /**
     * @param maxElements 最大元素
     */
    @Override
    public void setMaxElements(int maxElements) {
        this.maxElements = maxElements;
    }

    /**
     * @param eventListener 监听事件
     * @return 注册监听
     */
    @Override
    public boolean registeredEventListeners(CacheEventListener eventListener) {
        return cacheEventListeners.add(eventListener);
    }

    /**
     * @return 得到注册事件数量
     */
    @Override
    public int getEventSize() {
        return cacheEventListeners.size();
    }

    /**
     * @return 缓存名称
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param name 设置缓存名称
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param entry 放入实体对象
     */
    @Override
    public void put(CacheEntry entry) {

        if (entry.getLive()<=0)
        {
            entry.setLive(second);
        }
        if (store instanceof SingleRedissonStore)
        {
            //redis 就直接放了，不限制大小

            store.put(entry);
        } else
        {
            if (store.size() > maxElements)
            {
                CacheEntry evictedEntry = store.getEvictedCacheEntry();
                if (evictedEntry != null) {
                    if (store.containsKey(entry.getKey())) {
                        for (CacheEventListener eventListener : cacheEventListeners) {
                            eventListener.notifyElementUpdated(this, evictedEntry);
                        }
                    } else {
                        for (CacheEventListener eventListener : cacheEventListeners) {
                            eventListener.notifyElementPut(this, evictedEntry);
                        }
                    }
                }
            }
            if (store.size() < maxElements) {
                store.put(entry);
            }
        }
    }

    /**
     * @param entry 实体
     * @return 替换实体是否成功
     */
    @Override
    public boolean replace(CacheEntry entry) {
        store.put(entry);
        return true;
    }

    /**
     * @param key 缓存对象
     * @return 得到缓存实体
     */
    @Override
    public CacheEntry get(String key) {
        return store.get(key);
    }

    /**
     * @return 得到缓存key集
     */
    @Override
    public Set<String> getKeys() {
        return store.getKeys();
    }

    /**
     * @param key key对象
     * @return 删除是否成功
     * @throws IllegalStateException 异常
     */
    @Override
    public boolean remove(String key) throws IllegalStateException {
        com.github.jspxnet.cache.container.CacheEntry entry = store.remove(key);
        for (CacheEventListener eventListener : cacheEventListeners) {
            if (entry != null) {
                eventListener.notifyElementRemoved(this, entry);
            }
        }
        return entry != null;
    }

    /**
     * 删除所有对象
     *
     * @throws IllegalStateException                   异常
     * @throws com.github.jspxnet.cache.CacheException 异常
     */
    @Override
    public void removeAll() throws IllegalStateException, com.github.jspxnet.cache.CacheException {
        for (CacheEventListener eventListener : cacheEventListeners) {
            eventListener.notifyRemoveAll(this);
        }
        store.removeAll();
    }

    /**
     * 刷新
     *
     * @throws IllegalStateException                   异常
     * @throws com.github.jspxnet.cache.CacheException 异常
     */
    @Override
    public void flush() throws IllegalStateException, com.github.jspxnet.cache.CacheException {

    }

    @Override
    public List<Object> getAll()  {
        Collection<CacheEntry>  collection = store.getAll();
        return BeanUtil.copyFieldList(collection,"value");
    }

    /**
     * @return 得到内存使用空间
     * @throws IllegalStateException 异常
     */
    @Override
    public long calculateInMemorySize() throws IllegalStateException {
        return store.getSizeInBytes();
    }

    /**
     * @param entry 实体
     * @return 是否超时
     * @throws IllegalStateException 异常
     * @throws NullPointerException  异常
     */
    @Override
    public boolean isExpired(CacheEntry entry) throws IllegalStateException, NullPointerException {
        if (entry == null) {
            return true;
        }
        CacheEntry element = store.get(entry.getKey());
        return element == null || element.isExpired();
    }


    /**
     * @param key key对象
     * @return 是否保存在磁盘，目前只为了兼容EHCache
     */
    @Override
    public boolean isElementOnDisk(String key) {
        return false;
    }

    /**
     * @return 是否永久保存
     */
    @Override
    public boolean isEternal() {
        return eternal;
    }

    /**
     * @param eternal 永久保存
     */
    @Override
    public void setEternal(boolean eternal) {
        this.eternal = eternal;
    }

    /**
     * @return 得到磁盘保存路径
     */
    @Override
    public String getDiskStorePath() {
        return diskStorePath;
    }

    /**
     * @param diskStorePath 设置磁盘保存路径
     */
    @Override
    public void setDiskStorePath(String diskStorePath) {
        this.diskStorePath = diskStorePath;
    }

    @Override
    public void dispose() throws IllegalStateException {
        store.dispose();
    }

    /**
     * @return 当前缓存数量
     */
    @Override
    public long getSize() {
        return store.size();
    }

    /**
     * @return 是否已经保存满了
     */
    @Override
    public boolean isFull() {
        return store.size() >= maxElements;
    }

    /**
     * 初始化缓存
     */
    @Init
    @Override
    public void init() {
        CacheManager cacheManager = JSCacheManager.getCacheManager();
        if (cacheManager.containsKey(name)) {
            return;
        }
        if (store == null) {
            store = new MemoryStore();
        }
        this.store.setMaxElements(maxElements);
        this.store.setSecond(second);
        if (StringUtil.isNull(this.store.getName()))
        {
            this.store.setName(name);
        }
        cacheManager.registeredCache(this);
        log.info("create cache " + name);
    }

    /**
     * 卸载所有数据
     */
    @Destroy
    @Override
    public void destroy() {
        cacheEventListeners.clear();
        if (store != null) {
            store.removeAll();
            store.dispose();
        }
    }


    /**
     * 删除过期的对象
     */
    @Override
    public void run() {
        if (!store.isUseTimer()) {
            return;
        }

        try {
            for (String keys : store.getKeys()) {
                CacheEntry entry = store.get(keys);
                if (entry != null && entry.isExpired()) {
                    for (CacheEventListener eventListener : cacheEventListeners) {
                        eventListener.notifyElementExpired(this, entry);
                    }
                    store.remove(keys);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
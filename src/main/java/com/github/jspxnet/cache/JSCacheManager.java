/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
 * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.8+  x86/amd64
 *
 */
package com.github.jspxnet.cache;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.cache.container.CacheEntry;
import com.github.jspxnet.cache.core.JSCache;
import com.github.jspxnet.cache.event.CacheEventListener;
import com.github.jspxnet.cache.store.Store;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.sioc.SchedulerManager;
import com.github.jspxnet.sioc.scheduler.SchedulerTaskManager;
import com.github.jspxnet.sioc.scheduler.TaskProxy;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-14
 * Time: 12:23:58
 * 1.当线程池小于corePoolSize时，新提交任务将创建一个新线程执行任务，即使此时线程池中存在空闲线程。
 * 2.当线程池达到corePoolSize时，新提交任务将被放入workQueue中，等待线程池中任务调度执行
 * 3.当workQueue已满，且{@code maximumPoolSize >corePoolSize}时，新提交任务会创建新线程执行任务
 */
@Slf4j
public class JSCacheManager implements CacheManager {

    private static final List<Cache> caches = Collections.synchronizedList(new ArrayList<>());
    private static final CacheManager CACHE_MANAGER = new JSCacheManager();


    /**
     * 公开的构造子，外界可以直接实例化
     */
    public JSCacheManager() {

    }

    static public CacheManager getCacheManager() {
        return CACHE_MANAGER;
    }

    /**
     * @param cls            名称
     * @param keepTimeSecond 单位 秒 second
     * @param maxElements    最大数量
     * @param eternal        是否永远保存
     * @param diskStorePath  保存目录
     * @return 返回cache
     */
    @Override
    public Cache createCache(IStore store, Class<?> cls, int keepTimeSecond, int maxElements, boolean eternal, String diskStorePath) {
        JSCache cache = new JSCache();
        cache.setStore(store);
        cache.setName(cls.getName());
        cache.setSecond(keepTimeSecond);
        cache.setMaxElements(maxElements);
        cache.setEternal(eternal);
        cache.setDiskStorePath(diskStorePath);
        cache.init();
        registeredCache(cache);
        return cache;
    }

    @Override
    public Cache createCache(IStore store, Map<String, String> configMap) {
        try {
            return createCache(store, ClassUtil.loadClass(configMap.get("name")), StringUtil.toInt(configMap.get("keepTime")),
                    StringUtil.toInt(configMap.get("maxElements")), StringUtil.toBoolean(configMap.get("eternal")),
                    configMap.get("diskStorePath"));
        } catch (ClassNotFoundException e) {
            log.error("createCache", e);
        }
        return getCache(com.github.jspxnet.cache.DefaultCache.class);
    }

    @Override
    public boolean containsKey(String key) {
        for (Cache cache : caches) {
            if (cache==null)
            {
                continue;
            }
            if (cache.getName().equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    synchronized public void registeredCache(JSCache cache) {
        if (cache == null) {
            return;
        }
        if (!containsKey(cache.getName())) {
            if (cache.getSecond() > 0 && cache.getStore().isUseTimer()) {
                SchedulerManager schedulerManager = SchedulerTaskManager.getInstance();
                schedulerManager.add(cache.getName(), "缓存清理", "0 */1 * * * *", TaskProxy.SYS_TYPE, cache);
            }
            caches.add(cache);
        }
    }

    @Override
    public Cache getCache(Class<?> cls) {
        return getCache(cls.getName());
    }

    /**
     * 不需要注册，创建的时候自己会注册
     *
     * @param key cachekey
     * @return cache object
     */
    @Override
    public Cache getCache(String key) {
        for (Cache cache : caches) {
            if (cache != null && cache.getName().equalsIgnoreCase(key)) {
                return cache;
            }
        }
        //查询本地是否配置了本cache  begin

        BeanFactory beanFactory = EnvFactory.getBeanFactory();
        if (beanFactory != null) {
            String cacheName = key;
            String namespace = Environment.CACHE;
            if (key.contains("@")) {
                cacheName = StringUtil.substringBefore(key, "@");
                namespace = StringUtil.substringAfterLast(key, "@");
            }
            try {
                if (beanFactory.containsBean(cacheName, namespace)) {
                    Object o = beanFactory.getBean(cacheName, namespace);
                    if ((o instanceof Cache)) {
                        return (Cache) o;
                    }
                }
            } catch (Exception e) {
                log.error("查询cache", e);
            }
        }
        try {
            if (key != null && key.contains(".") && !DefaultCache.class.getName().equalsIgnoreCase(key)
                    && !LockCache.class.getName().equalsIgnoreCase(key)) {
                Store defaultStore = (Store) beanFactory.getBean(Environment.DEFAULT_STORE, Environment.CACHE);
                Class<?> cls = ClassUtil.loadClass(key);
                return createCache(defaultStore, cls, DateUtil.MINUTE * 3, 1000, false, null);
            }
        } catch (Exception e) {
            log.error("载入类", e);
        }
        Cache cache = (Cache) beanFactory.getBean(DefaultCache.class, Environment.CACHE);
        cache.setName(DefaultCache.class.getName());
        return cache;

    }

    /**
     *
     * @return 得到缓存列表
     */
    @Override
    public Collection<Cache> getCaches() {
        return caches;
    }

    /**
     *
     * @param cacheName 缓存名称
     * @param key key
     * @return 得到缓存对象
     */
    static public Object get(String cacheName, String key) {
        Cache cache = CACHE_MANAGER.getCache(cacheName);
        if (cache == null) {
            return null;
        }
        if (StringUtil.isNull(cache.getName())) {
            cache.setName(cacheName);
        }
        CacheEntry cacheEntry = cache.get(key);
        if (cacheEntry == null) {
            return null;
        }
        return cacheEntry.getValue();
    }

    /**
     *
     * @param tClass  内存类型
     * @param key key
     * @return 得到缓存对象
     */
    static public Object get(Class<?> tClass, String key) {
        return get(tClass.getName(), key);
    }

    /**
     *
     * @param cls 内存类型
     * @param key key
     * @param o 放入对象
     * @return 放入缓存
     */
    static public boolean put(Class<?> cls, String key, Object o) {
        return put(cls.getName(), key, o,0);
    }

    /**
     *
     * @param key key
     * @return 锁定
     */
    static public boolean lock(String key) {
        return put(LockCache.class, key, 1, 10);
    }

    /**
     *
     * @param key key
     * @param timeToLive 锁保持时间，单位为秒
     * @return 锁定
     */
    static public boolean lock(String key, int timeToLive) {
        Cache cache = CACHE_MANAGER.getCache(LockCache.class);
        if (cache == null || key == null) {
            return false;
        }
        return cache.lock(key,timeToLive);
    }

    /**
     *
     * @param key key
     * @return 判断是否加锁
     */
    static public boolean isLock(String key) {
        Cache cache = CACHE_MANAGER.getCache(LockCache.class);
        if (cache == null || key == null) {
            return false;
        }
        return cache.isLock(key);
    }

    /**
     *
     * @param key key
     * @return 解锁
     */
    static public boolean unLock(String key) {
        Cache cache = CACHE_MANAGER.getCache(LockCache.class);
        if (cache == null || key == null) {
            return false;
        }
        return cache.unLock(key);
    }

    /**
     * 放入模版类型数据,到期删除,的单例模式
     *
     * @param cls 类对象
     * @param key       key
     * @param o         对象
     * @param timeToLive 时间单位为秒
     * @return 放入模版类型数据, 到期删除, 的单例模式
     */
    static public boolean put(Class<?> cls, String key, Object o,int timeToLive) {
        if (key == null) {
            return false;
        }
        return put(cls.getName(), key, o, timeToLive);
    }

    /**
     *
     * @param cacheName 缓存名称
     * @param key key
     * @param o 对象
     * @return 放入缓存
     */
    static public boolean put(String cacheName, String key, Object o)
    {
        return put(cacheName, key, o,0);
    }
    /**
     * @param cacheName  缓存名称
     * @param key        key
     * @param o          对象
     * @param timeToLive 超时
     * @return 可以放入单例的对象
     */
    static public boolean put(String cacheName, String key, Object o, int timeToLive) {
        Cache cache = CACHE_MANAGER.getCache(cacheName);
        if (cache == null || key == null) {
            return false;
        }
        CacheEntry cacheEntry = new CacheEntry();
        try {
            cacheEntry.setKey(key);
            cacheEntry.setValue(o);
        } catch (Exception e) {
            log.error("缓存数据规则不正确", e);
        }
        cacheEntry.setLive(timeToLive);
        cache.put(cacheEntry);
        return true;
    }


    /**
     * @param cls  缓存名称
     * @param keys 删除KEY
     * @return 删除
     */
    static public boolean remove(Class<?> cls, String... keys) {
        Cache cache = CACHE_MANAGER.getCache(cls);
        if (cache == null) {
            return false;
        }
        for (String key : keys) {
            if (key == null) {
                continue;
            }
            cache.remove(key);
        }
        return true;
    }

    /**
     * 删除 所有
     *
     * @param cacheName 缓存名称
     */
    static public void removeAll(String cacheName) {
        Cache cache = CACHE_MANAGER.getCache(cacheName);
        if (cache == null) {
            return;
        }
        cache.removeAll();
    }

    static public void removeAll(Class<?> cls) {
        removeAll(cls.getName());
    }

    static public void queryRemove(Class<?> cls, String trem) {
        if (cls == null) {
            return;
        }
        queryRemove(cls.getName(), trem);
    }

    static public void queryRemove(String cacheName, String trem) {
        Cache cache = CACHE_MANAGER.getCache(cacheName);
        if (cache == null) {
            return;
        }
        if (StringUtil.ASTERISK.equals(trem)) {
            removeAll(cacheName);
            return;
        }

        Set<String> set = cache.getKeys();
        if (set != null) {
            for (String key : set) {
                if (key != null && StringUtil.getPatternFind(key, trem)) {
                    cache.remove(key);
                }
            }
        }
    }

    /**
     * @param cacheName 缓存名称
     * @return 得到大小
     */
    static public long getSize(Class<?> cacheName) {
        return CACHE_MANAGER.getCache(cacheName).getSize();
    }

    /**
     * 注册缓存监听
     *
     * @param cacheName          缓存名称
     * @param cacheEventListener 监听
     * @return 是否成功
     */
    static public boolean registeredEventListeners(String cacheName, CacheEventListener cacheEventListener) {
        Cache cache = CACHE_MANAGER.getCache(cacheName);
        return cache.registeredEventListeners(cacheEventListener);
    }

    /**
     * @param cla                类对象
     * @param cacheEventListener 监听
     * @return 是否成功
     */
    static public boolean registeredEventListeners(Class<?> cla, CacheEventListener cacheEventListener) {
        return registeredEventListeners(cla.getName(), cacheEventListener);
    }

    /**
     * 得到事件个数
     *
     * @param cacheName 缓存
     * @return 事件数量
     */
    static public int getEventSize(String cacheName) {
        return CACHE_MANAGER.getCache(cacheName).getEventSize();
    }

    /**
     * @param cacheName 缓存名称
     * @return 得到缓存 key
     */
    static public Set<String> getKeys(String cacheName) {
        return CACHE_MANAGER.getCache(cacheName).getKeys();
    }

    /**
     * @param cla 缓存名称
     * @return 一次性得到所有数据
     */
    static public List<Object> getAll(Class<?> cla) {
        return getAll(cla.getName());
    }

    /**
     * @param cacheName 缓存名称
     * @return 一次性得到所有数据
     */
    static public List<Object> getAll(String cacheName) {
        return CACHE_MANAGER.getCache(cacheName).getAll();
    }

    /**
     * @param cla 类名
     * @return 得到缓存 key
     */
    static public Set<String> getKeys(Class<?> cla) {
        if (cla == null) {
            return new HashSet<>(0);
        }
        return getKeys(cla.getName());
    }


    /**
     * 打印缓存d
     *
     * @param cacheName 缓存名称
     */
    static public void printKeys(String cacheName) {
        Cache cache = CACHE_MANAGER.getCache(cacheName);
        for (String key : cache.getKeys()) {
            log.debug(key);
        }
    }


    static public void shutdown() {
        for (Cache cache : caches) {
            cache.destroy();
        }
        caches.clear();
    }

}
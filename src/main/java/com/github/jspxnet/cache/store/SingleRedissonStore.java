package com.github.jspxnet.cache.store;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.cache.CacheException;
import com.github.jspxnet.cache.IStore;
import com.github.jspxnet.cache.container.CacheEntry;
import com.github.jspxnet.cache.redis.RedissonClientConfig;
import com.github.jspxnet.sioc.annotation.Init;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 但缓存下是用,分布式多缓存下是用 RedisStore
 */
//@Bean(bind = SingleRedissonStore.class)
@Slf4j
public class SingleRedissonStore extends Store implements IStore {
    private static RedissonClient redisson = null;
    private static  boolean useCache;

    public SingleRedissonStore() {

    }

    @Init
    public void init() {
        if (redisson != null) {
            return;
        }
        redisson =  (RedissonClient)EnvFactory.getBeanFactory().getBean(RedissonClientConfig.class);
        useCache = EnvFactory.getEnvironmentTemplate().getBoolean(Environment.useCache);
    }


    @Override
    public boolean isUseTimer() {
        return false;
    }

    private String cacheKey;

    @Override
    public void put(CacheEntry entry) {

        if (!useCache||entry.getKey()==null || cacheKey==null)
        {
            return;
        }
        RMap<String,CacheEntry> rMap = redisson.getMap(cacheKey);
        rMap.put(entry.getKey(), entry);
        if (getSecond()>1)
        {
            rMap.expire(Instant.now().plusSeconds(getSecond()));
        }
    }


    @Override
    public long size() {
        if (!useCache||redisson==null||cacheKey==null)
        {
            return 0;
        }
        return redisson.getMap(cacheKey).size();
    }

    /**
     * 得到
     *
     * @param key key
     * @return 返回数据
     */
    @Override
    public CacheEntry get(String key) {
        if (!useCache || StringUtil.isNull(cacheKey))
        {
            return null;
        }

        RMap<String, CacheEntry> rMap = null;
        try {
            rMap = redisson.getMap(cacheKey);
            if (rMap==null)
            {
                return null;
            }
            CacheEntry cacheEntry = rMap.get(key);
            if (cacheEntry == null) {
                return null;
            }
            if (System.currentTimeMillis() > cacheEntry.getExpirationTime())
            {
                rMap.delete();
                return null;
            }
            if (cacheEntry.isKeep())
            {
                cacheEntry.setAccessTime(System.currentTimeMillis());
                rMap.replace(key,cacheEntry);
            }
            return cacheEntry;
        } catch (Exception e)
        {
            if (rMap!=null)
            {
                rMap.remove(key);
            } else
            {
                redisson.getMap(cacheKey).clear();
            }
            return null;
        }
    }

    /**
     * 将被逐出的对象
     *
     * @return 得到第一个对象
     */
    @Override
    public CacheEntry getEvictedCacheEntry() {
        return null;
    }

    /**
     * 删除
     *
     * @param key key
     * @return 删除的数据
     */
    @Override
    public CacheEntry remove(String key) {
        RMap<String, CacheEntry> rMap = redisson.getMap(cacheKey);
        if (!useCache||rMap==null)
        {
            return null;
        }
        Object obj = rMap.get(key);
        if (obj==null)
        {
            return null;
        }
        return rMap.remove(key);
    }

    /**
     * 删除所有
     */
    @Override
    public void removeAll() {
        if (!useCache||redisson==null||redisson.isShutdown()) {
            return;
        }
        RMap<String, CacheEntry> rMap = redisson.getMap(cacheKey);
        if (rMap!=null)
        {
            rMap.delete();
        }
    }

    @Override
    public Collection<CacheEntry> getAll() {
        if (!useCache||redisson==null||redisson.isShutdown()) {
            return new ArrayList<>(0);
        }
        RMap<String, CacheEntry> rMap = redisson.getMap(cacheKey);
        if (rMap!=null)
        {
           return rMap.values();

        }
        return new ArrayList<>(0);
    }
    /**
     *
     * @return redis默认一直保存
     * @throws CacheException 异常
     */
    @Override
    public long getSizeInBytes() throws CacheException {
        return 0;
    }

    @Override
    public Set<String> getKeys() {
        if (!useCache)
        {
            return new HashSet<>(0);
        }
        RMap<String, CacheEntry> rMap = redisson.getMap(cacheKey);
        if (rMap==null)
        {
            return new HashSet<>(0);
        }
        return rMap.keySet();
    }

    @Override
    public boolean containsKey(String key) {
        RMap<String, CacheEntry> rMap = redisson.getMap(cacheKey);
        return rMap.containsKey(key);
    }

    @Override
    public void setName(String name) {
        cacheKey = StringUtil.replace(name, StringUtil.DOT, ":");
        super.setName(name);
    }

    @Override
    public void dispose() {
       if (redisson!=null&&!redisson.isShutdown()) {
            redisson.shutdown();
        }
    }
}
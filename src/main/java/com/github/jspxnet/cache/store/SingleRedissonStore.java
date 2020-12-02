package com.github.jspxnet.cache.store;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.cache.CacheException;
import com.github.jspxnet.cache.IStore;
import com.github.jspxnet.cache.container.CacheEntry;
import com.github.jspxnet.cache.redis.RedissonClientConfig;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Init;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 但缓存下是用,分布式多缓存下是用 RedisStore
 */
//@Bean(bind = SingleRedissonStore.class)
@Slf4j
public class SingleRedissonStore extends Store implements IStore {
    private static RedissonClient redisson = null;


    public SingleRedissonStore() {

    }

    @Init
    public void init() {
        if (redisson != null) {
            return;
        }
        redisson =  (RedissonClient)EnvFactory.getBeanFactory().getBean(RedissonClientConfig.class);
    }


    @Override
    public boolean isUseTimer() {
        return false;
    }

    private String CACHE_KEY = StringUtil.empty;

    @Override
    public void put(CacheEntry entry) {
        if (entry.getKey()==null)
        {
            return;
        }
        RMap<String,CacheEntry> rMap = redisson.getMap(CACHE_KEY);
        rMap.put(entry.getKey(), entry);
        if (getSecond()>1)
        {
            rMap.expire(getSecond(), TimeUnit.SECONDS);
        }
    }


    @Override
    public long size() {
        return redisson.getMap(CACHE_KEY).size();
    }

    /**
     * 得到
     *
     * @param key key
     * @return 返回数据
     */
    @Override
    public CacheEntry get(String key) {
        RMap<String, CacheEntry> rMap = redisson.getMap(CACHE_KEY);
        if (rMap==null)
        {
            return null;
        }
        try {
            CacheEntry cacheEntry = rMap.get(key);
            if (cacheEntry == null) {
                return null;
            }
            if (System.currentTimeMillis() > cacheEntry.getExpirationTime())
            {
                rMap.delete();
                return null;
            }
            if (cacheEntry.isAccessKeep())
            {
                cacheEntry.setLastAccessTime();
                rMap.replace(key,cacheEntry);
            }
            return cacheEntry;
        } catch (Exception e)
        {
            rMap.remove(key);
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
        RMap<String, CacheEntry> rMap = redisson.getMap(CACHE_KEY);
        if (rMap==null)
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
        if (redisson!=null&&redisson.isShutdown()) {
            return;
        }
        RMap<String, CacheEntry> rMap = redisson.getMap(CACHE_KEY);
        if (rMap!=null)
        {
            rMap.delete();
        }
    }

  /*  private RedisClient redisClient = null;

    private org.redisson.client.RedisClient getRedisClient() {
        if (redisClient == null) {
            RedisClientConfig redisConfig = new RedisClientConfig();
            SingleServerConfig singleServerConfig = config.useSingleServer();
            redisConfig.setAddress(singleServerConfig.getAddress());
            if (!StringUtil.isEmpty(singleServerConfig.getPassword())) {
                redisConfig.setPassword(singleServerConfig.getPassword());
            }
            redisConfig.setDatabase(singleServerConfig.getDatabase())
                    .setClientName(singleServerConfig.getClientName())
                    .setGroup(config.getEventLoopGroup());

            redisClient = RedisClient.create(redisConfig);
        }
        return redisClient;
    }
*/
    @Override
    public long getSizeInBytes() throws CacheException {
       /*
        try {
            RedisConnection conn = getRedisClient().connect();
            Map<String, String> memoryInfo = conn.sync(StringCodec.INSTANCE, RedisCommands.INFO_MEMORY);
            conn.closeAsync();
            return memoryInfo.size();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return 0;
    }

    @Override
    public Set<String> getKeys() {
        RMap<String, CacheEntry> rMap = redisson.getMap(CACHE_KEY);
        if (rMap==null)
        {
            return new HashSet<>(0);
        }
        return rMap.keySet();
    }

    @Override
    public boolean containsKey(String key) {
        RMap<String, CacheEntry> rMap = redisson.getMap(CACHE_KEY);
        return rMap.containsKey(key);
    }

    @Override
    public void setName(String name) {
        CACHE_KEY = StringUtil.replace(name, ".", ":");
        super.setName(name);
    }

    @Override
    public void dispose() {
/*
        if (!redisson.isShutdown()) {
            redisson.shutdown();
        }
*/
    }
}
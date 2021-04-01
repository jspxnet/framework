package com.github.jspxnet.boot;

import com.github.jspxnet.cache.redis.RedissonClientConfig;
import org.apache.catalina.LifecycleException;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.tomcat.RedissonSessionManager;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/3/2 1:25
 * description: jspbox
 **/
public class TomcatRedissonSessionManager extends RedissonSessionManager {
    @Override
    public RedissonClient buildClient() throws LifecycleException {
        String config = getConfigPath();
        try {
            Config redisConfig = RedissonClientConfig.getRedisConfig(config);
            assert redisConfig != null;
            return Redisson.create(redisConfig);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

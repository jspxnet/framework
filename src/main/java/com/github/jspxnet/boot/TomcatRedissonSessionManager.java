package com.github.jspxnet.boot;

import com.github.jspxnet.cache.redis.RedissonClientConfig;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;
import org.apache.catalina.LifecycleException;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.tomcat.RedissonSessionManager;

import java.io.File;
import java.net.URL;

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
            return Redisson.create(redisConfig);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

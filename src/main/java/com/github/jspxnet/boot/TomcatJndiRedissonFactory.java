package com.github.jspxnet.boot;

import com.github.jspxnet.cache.redis.RedissonClientConfig;
import org.redisson.JndiRedissonFactory;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/3/2 20:23
 * description: 配置适配器
 **/
public class TomcatJndiRedissonFactory extends JndiRedissonFactory {
    @Override
    protected RedissonClient buildClient(String config)
    {
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

package com.github.jspxnet.boot;

import com.github.jspxnet.cache.redis.RedissonClientConfig;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;
import org.redisson.JndiRedissonFactory;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import javax.naming.NamingException;
import java.io.File;
import java.net.URL;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/3/2 20:23
 * description: 配置适配器
 **/
public class TomcatJndiRedissonFactory extends JndiRedissonFactory {
    @Override
    protected RedissonClient buildClient(String config) throws NamingException
    {
        try {
            Config redisConfig = RedissonClientConfig.getRedisConfig(config);
            return Redisson.create(redisConfig);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

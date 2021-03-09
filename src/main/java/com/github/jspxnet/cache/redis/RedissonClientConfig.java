package com.github.jspxnet.cache.redis;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.io.IoUtil;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Destroy;
import com.github.jspxnet.sioc.annotation.Init;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import java.io.File;
import java.io.IOException;
/**
 * threads（线程池数量）
 *
 * 默认值: 当前处理核数量 * 2
 *
 * 这个线程池数量被所有RTopic对象监听器，RRemoteService调用者和RExecutorService任务共同共享。
 * nettyThreads （Netty线程池数量）
 *
 * 默认值: 当前处理核数量 * 2
 */
@Bean(singleton = true)
@Slf4j
public class RedissonClientConfig {
    private static RedissonClient redisson = null;
    private String config = StringUtil.empty;
    private Config redisConfig = null;

    public RedissonClientConfig()
    {

    }

    public static Config getRedisConfig(String config) throws IOException {
        if (StringUtil.isNull(config) && StringUtil.isNull(config)) {
            log.error("not config Redis cache link, 没有正确配置Redis 链接");
            return null;
        }
        config = StringUtil.trim(config);
        Config redisConfig;
        boolean json = StringUtil.isNull(config);
        File file = null;
        if (!json&&(config.toLowerCase().endsWith(".json")||config.toLowerCase().endsWith(".yml")))
        {
            file = EnvFactory.getFile(config);
        }
        if (file!=null&&config.toLowerCase().endsWith(".json"))
        {
            redisConfig = Config.fromJSON(IoUtil.autoReadText(file));
        } else
        if (file!=null&&config.toLowerCase().endsWith(".yml"))
        {
            redisConfig = Config.fromYAML(IoUtil.autoReadText(file));
        }
        else
        if (StringUtil.isJsonObject(config)) {
            redisConfig = Config.fromJSON(config);
        } else {
            redisConfig = Config.fromYAML(config);
        }
        return redisConfig;

    }


    @Init
     public void init() {
        if (!EnvFactory.getEnvironmentTemplate().getBoolean(Environment.useCache))
        {
            return;
        }
        if (redisson == null) {
            if (StringUtil.isNull(config) && StringUtil.isNull(config)) {
                log.error("not config Redis cache link, 没有正确配置Redis 链接");
                return;
            }
            try {
                redisConfig = getRedisConfig( config);
                redisson = Redisson.create(redisConfig);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Destroy
    public void close() {
        if (redisson!=null && !redisson.isShutdown())
        {
            redisson.shutdown();
        }
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public Config getConfig() {
        return redisConfig;
    }

    public RedissonClient getRedissonClient() {
        return redisson;
    }
}

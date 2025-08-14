package com.github.jspxnet.cache.redis;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.io.IoUtil;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Destroy;
import com.github.jspxnet.sioc.annotation.Init;
import com.github.jspxnet.utils.StringUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ConfigSupport;
import java.io.File;
import java.io.Serializable;

/**
 * threads（线程池数量）
 * 默认值: 当前处理核数量 * 2
 * 这个线程池数量被所有RTopic对象监听器，RRemoteService调用者和RExecutorService任务共同共享。
 * nettyThreads （Netty线程池数量）
 * 默认值: 当前处理核数量 * 2
 */
@Slf4j
@Bean(singleton = true)
public class RedissonClientConfig implements Serializable {
    private static RedissonClient redisson = null;
    @Setter
    private String config = StringUtil.empty;
    private Config redisConfig = null;

    public RedissonClientConfig()
    {

    }

    public static Config getRedisConfig(String config) throws Exception {
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
            String jsonStr = IoUtil.autoReadText(file);
            ConfigSupport support = new ConfigSupport();
            redisConfig = support.fromJSON(jsonStr, Config.class);;
        } else
        if (file!=null&&config.toLowerCase().endsWith(".yml"))
        {
            redisConfig = Config.fromYAML(IoUtil.autoReadText(file));
        }
        else
        if (StringUtil.isJsonObject(config)) {
            ConfigSupport support = new ConfigSupport();
            redisConfig = support.fromJSON(config, Config.class);;
        } else {
            redisConfig = Config.fromYAML(config);
        }
        //System.setProperty("java.net.preferIPv4Stack", "true");
        //System.setProperty("java.net.preferIPv6Addresses", "false");
/*
        redisConfig.useSingleServer().setKeepAlive(true) // 启用 TCP Keep-Alive
                .setPingConnectionInterval(10000) // 每 10 秒发送心跳
                .setDnsMonitoringInterval(-1);          // 禁用 DNS 监测
        redisConfig.setAddressResolverGroupFactory(new RoundRobinDnsAddressResolverGroupFactory());
*/
        return redisConfig;

    }


    @Init
     public void init() {
        if (redisson == null) {
            if (StringUtil.isNull(config)) {
                if (EnvFactory.getEnvironmentTemplate().getBoolean(Environment.useCache))
                {
                    log.error("not config Redis cache link, 没有正确配置Redis 链接");
                }
                return;
            }
        }

        if (redisConfig==null)
        {
            try {
                redisConfig = getRedisConfig( config);
                assert redisConfig != null;
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

    public Config getConfig() {
        return redisConfig;
    }

    public RedissonClient getRedissonClient() {
        return redisson;
    }
}

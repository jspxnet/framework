package com.github.jspxnet.cache.redis;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Destroy;
import com.github.jspxnet.sioc.annotation.Init;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

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
    private String configFile = StringUtil.empty;
    private Config config = null;
    private String content = StringUtil.empty;

    public RedissonClientConfig()
    {

    }

    @Init
     public void init() {
        if (redisson == null) {
            if (StringUtil.isNull(content) && StringUtil.isNull(configFile)) {
                log.error("not config Redis cache link, 没有正确配置Redis 链接");
                return;
            }
            try {

                File file = new File(configFile);
                if (!file.exists()) {
                    file = EnvFactory.getFile(configFile);
                }

                if (file.isFile() && file.canRead()) {
                    String fileType = FileUtil.getTypePart(file);
                    if ("json".equalsIgnoreCase(fileType) || "conf".equalsIgnoreCase(fileType)) {
                        if (configFile.startsWith("http")) {
                            config = Config.fromJSON(new URL(configFile));
                        } else {
                            config = Config.fromJSON(file);
                        }
                    } else if ("yaml".equalsIgnoreCase(fileType)||"yml".equalsIgnoreCase(fileType)) {
                        if (configFile.startsWith("http")) {
                            config = Config.fromYAML(new URL(configFile));
                        } else {
                            config = Config.fromYAML(file);
                        }
                    } else {
                        config = Config.fromJSON(file);
                    }
                } else {
                    if (StringUtil.isJsonObject(content)) {
                        config = Config.fromJSON(content);
                    } else {
                        config = Config.fromYAML(content);
                    }
                }

                redisson = Redisson.create(config);

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

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public RedissonClient getRedissonClient() {
        return redisson;
    }
}

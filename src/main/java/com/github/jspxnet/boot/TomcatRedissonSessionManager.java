package com.github.jspxnet.boot;

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
        Config redisConfig = null;
        String config = getConfigPath();
        try {
            File file = new File(config);
            if (!StringUtil.isJsonObject(config)&&file.isFile() && file.canRead()) {
                String fileType = FileUtil.getTypePart(file);
                if ("json".equalsIgnoreCase(fileType) || "conf".equalsIgnoreCase(fileType)) {
                    if (config.startsWith("http")) {
                        redisConfig = Config.fromJSON(new URL(config));
                    } else {
                        redisConfig = Config.fromJSON(file);
                    }
                } else if ("yaml".equalsIgnoreCase(fileType)||"yml".equalsIgnoreCase(fileType)) {
                    if (config.startsWith("http")) {
                        redisConfig = Config.fromYAML(new URL(config));
                    } else {
                        redisConfig = Config.fromYAML(file);
                    }
                } else {
                    redisConfig = Config.fromJSON(file);
                }
            } else {
                if (StringUtil.isJsonObject(config)) {
                    redisConfig = Config.fromJSON(config);
                } else {
                    redisConfig = Config.fromYAML(config);
                }
            }

            return Redisson.create(redisConfig);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

package com.github.jspxnet.sober.queue;

import com.github.jspxnet.boot.JspxNetApplication;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sioc.annotation.Scheduled;
import lombok.extern.slf4j.Slf4j;


/**
 * 这里是分布式存储链表的启动程序
 */
@Slf4j
@Bean
public class StoreQueueServerMain {
    @Ref
    private RedisStoreQueueServer redisStoreQueueServer;

    /**
     * @param args 控制台命令方式启动接口
     */
    public static void main(String[] args) {
        JspxNetApplication.autoRun();
    }

    /**
     * @return jspx.net AOP容器中自动启动
     */
    @Scheduled
    public String run() {
        if (redisStoreQueueServer == null) {
            return Environment.ERROR;
        }
        redisStoreQueueServer.run();
        return Environment.SUCCESS;
    }

}

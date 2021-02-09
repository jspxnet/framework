package com.github.jspxnet.sober.queue;

import com.github.jspxnet.boot.JspxNetApplication;
import com.github.jspxnet.sioc.annotation.Ref;
import lombok.extern.slf4j.Slf4j;


/**
 * 这里是分布式存储链表的启动程序
 */
@Slf4j
public class StoreQueueServerMain {
    @Ref
    private RedisStoreQueueServer redisStoreQueueServer;

    /**
     * @param args 控制台命令方式启动接口
     */
    public static void main(String[] args) {
        JspxNetApplication.autoRun();
    }
}

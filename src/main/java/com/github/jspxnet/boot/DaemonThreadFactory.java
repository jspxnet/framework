package com.github.jspxnet.boot;

import java.util.concurrent.ThreadFactory;

/**
 * Created by jspx.net
 *
 * @author: chenYuan
 * @date: 2021/1/23 17:15
 * @description: jspbox
 **/
public class DaemonThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable runnable) {

        Thread thread = new Thread(runnable);
        //设置守护线程
        thread.setDaemon(true);
        return thread;
    }
}
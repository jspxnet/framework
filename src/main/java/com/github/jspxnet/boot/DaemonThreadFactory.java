package com.github.jspxnet.boot;

import java.util.concurrent.ThreadFactory;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/1/23 17:15
 * description: 守护线程
 **/
public class DaemonThreadFactory implements ThreadFactory {
    /**
     *
     * @param runnable 执行类
     * @return 守护线程
     */
    @Override
    public Thread newThread(Runnable runnable) {

        Thread thread = new Thread(runnable);
        //设置守护线程
        thread.setDaemon(true);
        return thread;
    }
}
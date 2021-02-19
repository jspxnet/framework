package com.github.jspxnet.boot;

import com.github.jspxnet.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/1/23 17:15
 * description: 守护线程
 **/
public class DaemonThreadFactory implements ThreadFactory {
    static final private List<Thread> THREAD_LIST = new ArrayList<>();
    private String name;
    public DaemonThreadFactory(String name)
    {
        this.name = name;
    }

    private  DaemonThreadFactory()
    {

    }

    /**
     *
     * @param runnable 执行类
     * @return 守护线程
     */
    @Override
    public Thread newThread(Runnable runnable) {

        Thread thread = new Thread(runnable,name + "_" + runnable.hashCode());
        //设置守护线程
        thread.setDaemon(true);
        THREAD_LIST.add(thread);
        return thread;
    }

    public static void shutdown()
    {
        for (Thread thread:THREAD_LIST)
        {
            if (thread.isAlive()&&!thread.isInterrupted())
            {
                thread.interrupt();
                try {
                    thread.join(DateUtil.SECOND);
                } catch (InterruptedException e) {
                    thread.stop(e);
                }

            }
        }
    }
}
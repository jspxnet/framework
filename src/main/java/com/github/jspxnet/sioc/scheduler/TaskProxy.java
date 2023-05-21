package com.github.jspxnet.sioc.scheduler;


import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.mac.NetworkInfo;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.SchedulerManager;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.SystemUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;


@Data
@Slf4j
public class TaskProxy implements Runnable {
    static public final int  SYS_TYPE = 1;

    static public final int  DEFAULT_TYPE = 0;
    //0 普通  1:系统级别
    private int taskType = 0;

    //起个名字,如果为空 就是 methodName
    private String name;

    //时间表达式
    private String pattern;

    //是否只执行异常
    private int once = 0;

    //延时多少秒执行
    private int delayed = 0;

    //调用方法名称
    private String methodName;

    private Object bean;

    //运行次数
    private int runTimes = 0;


    @Override
    public String toString()
    {
        JSONObject json = new JSONObject();
        try {
            json.put("mac", NetworkInfo.getMacAddress());
        } catch (IOException e) {
            //...
            //e.printStackTrace();
        }
        json.put("taskType",taskType);
        json.put("methodName",methodName);
        if (bean!=null)
        {
            json.put("bean",bean.getClass().getName());
        }
        return json.toString();
    }

    public String getScheduledId()
    {
        return EncryptUtil.getMd5(toString());
    }

    final private ReentrantLock lock = new ReentrantLock();

    public void forceRun() throws Exception {
        BeanUtil.invoke(bean, methodName);
    }


    @Override
    public void run() {
        try {
            if (delayed > 0) {
                Thread.sleep(delayed);
            }

            if (methodName!=null && !lock.isLocked())
            {
                lock.lock();
                try {
                    runTimes++;
                    BeanUtil.invoke(bean, methodName);

                    //关闭一次性任务 begin
                    if (once== YesNoEnumType.YES.getValue()&&runTimes>=1)
                    {
                        SchedulerManager schedulerManager = SchedulerTaskManager.getInstance();
                        try {
                            log.info("关闭一次性任务:" + bean + "method." + methodName);
                            schedulerManager.stopRemove(getScheduledId());
                        } catch (IllegalStateException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    //关闭一次性任务 end
                } finally {
                    lock.unlock();
                }
            }
        } catch (Exception e) {
            log.error(bean + " method " + methodName, e);
            e.printStackTrace();
        }
    }
}

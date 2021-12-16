package com.github.jspxnet.sioc.scheduler;


import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.SchedulerManager;
import com.github.jspxnet.utils.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
public class TaskProxy implements Runnable {
    private String pattern;

    private boolean once = false;
    //延时多少秒执行
    private int delayed = 0;

    private String methodName;

    private Object bean;

    private int runTimes = 0;

    @Override
    public String toString()
    {
        JSONObject json = new JSONObject();
        json.put("pattern",pattern);
        json.put("once",once);
        json.put("delayed",delayed);
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

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public boolean isOnce() {
        return once;
    }

    public void setOnce(boolean once) {
        this.once = once;
    }

    public int getDelayed() {
        return delayed;
    }

    public void setDelayed(int delayed) {
        this.delayed = delayed;
    }

    final private ReentrantLock lock = new ReentrantLock();

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
                    if (once&&runTimes==1)
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
            log.error(bean + "method" + methodName, e);
            e.printStackTrace();
        }
    }
}

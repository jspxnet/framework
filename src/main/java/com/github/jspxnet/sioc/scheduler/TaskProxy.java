package com.github.jspxnet.sioc.scheduler;



import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.SchedulerManager;
import com.github.jspxnet.utils.BeanUtil;
import it.sauronsoftware.cron4j.Scheduler;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class TaskProxy implements Runnable {
    private String pattern;

    private boolean once = false;
    //延时多少秒执行
    private int delayed = 0;

    private String methodName;

    private Object bean;

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

    @Override
    public void run() {
        try {
            if (delayed > 0) {
                Thread.sleep(delayed);
            }
            BeanUtil.invoke(bean, methodName);
        } catch (Exception e) {
            log.error(bean + "method" + methodName, e);
            e.printStackTrace();
        }
        if (once) {
            SchedulerManager schedulerManager = SchedulerTaskManager.getInstance();
            String scheduledId = getScheduledId();
            Scheduler scheduler = schedulerManager.remove(scheduledId);
            if (scheduler != null) {
                log.info("关闭一次性任务:" + bean + "method." + methodName);
                scheduler.stop();
            }
        }
    }
}

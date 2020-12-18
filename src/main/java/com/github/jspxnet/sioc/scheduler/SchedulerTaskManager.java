package com.github.jspxnet.sioc.scheduler;


import com.github.jspxnet.sioc.SchedulerManager;
import com.github.jspxnet.sioc.annotation.Scheduled;
import com.github.jspxnet.sioc.util.AnnotationUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.SchedulingPattern;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 如果想每分钟执行一次，那么表达式就是这样
 * // 如果想每分钟执行一次，那么表达式就是这样
 **/
@Slf4j
public class SchedulerTaskManager implements SchedulerManager {

    final private static Map<String, Scheduler> SCHEDULER_MAP = new ConcurrentHashMap<>();
    private static SchedulerTaskManager instance = new SchedulerTaskManager();

    private SchedulerTaskManager() {
    }

    public static SchedulerTaskManager getInstance() {
        return instance;
    }

    @Override
    public boolean add(String id, String pattern, Runnable runnable) {
        if (SCHEDULER_MAP.containsKey(id)) {
            //已经有这个任务了，不重复
            return false;
        }
        if (StringUtil.isEmpty(pattern)) {
            pattern = "* * * * *";
        }
        Scheduler scheduler = new Scheduler();
        scheduler.schedule(pattern, runnable);
        scheduler.start();
        SCHEDULER_MAP.put(id, scheduler);
        return true;
    }

    /**
     * @param bean bean对象
     * @return 解析 Scheduled 标签放入计划任务
     */
    @Override
    public int add(Object bean) {
        if (bean == null) {
            return -1;
        }
        Method[] methods = ClassUtil.getDeclaredMethods(bean.getClass());
        if (methods == null) {
            return -1;
        }

        for (Method method : methods) {
            Scheduled scheduled = method.getAnnotation(Scheduled.class);
            if (scheduled == null) {
                continue;
            }
            TaskProxy taskProxy = new TaskProxy();
            taskProxy.setBean(bean);
            taskProxy.setMethodName(method.getName());
            taskProxy.setPattern(scheduled.cron());
            taskProxy.setOnce(scheduled.once());
            taskProxy.setDelayed(scheduled.delayed());
            add(taskProxy);
        }
        return SCHEDULER_MAP.size();
    }

    /**
     * 提供外部灵活的添加方式
     *
     * @param taskProxy 任务代理执行器
     * @return 添加是否成功
     */
    @Override
    public boolean add(TaskProxy taskProxy) {
        if (taskProxy.getBean() == null) {
            return false;
        }
        if (StringUtil.isEmpty(taskProxy.getMethodName()) || StringUtil.isEmpty(taskProxy.getPattern())) {
            return false;
        }
        String scheduledId = AnnotationUtil.getScheduledId(taskProxy);
        if (StringUtil.isEmpty(scheduledId)) {
            return false;
        }
        if (SCHEDULER_MAP.containsKey(scheduledId)) {
            //已经有这个任务了，不重复
            return false;
        }
        if (!SchedulingPattern.validate(taskProxy.getPattern())) {
            log.error("Scheduled cron is cron4j,定时器表达式错误，查看cron4j表达式," + taskProxy.getBean().getClass());
            return false;
        }

        Scheduler scheduler = new Scheduler();
        scheduler.schedule(taskProxy.getPattern(), taskProxy);
        scheduler.start();
        SCHEDULER_MAP.put(scheduledId, scheduler);
        return true;
    }

    /**
     * @return 得到当前所有的任务数量
     */
    @Override
    public int size() {
        return SCHEDULER_MAP.size();
    }

    /**
     * @return 得到当前所有的任务ID
     */
    @Override
    public Set<String> keySet() {
        return SCHEDULER_MAP.keySet();
    }

    /**
     * @param id 任务id
     * @return 得到任务管理器，提供给外部操作
     */
    @Override
    public Scheduler get(String id) {
        return SCHEDULER_MAP.get(id);
    }


    /**
     * @param id 任务id
     * @return 删除定时任务
     */
    @Override
    public Scheduler remove(String id) {
        return SCHEDULER_MAP.remove(id);
    }

    /**
     * 关闭所有
     */
    @Override
    public void shutdown() {
        for (Scheduler scheduler : SCHEDULER_MAP.values()) {
            if (scheduler==null)
            {
                continue;
            }
            if (scheduler.isStarted())
            {
                scheduler.stop();
            }
        }
    }

}

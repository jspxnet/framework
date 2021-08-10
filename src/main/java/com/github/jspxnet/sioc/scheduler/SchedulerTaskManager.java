package com.github.jspxnet.sioc.scheduler;


import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.cron4j.Scheduler;
import com.github.jspxnet.cron4j.SchedulingPattern;
import com.github.jspxnet.sioc.SchedulerManager;
import com.github.jspxnet.sioc.annotation.Scheduled;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Method;
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
    private static final SchedulerTaskManager INSTANCE = new SchedulerTaskManager();

    private SchedulerTaskManager() {
    }

    public static SchedulerTaskManager getInstance() {
        return INSTANCE;
    }

    /**
     *
     * @param id 任务id
     * @param pattern 时间表达式
     * @param runnable 执行类
     * @return 添加是否成功
     */
    @Override
    public boolean add(String id, String pattern, Runnable runnable) {
        if (SCHEDULER_MAP.containsKey(id)) {
            //已经有这个任务了，不重复
            return false;
        }
        if (StringUtil.isEmpty(pattern)) {
            pattern = "* * * * * *";
        }
        Scheduler scheduler = new Scheduler();
        //声明线程后调用setDeamon(true)，将该线程设置为守护线程，则容器关闭后，这些守护线程会立即关闭
        scheduler.setDaemon(true);
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
        Map<String, Object> valueMap = EnvFactory.getEnvironmentTemplate().getVariableMap();
        for (Method method : methods) {
            Scheduled scheduled = method.getAnnotation(Scheduled.class);
            if (scheduled == null) {
                continue;
            }
            TaskProxy taskProxy = new TaskProxy();
            taskProxy.setBean(bean);
            taskProxy.setMethodName(method.getName());
            //scheduled.cron() 变量替换
            String cron = scheduled.cron();
            if (scheduled.cron().contains("${"))
            {
                cron = EnvFactory.getPlaceholder().processTemplate(valueMap,cron);
                if (StringUtil.isEmpty(cron))
                {
                    cron = "* * * * *";
                }
            }
            taskProxy.setPattern(cron);
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
        String scheduledId = taskProxy.getScheduledId();
        if (SCHEDULER_MAP.containsKey(scheduledId)) {
            //已经有这个任务了，不重复
            return false;
        }
        if (!SchedulingPattern.validate(taskProxy.getPattern())) {
            log.error("Scheduled cron is cron4j,定时器表达式错误，查看cron4j表达式," + taskProxy.getBean().getClass());
            return false;
        }

        Scheduler scheduler = new Scheduler();
        scheduler.setDaemon(true);

        String cron = taskProxy.getPattern();
        if (cron.contains("${"))
        {
            Map<String, Object> valueMap = EnvFactory.getEnvironmentTemplate().getVariableMap();
            cron = EnvFactory.getPlaceholder().processTemplate(valueMap,cron);
            if (StringUtil.isEmpty(cron))
            {
                cron = "* * * * * *";
            }
        }
        scheduler.schedule(cron, taskProxy);
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

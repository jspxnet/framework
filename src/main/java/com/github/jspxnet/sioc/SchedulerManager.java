package com.github.jspxnet.sioc;

import com.github.jspxnet.cron4j.Scheduler;
import com.github.jspxnet.sioc.scheduler.TaskProxy;
import java.util.Set;

public interface SchedulerManager {
    /**
     * @param bean bean对象
     * @return 解析 Scheduled 标签放入计划任务
     */
    int add(Object bean);
    /**
     *
     * @param id 任务id
     * @param pattern 时间表达式
     * @param runnable 执行类
     * @return 添加是否成功
     */
    boolean add(String id, String pattern, Runnable runnable);
    /**
     * 提供外部灵活的添加方式
     *
     * @param taskProxy 任务代理执行器
     * @return 添加是否成功
     */
    boolean add(TaskProxy taskProxy);
    /**
     * @return 得到当前所有的任务数量
     */
    int size();
    /**
     * @return 得到当前所有的任务ID
     */
    Set<String> keySet();
    /**
     * @param id 任务id
     * @return 得到任务管理器，提供给外部操作
     */
    Scheduler get(String id);
    /**
     * @param id 任务id
     * @return 删除定时任务
     */
    Scheduler stopRemove(String id);

    /**
     * 关闭所有
     */
    void shutdown();
}

package com.github.jspxnet.sioc;

import com.github.jspxnet.cron4j.Scheduler;
import com.github.jspxnet.sioc.scheduler.TaskProxy;
import com.github.jspxnet.txweb.model.dto.SchedulerDto;

import java.util.List;
import java.util.Set;

/**
 * 定时任务管理器
 */
public interface SchedulerManager {

    /**
     *
     * @param id  任务id
     * @param name 任务名称
     * @param pattern  时间表达式
     * @param taskType  任务类型
     * @param runnable  执行类
     * @return  添加是否成功
     */
    boolean add(String id, String name, String pattern, int taskType, Runnable runnable);

    /**
     * @param bean bean对象
     * @return 解析 Scheduled 标签放入计划任务
     */
    int add(Object bean);

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
     *
     * @param find 查询
     * @param page 页面
     * @param count 行数
     * @return 返回列表
     */
    List<SchedulerDto> getList(String find, int page, int count);

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

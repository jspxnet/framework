package com.github.jspxnet.sioc.scheduler;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.cron4j.Scheduler;
import com.github.jspxnet.cron4j.SchedulingPattern;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.sioc.SchedulerManager;
import com.github.jspxnet.sioc.annotation.Scheduled;
import com.github.jspxnet.txweb.model.dto.SchedulerDto;
import com.github.jspxnet.txweb.turnpage.TurnPageButton;
import com.github.jspxnet.txweb.turnpage.impl.TurnPageButtonImpl;
<<<<<<< HEAD
import com.github.jspxnet.utils.BooleanUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.XMLUtil;
=======
import com.github.jspxnet.utils.*;
>>>>>>> dev
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Method;
import java.util.*;
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
     * @param id  任务id
     * @param name 任务名称
     * @param pattern  时间表达式
     * @param taskType  任务类型
     * @param runnable  执行类
     * @return  添加是否成功
     */
    @Override
    public boolean add(String id, String name, String pattern,int taskType, Runnable runnable) {
        if (SCHEDULER_MAP.containsKey(id)) {
            //已经有这个任务了，不重复
            return false;
        }
        if (StringUtil.isEmpty(pattern)) {
            pattern = "* * * * * *";
        }

        TaskProxy taskProxy = new TaskProxy();
        taskProxy.setTaskType(taskType);
        taskProxy.setName(name);
        taskProxy.setOnce(YesNoEnumType.NO.getValue());
        taskProxy.setBean(runnable);
        taskProxy.setPattern(pattern);
        taskProxy.setMethodName("run");
        return add(taskProxy);

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
            taskProxy.setName(scheduled.name());
            if (StringUtil.isNull(taskProxy.getName())) {
                taskProxy.setName(method.getName());
            }
            //scheduled.cron() 变量替换
            String cron = scheduled.cron();
<<<<<<< HEAD
            if (scheduled.cron().contains("${")) {
=======
            if (scheduled.cron()!=null&&scheduled.cron().contains("${")) {
                String[] varNameList = StringUtil.getFreeMarkerVar(scheduled.cron());
                for (String varName:varNameList)
                {
                    if (!valueMap.containsKey(varName))
                    {
                        valueMap.put(varName,"0 */1 * * * *");
                    }
                }
>>>>>>> dev
                cron = EnvFactory.getPlaceholder().processTemplate(valueMap, cron);
                if (StringUtil.isEmpty(cron)) {
                    cron = "0 */1 * * * *";
                }
            }
            if (StringUtil.isEmpty(cron)) {
                cron = "0 */1 * * * *";
            }

            taskProxy.setPattern(cron);
            taskProxy.setOnce(BooleanUtil.toInt(scheduled.once()));
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
        String cron = taskProxy.getPattern();
        if (cron.contains("${")) {
            cron = XMLUtil.deleteQuote(cron);
            Map<String, Object> valueMap = EnvFactory.getEnvironmentTemplate().getVariableMap();
            cron = EnvFactory.getPlaceholder().processTemplate(valueMap, cron);
            if (StringUtil.isEmpty(cron)) {
                cron = "0 */1 * * * *";
            }
            taskProxy.setPattern(cron);
<<<<<<< HEAD
        }
        log.debug("定时任务加入:id={},{}", taskProxy.getScheduledId(), taskProxy);
        if (!SchedulingPattern.validate(taskProxy.getPattern())) {
            log.error("Scheduled cron is cron4j,定时器表达式错误:{}，类对象:{}",taskProxy.getPattern(),taskProxy.getBean().getClass());
            return false;
        }
=======
        }
        log.debug("定时任务加入:id={},{}", taskProxy.getScheduledId(), taskProxy);
        if (!SchedulingPattern.validate(taskProxy.getPattern())) {
            log.error("Scheduled cron is cron4j,定时器表达式错误:{}，类对象:{}",taskProxy.getPattern(),taskProxy.getBean().getClass());
            return false;
        }
>>>>>>> dev
        Scheduler scheduler = new Scheduler(taskProxy);
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
     *
     * @param find 查询
     * @param page 页面
     * @param count 行数
     * @return 返回列表
     */
    @Override
    public List<SchedulerDto> getList(String find, int page, int count) {

        TurnPageButton turnPageButton = new TurnPageButtonImpl();
        turnPageButton.setCurrentPage(page);
        turnPageButton.setCount(count);
        List<SchedulerDto> result = new ArrayList<>();
        Collection<Scheduler> collation = SCHEDULER_MAP.values();
        turnPageButton.setTotalCount(collation.size());
<<<<<<< HEAD
        int firstRow = (int) turnPageButton.getFristRow();
=======
        int firstRow = (int) turnPageButton.getFirstRow();
>>>>>>> dev
        int i = -1;
        for (Scheduler scheduler : collation) {
            i++;
            if (scheduler == null) {
                continue;
            }
            if (i>=firstRow) {
                SchedulerDto dto = scheduler.getTaskConf();
                if (StringUtil.isNull(find) || dto.getMethodName().contains(find)||dto.getName().contains(find)
                        ||dto.getClassName().contains(find))
                {
                    result.add(scheduler.getTaskConf());
                }
            }
            if (result.size() >= count) {
                break;
            }
        }
        return result;
    }

    /**
     * @param id 任务id
     * @return 删除定时任务
     */
    @Override
    public Scheduler stopRemove(String id) {
        Scheduler scheduler = SCHEDULER_MAP.get(id);
        if (scheduler != null && scheduler.isStarted()) {
            scheduler.stop();
        }
        return SCHEDULER_MAP.remove(id);
    }

    /**
     * 关闭所有
     */
    @Override
    public void shutdown() {
        for (Scheduler scheduler : SCHEDULER_MAP.values()) {
            if (scheduler == null) {
                continue;
            }
            if (scheduler.isStarted()) {
                scheduler.stop();
            }
        }
    }

}

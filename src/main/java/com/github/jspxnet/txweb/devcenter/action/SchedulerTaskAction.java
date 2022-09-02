package com.github.jspxnet.txweb.devcenter.action;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.cron4j.Scheduler;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sioc.scheduler.SchedulerTaskManager;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.devcenter.scheduler.SchedulerRegisterTask;
import com.github.jspxnet.txweb.devcenter.view.SchedulerTaskView;
import com.github.jspxnet.txweb.model.dto.SchedulerDto;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.table.SchedulerControl;
import com.github.jspxnet.utils.BeanUtil;

/**
 *
 * useSchedulerRegister=true
 * schedulerRegisterUrl=http://127.0.0.1:8080/devcenter/task/register.jwc
 * schedulerRegisterApi=http://127.0.0.1:8080/devcenter/task/list/register
 * schedulerRegisterToken=3294u23uosudf98398432432
 */
@HttpMethod(caption = "本地定时任务", actionName = "*", namespace = Environment.DEV_CENTER+"/tasklocal")
@Bean(namespace = Environment.DEV_CENTER, singleton = true)
public class SchedulerTaskAction extends SchedulerTaskView {

    @Ref
    private SchedulerRegisterTask schedulerRegisterTask;

    @Operate(caption = "清理刷新", method = "refresh")
    public RocResponse<Integer> refresh()
    {
        genericDAO.createCriteria(SchedulerControl.class).delete(false);
        genericDAO.evict(SchedulerControl.class);
        schedulerRegisterTask.run();
        return RocResponse.success();
    }

    @Operate(caption = "强制运行一次", method = "forcerun")
    public RocResponse<Integer> forceRun(@Param(caption = "guid",required = true) String[] guids)
    {
        SchedulerTaskManager schedulerTaskManager = SchedulerTaskManager.getInstance();
        for (String guid:guids)
        {
            Scheduler scheduler = schedulerTaskManager.get(guid);
            if (scheduler==null)
            {
                return RocResponse.success(0,"任务不在此服务");
            }
            try {
                scheduler.forceRun();
            } catch (Exception e) {
                e.printStackTrace();
                return RocResponse.error(ErrorEnumType.CALL_API.getValue(),e.getMessage());
            }
        }
        return RocResponse.success(1,"执行成功");
    }

    @Operate(caption = "启动任务", method = "start")
    public RocResponse<Integer> start(@Param(caption = "guid",required = true) String[] guids)
    {
        SchedulerTaskManager schedulerTaskManager = SchedulerTaskManager.getInstance();
        for (String guid:guids)
        {
            Scheduler scheduler = schedulerTaskManager.get(guid);
            if (scheduler==null)
            {
                return RocResponse.success(0,"任务不在此服务");
            }
            try {
                scheduler.start();
            } catch (Exception e) {
                e.printStackTrace();
                return RocResponse.error(ErrorEnumType.CALL_API.getValue(),e.getMessage());
            }
        }
        schedulerRegisterTask.run();
        return RocResponse.success(1,"执行成功");
    }

    @Operate(caption = "停止任务", method = "stop")
    public RocResponse<Integer> stop(@Param(caption = "guid",required = true) String[] guids)
    {
        SchedulerTaskManager schedulerTaskManager = SchedulerTaskManager.getInstance();
        for (String guid:guids)
        {
            Scheduler scheduler = schedulerTaskManager.get(guid);
            if (scheduler==null)
            {
                return RocResponse.success(0,"任务不在此服务");
            }
            try {
                scheduler.stop();

            } catch (Exception e) {
                e.printStackTrace();
                return RocResponse.error(ErrorEnumType.CALL_API.getValue(),e.getMessage());
            }
        }
        schedulerRegisterTask.run();
        return RocResponse.success(1,"执行成功");
    }


    @Operate(caption = "更新执行周期", method = "updatepattern")
    public RocResponse<Integer> updatePattern(@Param(caption = "guid",required = true) String guid,@Param(caption = "pattern",required = true) String pattern)
    {
        SchedulerTaskManager schedulerTaskManager = SchedulerTaskManager.getInstance();
        Scheduler scheduler = schedulerTaskManager.get(guid);
        if (scheduler==null)
        {
            return RocResponse.success(0,"任务不在此服务");
        }
        try {
            //原状态
            //scheduler.isStarted();
            scheduler.reschedule(guid,pattern);
            //scheduler.stop();
            genericDAO.delete(SchedulerControl.class,guid);
            SchedulerDto dto = scheduler.getTaskConf();
            SchedulerControl schedulerControl =  BeanUtil.copy(dto,SchedulerControl.class);
            genericDAO.save(schedulerControl);
            //scheduler.start();
            schedulerRegisterTask.run();
            return RocResponse.success(1,"执行成功");
        } catch (Exception e) {
            e.printStackTrace();
            return RocResponse.error(ErrorEnumType.CALL_API.getValue(),e.getMessage());
        }
    }

}

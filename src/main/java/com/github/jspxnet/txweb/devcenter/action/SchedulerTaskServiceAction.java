package com.github.jspxnet.txweb.devcenter.action;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.devcenter.view.SchedulerTaskServiceView;
import com.github.jspxnet.txweb.model.dto.SchedulerRegisterDto;
import com.github.jspxnet.txweb.model.param.SchedulerRegisterParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * useSchedulerRegister=true
 * schedulerRegisterUrl=http://127.0.0.1:8080/devcenter/task/register.jwc
 * schedulerRegisterApi=http://127.0.0.1:8080/devcenter/tasklocal
 * schedulerRegisterName=scheduler-local
 * schedulerRegisterToken=3294u23uosudf98398432432
 */
@HttpMethod(caption = "定时任务服务", actionName = "*", namespace = Environment.DEV_CENTER+"/taskserv")
@Bean(namespace = Environment.DEV_CENTER, singleton = true)
@Slf4j
public class SchedulerTaskServiceAction extends SchedulerTaskServiceView {


    @Operate(caption = "注册外部服务", method = "register")
    public RocResponse<Integer> register(@Param(caption = "注册参数",required = true) SchedulerRegisterParam param)
    {
        SchedulerRegisterDto dto = BeanUtil.copy(param, SchedulerRegisterDto.class);
        schedulerTaskService.register(dto);
        log.info("注册定时调用服务:{}", ObjectUtil.toString(dto));
        return RocResponse.success();
    }

    //本地的服务
    @Operate(caption = "清理刷新", method = "refresh")
    public RocResponse<Integer> refreshAll()
    {
        try {
            schedulerTaskService.refreshAll();
        } catch (Exception e) {
            e.printStackTrace();
            return RocResponse.error(ErrorEnumType.CALL_API.getValue(),e.getMessage());
        }
        return RocResponse.success();
    }

    @Operate(caption = "运行任务", method = "forcerun")
    public RocResponse<Integer> forceRun(@Param(caption = "服务id",required = true) String id,
                                         @Param(caption = "guids",required = true) String[] guids)
    {
        try {
            schedulerTaskService.forceRun(id,guids);
        } catch (Exception e) {
            e.printStackTrace();
            return RocResponse.error(ErrorEnumType.CALL_API.getValue(),e.getMessage());
        }
        return RocResponse.success();
    }


    @Operate(caption = "启动任务", method = "start")
    public RocResponse<Integer> start(@Param(caption = "服务id",required = true) String id,
                                      @Param(caption = "guids",required = true) String[] guids)
    {
        try {
            schedulerTaskService.start(id,guids);
        } catch (Exception e) {
            e.printStackTrace();
            return RocResponse.error(ErrorEnumType.CALL_API.getValue(),e.getMessage());
        }
        return RocResponse.success();
    }

    @Operate(caption = "停止任务", method = "stop")
    public RocResponse<Integer> stop(@Param(caption = "服务id",required = true) String id,
                                     @Param(caption = "guids",required = true) String[] guids)
    {
        try {
            schedulerTaskService.stop(id,guids);
        } catch (Exception e) {
            e.printStackTrace();
            return RocResponse.error(ErrorEnumType.CALL_API.getValue(),e.getMessage());
        }
        return RocResponse.success();
    }

    @Operate(caption = "更新执行周期", method = "updatepattern")
    public RocResponse<Integer> updatePattern(@Param(caption = "服务id",required = true) String id,
                                              @Param(caption = "guid",required = true) String guid,
                                              @Param(caption = "pattern",required = true) String pattern)
    {
        try {
            schedulerTaskService.updatePattern(id,guid,pattern);
        } catch (Exception e) {
            e.printStackTrace();
            return RocResponse.error(ErrorEnumType.CALL_API.getValue(),e.getMessage());
        }
        return RocResponse.success();
    }

}

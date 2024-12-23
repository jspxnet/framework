package com.github.jspxnet.txweb.devcenter.view;

import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.devcenter.service.SchedulerTaskService;
import com.github.jspxnet.txweb.model.dto.SchedulerDto;
import com.github.jspxnet.txweb.model.dto.SchedulerRegisterDto;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SchedulerTaskServiceView extends ActionSupport {
    @Ref
    protected SchedulerTaskService schedulerTaskService;

    @Operate(caption = "任务管理器", method = "index", post = false)
    public String index()  {
        return "任务管理器";
    }

    @Operate(caption = "得到注册的列表", method = "list/register")
    public RocResponse<Collection<SchedulerRegisterDto>> getListRegister()
    {
        return RocResponse.success(schedulerTaskService.getRegisterList());
    }

    @Operate(caption = "服务列表", method = "list/page")
    public RocResponse<List<SchedulerDto>> getListPage(@Param(caption = "翻页参数",required = true) String id,
                                                       @Param(caption = "查询条件") String find,
                                                       @Param(caption = "当前页数",max = 5000,value = "1") Integer currentPage,
                                                       @Param(caption = "行数",min = 1,max = 5000,value = "12") Integer count) {
        //list/register
        try {
           return schedulerTaskService.getSchedulerList(id,find,currentPage,count);
        } catch (Exception e) {
            return RocResponse.success(new ArrayList<>(0), e.getMessage());
        }
    }


    @Operate(caption = "任务列表", method = "list")
    public RocResponse<List<SchedulerDto>> list(    @Param(caption = "查询条件") String find,
                                                       @Param(caption = "当前页数",max = 5000,value = "1") Integer currentPage,
                                                       @Param(caption = "行数",min = 1,max = 5000,value = "12") Integer count) {
        //list/register
        try {
           return RocResponse.success(schedulerTaskService.getTaskList(find,currentPage,count));
        } catch (Exception e) {
            return RocResponse.success(new ArrayList<>(0), e.getMessage());
        }
    }
}

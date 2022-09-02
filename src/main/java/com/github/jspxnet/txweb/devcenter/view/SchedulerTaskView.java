package com.github.jspxnet.txweb.devcenter.view;

import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sioc.scheduler.SchedulerTaskManager;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.txweb.model.dto.SchedulerDto;
import com.github.jspxnet.txweb.model.param.SimplePageParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import java.util.ArrayList;
import java.util.List;

public class SchedulerTaskView extends ActionSupport {
    @Ref
    protected GenericDAO genericDAO;

    @Operate(caption = "任务管理器", method = "index", post = false)
    public String index()  {
        return "任务管理器";
    }

    @Operate(caption = "得到注册的列表", method = "list/register")
    public RocResponse<List<SchedulerDto>> getListRegister(@Param(caption = "翻页参数") SimplePageParam pageParam)
    {
        SchedulerTaskManager schedulerTaskManager = SchedulerTaskManager.getInstance();
        return RocResponse.success(schedulerTaskManager.getList(pageParam.getFind(),pageParam.getCurrentPage(),pageParam.getCount()));
    }

    @Operate(caption = "任务列表", method = "list/page")
    public RocResponse<List<SchedulerDto>> getListPage(@Param(caption = "翻页参数") SimplePageParam pageParam) {
        SchedulerTaskManager schedulerTaskManager = SchedulerTaskManager.getInstance();
        long totalCount = schedulerTaskManager.size();
        if (totalCount <= 0) {
            return RocResponse.success(new ArrayList<>(0), language.getLang(LanguageRes.notDataFind));
        }
        return RocResponse.success(schedulerTaskManager.getList(pageParam.getFind(),pageParam.getCurrentPage(), pageParam.getCount()));
    }
}

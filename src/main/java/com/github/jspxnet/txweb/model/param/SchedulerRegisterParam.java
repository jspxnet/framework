package com.github.jspxnet.txweb.model.param;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;

@Data
public class SchedulerRegisterParam implements Serializable {

    @Param(caption = "是否开启注册")
    private boolean useSchedulerRegister = true;

    @Param(caption = "服务名称",max = 200,required = true)
    private String schedulerRegisterName = StringUtil.empty;

    @Param(caption = "任务服务注册接口",max = 200,required = true)
    private String schedulerRegisterUrl = StringUtil.empty;

    @Param(caption = "自己的调用接口位置",max = 200 ,required = true)
    private String schedulerRegisterApi = StringUtil.empty;

    @Param(caption = "安全验证",max = 200 )
    private String schedulerRegisterToken = StringUtil.empty;

}
package com.github.jspxnet.txweb.model.dto;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.json.JsonField;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.utils.BooleanUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 #######################################################
 #  定时任务控制台
 #######################################################

 useSchedulerRegister = true
 schedulerRegisterUrl = http://127.0.0.1:8080/devcenter/taskserv/register.jwc
 schedulerRegisterApi =
 schedulerRegisterName = scheduler-local
 schedulerCheckCron = 0 0/7 * * * *
 schedulerRegisterToken = 3294u23uosudf98398432432

 */

@Data
public class SchedulerRegisterDto implements Serializable {
    private String id = null;

    private boolean useSchedulerRegister = true;

    private String schedulerRegisterName = StringUtil.empty;

    private String schedulerRegisterUrl = StringUtil.empty;

    private String schedulerRegisterApi = StringUtil.empty;

    private String schedulerRegisterToken = StringUtil.empty;

    @Override
    public boolean equals(Object obj)
    {
        if (obj==null || getId()==null)
        {
            return false;
        }
       return getId().hashCode()==obj.hashCode();
    }

    @JsonField(name = "id")
    public String getId()
    {
        if (id==null)
        {
            id = EncryptUtil.getMd5(toString());
        }
        return id;
    }

    @Override
    public int hashCode()
    {
        return getId().hashCode();
    }

    @Override
    public String toString()
    {

        Map<String,String> map = new HashMap<>();
        map.put(Environment.USE_SCHEDULER_REGISTER, BooleanUtil.toString(useSchedulerRegister));
        map.put(Environment.SCHEDULER_REGISTER_URL,schedulerRegisterUrl);
        map.put(Environment.SCHEDULER_REGISTER_API,schedulerRegisterApi);
        map.put(Environment.SCHEDULER_REGISTER_TOKEN,schedulerRegisterToken);
        return ObjectUtil.toString(map);
    }

}

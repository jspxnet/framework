package com.github.jspxnet.txweb.dispatcher.handle;

import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.config.ActionConfig;
import com.github.jspxnet.txweb.proxy.DefaultActionInvocation;
import com.github.jspxnet.txweb.util.TXWebUtil;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 这个接口是为了做快捷应用，让浏览器发送命令快速执行，不去访问模版文件
 * 如果设置了返回none就不返回，默认 返回使用json返回
 */
@Slf4j
public class CommandHandle extends ActionHandle {
    final public static String NAME = "cmd";

    @Override
    public void doing(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //////////////////////////////////环境参数 begin
        Map<String, Object> envParams = createEnvironment(request, response);
        ActionConfig actionConfig = getActionConfig(envParams);
        if (actionConfig == null) {
            TXWebUtil.errorPrint("not found,找不到文件",null, response, HttpStatusType.HTTP_status_404);
            log.debug("actionConfig  is NULL:{}",envParams.toString());
            throw new Exception("actionConfig  is NULL :" + envParams.toString());
        }

        //不需要 synchronized
        ActionInvocation actionInvocation = new DefaultActionInvocation(actionConfig, envParams, NAME, null, request, response);
        try {
            actionInvocation.initAction();
            actionInvocation.invoke();
        } finally {
            actionInvocation.executeResult(null);
        }
        ////////////////////action end
    }

}

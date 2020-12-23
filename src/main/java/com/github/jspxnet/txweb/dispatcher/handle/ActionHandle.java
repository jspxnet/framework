package com.github.jspxnet.txweb.dispatcher.handle;

import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.ActionProxy;
import com.github.jspxnet.txweb.config.ActionConfig;
import com.github.jspxnet.txweb.dispatcher.WebHandle;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.proxy.DefaultActionInvocation;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.util.ParamUtil;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 * author ChenYuan
 * date 2017/6/17
 * 默认的模版执行
 */
@Slf4j
public class ActionHandle extends WebHandle {
    final public static String NAME = "action";


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
        if (actionConfig.isCache())
        {
            //缓存中有数据就直接执行返回
            String key = request.getRequestURI();
            log.debug("get page cache url:{}",key );
            String out = (String)JSCacheManager.get(actionConfig.getClassName(),key);
            if (!StringUtil.isEmpty(out))
            {
                TXWebUtil.print(out, WebOutEnumType.HTML.getValue(),response);
                return;
            }
        }

        //不需要 synchronized
        ActionInvocation actionInvocation = new DefaultActionInvocation(actionConfig, envParams, ActionHandle.NAME, null, request, response);
        actionInvocation.initAction();
        actionInvocation.invoke();
        actionInvocation.executeResult(null);
        ////////////////////action end
    }


    static public void execute(ActionProxy actionProxy) throws Exception {
        ActionSupport action = actionProxy.getAction();
        Method exeMethod = actionProxy.getMethod();
        Object result;
        if (exeMethod.getParameterCount() == 0) {
            result = TXWebUtil.invokeFun(action, exeMethod, null);
        } else {
            //jdk 8 需要添加编译参数 javac -parameters
            Object[] params = ParamUtil.getMethodParameter(action, exeMethod);
            result = TXWebUtil.invokeFun(action, exeMethod, params);
        }
        //默认模版方式调用
        if (!Void.TYPE.equals(exeMethod.getGenericReturnType())) {
            action.setResult(result);
        }
    }
}

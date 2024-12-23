package com.github.jspxnet.txweb.dispatcher.handle;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.ActionProxy;
import com.github.jspxnet.txweb.config.ActionConfig;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.txweb.proxy.DefaultActionInvocation;
import com.github.jspxnet.txweb.service.client.HessianSkeleton;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.ClassUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Objects;

/**
 * Hessian 远程接口直接调用，会运行拦截器，和权限相关，但返回的是接口对象
 */
@Slf4j
public class HessianHandle extends ActionHandle {
    final public static String NAME = "hessian";

    final public static String HTTP_HEARD_NAME = "hessian";

    final public static String POST = "POST";

    @Override
    public void doing(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //安全判断 ,关闭远程端口，所有的都不能使用
        if (!POST.equals(request.getMethod())) {
            TXWebUtil.errorPrint("Service",null, response, 531);
            return;
        }

        //////////////////////////////////环境参数 begin
        ActionConfig actionConfig = getActionConfig(request);
        if (actionConfig == null) {
            TXWebUtil.errorPrint("not found,找不到文件", null,response, HttpStatusType.HTTP_status_404);
            throw new Exception("actionConfig  is NULL :" + request.getRequestURI());
        }

        Map<String, Object> envParams = createRocEnvironment(actionConfig,request, response);
        //synchronized 这里不能有同步,否则调用不成功
        ActionInvocation actionInvocation = null;
        try {
            actionInvocation = new DefaultActionInvocation(actionConfig, envParams, NAME, null, request, response);
            actionInvocation.initAction();
            actionInvocation.invoke();
        } finally {
            if (actionInvocation!=null)
            {
                actionInvocation.executeResult(null);
            }
        }
        ////////////////////action end

    }

    static public void execute(ActionProxy actionProxy) {

        Action action = actionProxy.getAction();
        Class<?> actionClass = ClassUtil.getClass(action.getClass());
        //Hessian远程接口方式调用 begin
        String serviceId = actionClass.getName();

        String objectId = action.getString("id");
        if (objectId == null) {
            objectId = action.getString("ejbid");
        }
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        HttpServletResponse response = actionContext.getResponse();
        //替代原版HessianSkeleton  实现事务标签功能
        HessianSkeleton objectSkeleton = new HessianSkeleton(action, Objects.requireNonNull(ClassUtil.findRemoteApi(actionClass)));
        boolean debug = EnvFactory.getEnvironmentTemplate().getBoolean(Environment.DEBUG);
        objectSkeleton.setDebug(debug);
        try {
            com.caucho.services.server.ServiceContext.begin(request, response, serviceId, objectId);
            response.setContentType("x-application/hessian");
            InputStream is = request.getInputStream();
            OutputStream os = response.getOutputStream();
            objectSkeleton.invoke(is, os);
        } catch (Exception e) {
            log.error("serviceId:{},objectId:{},error:{}", objectId, objectId, e.getMessage());
        } finally {
            com.caucho.services.server.ServiceContext.end();
            action.destroy();
        }
        //Hessian远程接口方式调用 end

    }

}

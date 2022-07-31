package com.github.jspxnet.txweb.devcenter.interceptor;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.ActionProxy;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.txweb.dao.PermissionDAO;
import com.github.jspxnet.txweb.interceptor.InterceptorSupport;
import com.github.jspxnet.txweb.online.OnlineManager;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.UserSession;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class DevCenterInterceptor extends InterceptorSupport {

    private final static EnvironmentTemplate ENV_TEMPLATE = EnvFactory.getEnvironmentTemplate();


    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    /**
     * 载入在线管理
     */
    @Ref
    private OnlineManager onlineManager;

    @Ref
    private PermissionDAO permissionDAO;

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        //这里是不需要验证的action
        ActionContext actionContext = ThreadContextHolder.getContext();
        ActionProxy actionProxy = actionInvocation.getActionProxy();
        Action action = actionProxy.getAction();
        UserSession userSession = onlineManager.getUserSession(actionContext);

        String userListStr = ENV_TEMPLATE.getString(Environment.KEY_DCV_ENTER_USER_LIST);
        if (StringUtil.isNull(userListStr))
        {
            action.addFieldInfo(Environment.warningInfo, "配置文件中先配置dev_user_list,用户名授权后才可进入");
            return ActionSupport.UNTITLED;
        }

        String[] userList = StringUtil.split(userListStr,StringUtil.SEMICOLON);
        if (ObjectUtil.isEmpty(userList))
        {
            action.addFieldInfo(Environment.warningInfo, "配置文件中先配置dev_user_list,用户名授权后才可进入");
            return ActionSupport.UNTITLED;
        }

        if (!ArrayUtil.inArray(userList,userSession.getName(),true)) {
            action.addFieldInfo(Environment.warningInfo, "没有权限不允许进入");
            return ActionSupport.UNTITLED;
        }
        return actionInvocation.invoke();
        //也可以 return Action.ERROR; 终止action的运行
    }
}
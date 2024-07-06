package com.github.jspxnet.txweb.devcenter.interceptor;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.cache.DefaultCache;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.io.IoUtil;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.ActionProxy;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.txweb.dao.PermissionDAO;
import com.github.jspxnet.txweb.interceptor.BasePermissionInterceptor;
import com.github.jspxnet.txweb.online.OnlineManager;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.UserSession;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.io.File;


@Bean(singleton = true,namespace =  "devcenter")
@Slf4j
public class DevCenterInterceptor extends BasePermissionInterceptor {
    @Override
    public void init() {

        //换成中读取begin
        String txt = (String) JSCacheManager.get(DefaultCache.class,GUEST_STOP_URL_TXT);
        decodeGuestUrl(txt);
        txt = (String) JSCacheManager.get(DefaultCache.class,ADMIN_RULE_URL_TXT);
        decodeAdminUrl(txt);
        //换成中读取end

        if (!ArrayUtil.isEmpty(guestStopUrl) || !ArrayUtil.isEmpty(ruleOutUrl)) {
            return;
        }
        File file = null;
        try {
            if (guestUrlFile != null) {
                file = EnvFactory.getFile(guestUrlFile);
            }
            log.info("载入guestUrlFile:{}", file);

            if (file != null) {
                txt = IoUtil.autoReadText(file);
                JSCacheManager.put(DefaultCache.class,GUEST_STOP_URL_TXT,txt);
                decodeGuestUrl(txt);
            } else
            {
                log.error(guestUrlFile + "没有找到");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        String requestUri = actionContext.getRequest().getRequestURI();
        if (requestUri==null) {
            requestUri = StringUtil.empty;
        }
        if (ENV_TEMPLATE.getBoolean(Environment.USE_SCHEDULER_REGISTER) && (requestUri.startsWith("/devcenter/task/")  || requestUri.startsWith("/devcenter/tasklocal") || requestUri.startsWith("/devcenter/taskserv"))) {
            String schedulerRegisterToken = ENV_TEMPLATE.getString(Environment.SCHEDULER_REGISTER_TOKEN);
            String token = action.getString(Environment.SCHEDULER_REGISTER_TOKEN, true);
            if (token.equals(schedulerRegisterToken)) {
                return actionInvocation.invoke();
            }
        }

        String pathNamespace = actionContext.getNamespace();
        if (StringUtil.isNull(pathNamespace)) {
            pathNamespace = actionContext.getNamespace();
        }
        String checkUrl = StringUtil.replace(StringUtil.BACKSLASH + pathNamespace + StringUtil.BACKSLASH + actionContext.getActionName(), "//", StringUtil.BACKSLASH);

        //登陆入口，直接放行
        if (useGuestUrl) {
            boolean isRule = isRuleOutUrl(checkUrl);
            if (isRule) {
                log.debug("ruleOutUrl checkUrl={},isRule={}", checkUrl, isRule);
                return actionInvocation.invoke();
            }
        }

        UserSession userSession = onlineManager.getUserSession(actionContext);
        String userListStr = ENV_TEMPLATE.getString(Environment.KEY_DCV_ENTER_USER_LIST);
        if (StringUtil.isNull(userListStr)) {
            action.addFieldInfo(Environment.warningInfo, "配置文件中先配置dev_center_user_list,用户名授权后才可进入");
            return ActionSupport.UNTITLED;
        }

        String[] userList = StringUtil.split(userListStr, StringUtil.SEMICOLON);
        if (ObjectUtil.isEmpty(userList)) {
            action.addFieldInfo(Environment.warningInfo, "配置文件中先配置dev_center_user_list,用户名授权后才可进入");
            return ActionSupport.UNTITLED;
        }

        if (!ArrayUtil.inArray(userList, userSession.getName(), true)) {
            action.addFieldInfo(Environment.warningInfo, "没有权限不允许进入");
            log.debug("没有权限不允许进入 requestUri:{}",requestUri);
            return ActionSupport.UNTITLED;
        }
        return actionInvocation.invoke();
        //也可以 return Action.ERROR; 终止action的运行
    }
}
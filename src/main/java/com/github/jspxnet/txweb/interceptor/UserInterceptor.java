/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.interceptor;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.enums.UserEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.ActionProxy;
import com.github.jspxnet.txweb.IRole;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.txweb.dao.PermissionDAO;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.online.OnlineManager;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.Role;
import com.github.jspxnet.txweb.table.UserSession;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * date: 12-7-28
 * Time: 下午11:54
 * 权限拦截 ,规则为,放入用户的信息和角色信息,不做拦截
 * 是PermissionInterceptor  的简化版本
 */
@Slf4j
public class UserInterceptor extends InterceptorSupport {
    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Setter
    private boolean permission = true;

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

        String organizeId = action.getString("organizeId", true);
        if (!StringUtil.isEmpty(organizeId)) {
            permissionDAO.setOrganizeId(organizeId);
        }

        IRole role = userSession.getRole(permissionDAO.getNamespace(), permissionDAO.getOrganizeId());
        //自动分配调试权限 begin
        if (role==null&&!permission && (Environment.SYSTEM_ID == userSession.getUid() || userSession.getUid() == 1)) {
            //调试模式
            Role debugRole = createDebugRole();
            debugRole.setNamespace(permissionDAO.getNamespace());
            debugRole.setOrganizeId(permissionDAO.getOrganizeId());
            debugRole.setIp(userSession.getIp());
            userSession.setRole(debugRole);
            userSession.setLastRequestTime(System.currentTimeMillis());
            role = debugRole;
            onlineManager.updateUserSessionCache(userSession);
        }

        if (action.isGuest() && role == null) {
            userSession.setRole(permissionDAO.getRole(config.getString(Environment.guestRole)));
            onlineManager.updateUserSessionCache(userSession);
        } else if (role == null) {
            userSession.setRole(permissionDAO.getComposeRole(userSession.getUid(),organizeId));
            //二次修复
            role = userSession.getRole(permissionDAO.getNamespace(), permissionDAO.getOrganizeId());
            if (role == null) {
                Role regRole = permissionDAO.getRole(config.getString(Environment.registerRole));
                userSession.setRole(regRole);
            }
            onlineManager.updateUserSessionCache(userSession);
        }
        //没有角色权限自动载入 end

        role = userSession.getRole(permissionDAO.getNamespace(), permissionDAO.getOrganizeId());
        if (role == null) {
            if (RequestUtil.isRocRequest(action.getRequest())) {
                TXWebUtil.print(new JSONObject(RocResponse.error(ErrorEnumType.CONFIG.getValue(), "需要配置角色,初始化系统")),
                        WebOutEnumType.JSON.getValue(), action.getResponse(), HttpStatusType.HTTP_status_500);
            } else {
                action.addFieldInfo(Environment.warningInfo, permissionDAO.getNamespace() + " need config role,需要配置角色,初始化系统");
            }
            log.debug("角色没有初始化配置 namespace={},role={}", permissionDAO.getNamespace(), new JSONObject(role));
            return ActionSupport.UNTITLED;
        }

        String method = actionContext.getMethod().getName();
        String pathNamespace = action.getEnv(ActionEnv.Key_Namespace);
        String actionName = actionContext.getActionName();

        if (permission) {
            if (role.getUserType() < UserEnumType.INTENDANT.getValue()) {
                //配置的权限,判断是否可执行
                if (!role.checkOperate(pathNamespace, actionName, method)) {
                    //会员进入后，正常模式，完全通过后台权限判断是否能够操作
                    action.addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needPermission) + ", role name :" + role.getName() + " for " + role.getNamespace());
                    return ActionSupport.UNTITLED;
                }
            }

            //角色权限表判断
            if (role.getUserType() >= UserEnumType.INTENDANT.getValue() && StringUtil.hasLength(method) && role.getUserType() < UserEnumType.ChenYuan.getValue()
                    && !role.checkOperate(pathNamespace, actionName, method)) {
                action.addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needPermission));
                return ActionSupport.UNTITLED;
            }
        }
        return actionInvocation.invoke();
        //也可以 return Action.ERROR; 终止action的运行
    }
}
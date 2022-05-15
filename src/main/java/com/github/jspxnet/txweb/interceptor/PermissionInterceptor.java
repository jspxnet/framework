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


import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.enums.UserEnumType;
import com.github.jspxnet.io.IoUtil;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.network.rpc.model.transfer.RequestTo;
import com.github.jspxnet.sioc.annotation.Bean;
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
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-11-11
 * Time: 13:50:12
 * 权限拦截 ,规则为,在admin目录下的action必须大于等于操作人员,并且放入用户的信息和角色信息,适用cms 类型软件
 */
@Slf4j
@Bean(bind = PermissionInterceptor.class)
public class PermissionInterceptor extends InterceptorSupport {


    private String guestUrlFile = "guesturl.properties";
    private static String[] guestStopUrl = null;
    private static String[] ruleOutUrl = null;

    private String adminUrlFile = "adminurl.properties";
    private static String[] adminRuleUrl = null;
    private static String[] adminRuleOutUrl = null;

    public PermissionInterceptor() {

    }

    private boolean permission = true;

    public void setPermission(boolean permission) {
        this.permission = permission;
    }

    private boolean autoOrganizeId = true;

    public void setAutoOrganizeId(boolean autoOrganizeId) {
        this.autoOrganizeId = autoOrganizeId;
    }

    private boolean useGuestUrl = true;

    public void setUseGuestUrl(boolean useGuestUrl) {
        this.useGuestUrl = useGuestUrl;
    }

    /**
     * 载入在线管理
     */
    @Ref
    private OnlineManager onlineManager;

    @Ref
    private PermissionDAO permissionDAO;

    public String getGuestUrlFile() {
        return guestUrlFile;
    }

    public void setGuestUrlFile(String guestUrlFile) {
        this.guestUrlFile = guestUrlFile;
    }


    @Override
    public void destroy() {

    }

    @Override
    public void init() {
        if (!ArrayUtil.isEmpty(guestStopUrl)) {
            return;
        }
        File file = null;
        try {
            if (guestUrlFile != null && !guestUrlFile.startsWith("http")) {
                file = EnvFactory.getFile(guestUrlFile);
                if (file == null) {
                    log.error(guestUrlFile + "没有找到");
                }
            }
            log.info("载入guestUrlFile:{}", guestUrlFile);

            if (file != null) {
                String txt = IoUtil.autoReadText(file);
                String[] array = StringUtil.split(StringUtil.replace(txt, StringUtil.CRLF, StringUtil.CR), StringUtil.CR);
                for (String str : array) {
                    if (str == null) {
                        continue;
                    }
                    if (str.startsWith("!")) {
                        guestStopUrl = ArrayUtil.add(guestStopUrl, StringUtil.substringAfter(str, "!"));
                    } else {
                        ruleOutUrl = ArrayUtil.add(ruleOutUrl, str);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //---------------------------

        if (!ArrayUtil.isEmpty(adminRuleUrl)) {
            return;
        }

        try {
            if (adminUrlFile != null && !adminUrlFile.startsWith("http")) {
                file = EnvFactory.getFile(adminUrlFile);
                if (file == null) {
                    log.error(guestUrlFile + "没有找到");
                }
            }
            log.info("adminUrlFile:{}", guestUrlFile);

            if (file != null) {
                String txt = IoUtil.autoReadText(file);
                String[] array = StringUtil.split(StringUtil.replace(txt, StringUtil.CRLF, StringUtil.CR), StringUtil.CR);
                for (String str : array) {
                    if (str == null) {
                        continue;
                    }
                    if (str.startsWith("!")) {
                        adminRuleOutUrl = ArrayUtil.add(adminRuleOutUrl, StringUtil.substringAfter(str, "!"));
                    } else {
                        adminRuleUrl = ArrayUtil.add(adminRuleUrl, str);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        ActionContext actionContext = ThreadContextHolder.getContext();
        if (onlineManager == null) {
            actionContext.addFieldInfo(Environment.warningInfo, "onlineManager 为空,检查ioc配置是否正确");
            actionContext.setActionResult(ActionSupport.ERROR);
            log.error("onlineManager 为空,检查ioc配置是否正确");
            return ActionSupport.ERROR;
        }


        HttpServletRequest request = actionContext.getRequest();
        HttpServletResponse response = actionContext.getResponse();
        //这里是不需要验证的action
        ActionProxy actionProxy = actionInvocation.getActionProxy();
        Action action = actionProxy.getAction();

        String method = actionContext.getMethod().getName();
        String pathNamespace = actionContext.getNamespace();
        if (StringUtil.isNull(pathNamespace)) {
            pathNamespace = actionContext.getNamespace();
        }

        String checkUrl = StringUtil.replace(StringUtil.BACKSLASH + pathNamespace + StringUtil.BACKSLASH + actionContext.getActionName(), "//", StringUtil.BACKSLASH);

        String organizeId = null;
        if (autoOrganizeId) {
            organizeId = actionContext.getString(ActionEnv.KEY_organizeId);
        }
        //is admin url
        if (isAdminRuleUrl(checkUrl)) {
            organizeId = null;
        }
        permissionDAO.setOrganizeId(organizeId);

        UserSession userSession = onlineManager.getUserSession();
        //todo 待检查确认
        IRole role = userSession.getRole(permissionDAO.getNamespace(), organizeId);
        //自动分配调试权限 begin
        //role == null &&
        if (!permission && (Environment.SYSTEM_ID == userSession.getUid() || userSession.getUid() == 1)) {
            //调试模式
            Role debugRole = createDebugRole();
            debugRole.setNamespace(permissionDAO.getNamespace());
            debugRole.setOrganizeId(organizeId);
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
            userSession.setRole(permissionDAO.getComposeRole(userSession.getUid(), organizeId));
            //二次修复
            role = userSession.getRole(permissionDAO.getNamespace(), organizeId);
            if (role == null) {
                Role regRole = permissionDAO.getRole(config.getString(Environment.registerRole));
                userSession.setRole(regRole);
            }
            onlineManager.updateUserSessionCache(userSession);
        }
        //没有角色权限自动载入 end

        if (request instanceof RequestTo || INetCommand.RPC.equals(request.getAttribute(ActionEnv.Key_REMOTE_TYPE))) {
            //如果是RPC调用不拦截，RPC调用的安全使用通讯密钥方式来确保
            return actionInvocation.invoke();
        }

        //屏蔽的URL游客
        if (userSession.isGuest() && ArrayUtil.inArray(guestStopUrl, checkUrl, true)) {

            //如果都载入为空，那么载入游客权限 begin
            actionContext.addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.notAllowedOperation));

            //如果都载入为空，那么载入游客权限 end
            return ActionSupport.UNTITLED;
        }

        //登陆入口，直接放行
        if (useGuestUrl) {
            boolean isRule = isRuleOutUrl(checkUrl);
            if (isRule) {
                log.debug("ruleOutUrl checkUrl={},isRule={}", checkUrl, isRule);
                return actionInvocation.invoke();
            }
        }

        if (permission && userSession.getRole(permissionDAO.getNamespace(), organizeId) == null) {
            actionContext.addFieldInfo(Environment.warningInfo, permissionDAO.getNamespace() + " need config role,权限够不够");
            return ActionSupport.UNTITLED;
        }

        role = userSession.getRole(permissionDAO.getNamespace(), organizeId);
        if (role == null) {
            if (RequestUtil.isRocRequest(request)) {
                TXWebUtil.print(new JSONObject(RocResponse.error(ErrorEnumType.CONFIG.getValue(), "需要配置角色,初始化系统")),
                        WebOutEnumType.JSON.getValue(), response, HttpStatusType.HTTP_status_500);
            } else {
                actionContext.addFieldInfo(Environment.warningInfo, permissionDAO.getNamespace() + " need config role,需要配置角色,初始化系统");
            }
            log.debug("角色没有初始化配置 namespace={},role={}", permissionDAO.getNamespace(), new JSONObject(role));
            return ActionSupport.UNTITLED;
        }

        //访问控制,放开登录和管理目录
        //站点关闭 begin
        if (permission && !config.getBoolean(Environment.openSite)) {
            String closeInfo = config.getString(Environment.closeInfo);
            if (StringUtil.isNull(closeInfo)) {
                closeInfo = action.getRootNamespace() + "关闭状态，不允许访问";
            }
            actionContext.addFieldInfo(Environment.warningInfo, closeInfo);
            config.flush();
            return ActionSupport.UNTITLED;
        }
        //站点关闭 end

        //游客访问控制 begin
        if (!config.getBoolean(Environment.useGuestVisit) && role.getUserType() <= UserEnumType.NONE.getValue()) {
            //不输出信息就会到登录页面
            String closeInfo = config.getString(Environment.closeGuestVisitInfo);
            actionContext.addFieldInfo(Environment.warningInfo, closeInfo);
            return ActionSupport.UNTITLED;
        }
        //游客访问控制 end

        //时段限制 begin
        String accessForbiddenRange = config.get(Environment.accessForbiddenRange);
        if (!StringUtil.isNull(accessForbiddenRange) && DateUtil.isInTimeExpression(new Date(), accessForbiddenRange)) {
            String accessForbiddenTip = config.getString(Environment.accessForbiddenTip);
            if (StringUtil.isNull(accessForbiddenTip)) {
                accessForbiddenTip = accessForbiddenRange + "时间段内不能访问";
            }
            actionContext.addFieldInfo(Environment.warningInfo, accessForbiddenTip);
            return ActionSupport.UNTITLED;
        }
        //时段限制end

        if (permission) {
            if (role.getUserType() < UserEnumType.INTENDANT.getValue()) {
               /* if (role.getOfficeType() == YesNoEnumType.NO.getValue()) {
                    //会员进入后，判断如果是办公模式，非工作人员都要求登陆，成为工作人员才能访问
                    action.addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needOfficeWorkers));
                    return ActionSupport.LOGIN;
                }*/

                //配置的权限,判断是否可执行
                if (!role.checkOperate(pathNamespace, action.getClass().getName(), method)) {
                    //会员进入后，正常模式，完全通过后台权限判断是否能够操作
                    actionContext.addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needPermission) + ", role name :" + role.getName() + " for " + role.getNamespace());
                    return ActionSupport.UNTITLED;
                }
            }

            //角色权限表判断
            if (role.getUserType() >= UserEnumType.INTENDANT.getValue() && StringUtil.hasLength(method) && role.getUserType() < UserEnumType.ADMINISTRATOR.getValue()
                    && !role.checkOperate(pathNamespace, action.getClass().getName(), method)) {
                actionContext.addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needPermission));
                return ActionSupport.UNTITLED;
            }
        }
        //执行下一个动作,可能是下一个拦截器,也可能是action取决你的配置
        return actionInvocation.invoke();
        //也可以 return Action.ERROR; 终止action的运行
    }

    private static boolean isRuleOutUrl(String url) {
        if (url == null) {
            return true;
        }
        if (ObjectUtil.isEmpty(ruleOutUrl)) {
            return false;
        }
        for (String ruleUrl : ruleOutUrl) {
            if (ruleUrl.equals(url) || StringUtil.getPatternFind(url, ruleUrl)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAdminRuleUrl(String url) {
        if (url == null) {
            return true;
        }
        if (ObjectUtil.isEmpty(adminRuleUrl)) {
            return false;
        }
        for (String ruleUrl : adminRuleUrl) {
            if (ruleUrl.equals(url) || StringUtil.getPatternFind(url, ruleUrl)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        PermissionInterceptor permissionInterceptor = new PermissionInterceptor();
        permissionInterceptor.setPermission(true);
        permissionInterceptor.setGuestUrlFile("D:\\website\\webapps\\root\\WEB-INF\\classes\\guesturl.properties");
        permissionInterceptor.init();
        boolean b = PermissionInterceptor.isRuleOutUrl("/organize/save");
        System.out.println("--------------" + b);
    }
}
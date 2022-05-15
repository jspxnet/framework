/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.online;

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.UserSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2005-8-4
 * Time: 11:39:53
 * com.github.jspxnet.txweb.online.OnlineUserManager
 */
public interface OnlineManager extends Serializable {
    String getGuiPassword();

    String getDomain();

    void init() throws Exception;

    //Encrypt getEncrypt();
    boolean isOnline(long uid);

    boolean isOnline(String sessionId);

    UserSession createGuestUserSession();

    Map<String, String> getSafePrompt(long uid);

    void exit(String sessionId) throws Exception;

    void exit(long uid);

    UserSession getUserSession(String sessionId, String ip);

    UserSession getUserSession(String sessionId, String ip, long uid);

    UserSession getUserSession(Action action);

    UserSession getUserSession();

    UserSession getUserSession(ActionContext actionContext);

    void deleteUserSession(String sid, long uid) throws Exception;

    JSONObject login(HttpSession session, String loginId, String password, String client, String ip) throws Exception;

    Map<String, String> login(ActionSupport action, String isId, String loginId, String password, int cookieSecond) throws Exception;

    void exit(ActionSupport action);

    void setCookieTicket(HttpServletRequest request, HttpServletResponse response, String sid, int cookieSecond);

    void destroy();

    void updateUserSessionCache(UserSession userSession);
}
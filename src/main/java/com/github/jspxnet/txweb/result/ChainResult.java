/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.result;

import com.github.jspxnet.sober.exception.ValidException;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.WebConfigManager;
import com.github.jspxnet.txweb.config.ActionConfig;
import com.github.jspxnet.txweb.config.TxWebConfigManager;
import com.github.jspxnet.txweb.dispatcher.handle.ActionHandle;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.proxy.DefaultActionInvocation;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.URLUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-12-27
 * Time: 1:52:21
 * 结连跳转，保留环境变量，使用指定的模板页面
 */
@Slf4j
public class ChainResult extends RedirectResult {

    final static private String CHAIN_INVOKE_TIMES = "chainInvokeTimes";

    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception {
        String location = getConfigLocationUrl(actionInvocation);
        Action action = actionInvocation.getActionProxy().getAction();
        String namespace = action.getEnv(ActionEnv.Key_Namespace);
        if (location.contains("/")) {
            if (location.startsWith("/")) {
                namespace = StringUtil.substringBeforeLast(location, "/");
            } else {
                namespace = namespace + "/" + StringUtil.substringBeforeLast(location, "/");
            }
            action.put(ActionEnv.Key_Namespace, namespace);
        }
        String namePart = URLUtil.getFileNamePart(location);
        action.put(ActionEnv.Key_ActionName, namePart);
        WebConfigManager webConfigManager = TxWebConfigManager.getInstance();
        final ActionConfig actionConfig = webConfigManager.getActionConfig(namePart, namespace, true);
        if (actionConfig == null) {
            log.info("Chain result  not find action config namespace=" + namespace + "  action=" + namePart);
            return;
        }

        //设置新的action环境变量,避免死循环 begin
        action.setResult(null);
        action.getEnv().remove("submit");
        action.getEnv().remove("method");

        //设置新的action环境变量,避免死循环 end
        ActionInvocation chainActionInvocation = null;
        try {
            chainActionInvocation = new DefaultActionInvocation(actionConfig, action.getEnv(), ActionHandle.NAME, null, action.getRequest(), action.getResponse(),false);
            chainActionInvocation.initAction();
            if (action.getClass().getName().equals(chainActionInvocation.getActionProxy().getAction().getClass().getName())) {
                int times = ObjectUtil.toInt(chainActionInvocation.getActionProxy().getAction().getEnv(CHAIN_INVOKE_TIMES));
                times++;
                if (times > 3) {
                    chainActionInvocation.getActionProxy().getAction().put(CHAIN_INVOKE_TIMES, times);
                    chainActionInvocation.getActionProxy().getAction().setActionResult(ActionSupport.NONE);
                    return; //防止循环调用
                }
            }
            chainActionInvocation.invoke();
        } catch (ValidException e) {
            e.printStackTrace();
        }finally {
            if (chainActionInvocation!=null)
            {
                chainActionInvocation.executeResult(null);
            }
        }

    }
}
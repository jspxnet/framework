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

import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sober.queue.RedisStoreQueueClient;
import com.github.jspxnet.txweb.ActionProxy;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.online.OnlineManager;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.ActionLog;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-8-21
 * Time: 下午9:51
 */
@Slf4j
@Bean
public class ActionLogInterceptor extends InterceptorSupport {
    /**
     * 载入在线管理
     */
    @Ref
    private OnlineManager onlineManager;

    @Ref
    private RedisStoreQueueClient redisStoreQueueClient;


    private boolean guestLog = false;

    /**
     *
     * @param guestLog 是否记录游客日志
     */
    public void setGuestLog(boolean guestLog) {
        this.guestLog = guestLog;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        String result = actionInvocation.invoke();
        ActionProxy actionProxy = actionInvocation.getActionProxy();
        ActionSupport action = actionProxy.getAction();
        //游客就不记录了
        if (guestLog && action.isGuest() || !actionInvocation.isExecuted()) {
            return result;
        }

        //也可以 return Action.ERROR; 终止action的运行
        //保存历史记录 begin
        //@method
        String operation = actionProxy.getMethod().getName();
        if (TXWebUtil.defaultExecute.equalsIgnoreCase(operation) && !RequestUtil.isMultipart(action.getRequest()) || StringUtil.isEmpty(operation)) {
            return result;
        }
        ActionLog actionLog = action.getActionLog();
        if (actionLog != null && !StringUtil.isNull(actionLog.getContent())) {
            if (StringUtil.isNull(actionLog.getTitle()))
            {
                actionLog.setTitle(actionProxy.getCaption());
            }
            if (StringUtil.isNull(actionLog.getTitle())) {
                actionLog.setTitle(operation);
            }
            actionLog.setCaption(actionProxy.getCaption());
            actionLog.setClassMethod(operation);
            actionLog.setMethodCaption(actionProxy.getMethodCaption());
            actionLog.setActionResult(result);
            if (redisStoreQueueClient!=null&&!redisStoreQueueClient.save(actionLog))
            {
                log.error("日志记录保存发生错误");
            }
            //删除3年前的记录数据
        }
        //执行下一个动作,可能是下一个拦截器,也可能是action取决你的配置
        return result;
        //也可以 return Action.ERROR; 终止action的运行
    }
}
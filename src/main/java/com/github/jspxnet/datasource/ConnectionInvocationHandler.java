/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.datasource;

import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2007-3-23
 * Time: 14:33:39
 * 连接池的链接句柄，封装链接
 */
@Slf4j
public class ConnectionInvocationHandler implements InvocationHandler {

    private String checkSql = null;
    private boolean isClosed = false;
    private Connection target;
    private long lastAccessTime = System.currentTimeMillis();
    private int maxConnectionTime = DateUtil.HOUR;  //不能大于 8小时
    private final static String methodClose = "close";
    private final static String methodIsConnect = "isConnect";
    private final static String methodOpen = "open";
    private final static String methodHashCode = "hashCode";
    private final static String methodRelease = "release";
    private final static String methodIsClosed = "isClosed";
    private final static String methodSetCheckSql = "setCheckSql";
    private final static String methodSetMaxConnectionTime = "setMaxConnectionTime";
    private final static String methodIsOvertime = "isOvertime"; //超出最大链接时间2倍就强制关闭

    public ConnectionInvocationHandler(Connection target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (methodClose.equals(method.getName())) {
            isClosed = true;
            return null;
        }
        //--------------------------------------------------------------------------------------------------------------
        if (methodOpen.equals(method.getName())) {
            isClosed = false;
            return target != null && !target.isClosed();
        }
        //--------------------------------------------------------------------------------------------------------------
        if (methodIsClosed.equals(method.getName())) {
            return isClosed;
        }
        //--------------------------------------------------------------------------------------------------------------
        /**
         * 判断是否可以关闭了:
         * 1.真的已经关闭了
         */
        if (methodIsConnect.equals(method.getName())) {
            if (target == null || target.isClosed()) {
                isClosed = true;
                return false;
            }
            try {
                if (!StringUtil.isNull(checkSql)) {
                    target.createStatement().executeQuery(checkSql).close();
                }
            } catch (Exception e) {
                log.debug("checkSql:{}",checkSql);
                isClosed = true;
                return false;
            }
            return true;
        }
        //--------------------------------------------------------------------------------------------------------------
        if (methodSetCheckSql.equals(method.getName())) {
            checkSql = (String) args[0];
            return null;
        }
        //--------------------------------------------------------------------------------------------------------------
        if (methodSetMaxConnectionTime.equals(method.getName())) {
            maxConnectionTime = (Integer) args[0];
            if (maxConnectionTime < DateUtil.HOUR) {
                maxConnectionTime = DateUtil.HOUR;
            }
            return null;
        }
        //--------------------------------------------------------------------------------------------------------------
        if (methodIsOvertime.equals(method.getName())) {
            return (System.currentTimeMillis() - lastAccessTime > maxConnectionTime);
        }
        //--------------------------------------------------------------------------------------------------------------
        if (methodHashCode.equals(method.getName())) {
            if (target == null) {
                return 0;
            }
            return target.hashCode();
        }
        //--------------------------------------------------------------------------------------------------------------
        if (methodRelease.equals(method.getName())) {
            if (target != null)   //数据库连接断开后，这里会出现异常
            {
                try {
                    if (!target.isClosed()) {
                        target.close();
                    }
                    target = null;
                } catch (Throwable e) {
                    //...
                }
            }
            return target == null;
        }
        //--------------------------------------------------------------------------------------------------------------
        if (target != null) {
            lastAccessTime = System.currentTimeMillis();
            return method.invoke(target, args);
        }
        return null;
    }

}
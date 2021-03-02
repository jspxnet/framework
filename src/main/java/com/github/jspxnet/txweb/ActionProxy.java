/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb;

import com.github.jspxnet.json.JSONObject;
import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-12-26
 * Time: 23:29:46
 */
public interface ActionProxy {

    void setAction(Action action);

    Method getMethod();

    Action getAction();

    String execute() throws Exception;

    void destroy();

    void setMethod(String method);


    String getNamespace();

    String getActionName();

    void setNamespace(String namespace);

    String getCaption();

    String getMethodCaption() throws NoSuchMethodException;

    void setCallJson(JSONObject callJson);

    JSONObject getCallJson();

    void setExeType(String exeType);

    String getExeType();

}
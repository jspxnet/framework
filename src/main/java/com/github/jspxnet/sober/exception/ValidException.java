/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.exception;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: 陈原
 * date: 2007-12-4
 * Time: 16:27:24
 */
public class ValidException extends Exception {
    private Map<String, String> validMap = null;

    public ValidException(String s) {
        super(s);
    }

    public ValidException(Map<String, String> map, Object obj) {
        super();
        setStackTraceElement(map, obj, -1);
    }

    public ValidException(Map<String, String> map, Object obj, int index) {
        super();
        setStackTraceElement(map, obj, index);
    }

    public void setStackTraceElement(Map<String, String> map, Object obj, int index) {

        validMap = map;
        int i = 0;
        StackTraceElement[] ses;
        StackTraceElement se;
        if (index > 0) {
            ses = new StackTraceElement[map.size() + 1];
            se = new StackTraceElement(obj.getClass().getName(), "error column count:" + map.size(), "list", index);
            ses[0] = se;
            i = 1;
        } else {
            ses = new StackTraceElement[map.size()];
        }
        for (String key : map.keySet()) {
            se = new StackTraceElement(obj.getClass().getName(), key, map.get(key), index);
            ses[i] = se;
            i++;
        }
        setStackTrace(ses);

    }

    public Map<String, String> getValidMap() {
        return validMap;
    }

}
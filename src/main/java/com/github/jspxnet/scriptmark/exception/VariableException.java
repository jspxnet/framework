/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.exception;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-16
 * Time: 14:28:53
 */
public class VariableException extends RuntimeException {
    private String variableName;


    public VariableException(Class cla, String variableName, String message) {
        super(message + " is " + variableName);
        this.variableName = variableName;
    }

    public String getVariableName() {
        return variableName;
    }

}
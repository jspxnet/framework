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
 * Time: 15:04:47
 */
public class UndefinedException extends VariableException {

    private static final long serialVersionUID = 1L;

    public UndefinedException(String variableName) {
        super(UndefinedException.class, variableName, "Undefined Variable: " + variableName);
    }

    public UndefinedException(String variableName, String message) {
        super(UndefinedException.class, variableName, message);
    }
}
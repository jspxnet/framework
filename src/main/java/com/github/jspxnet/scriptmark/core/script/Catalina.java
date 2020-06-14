/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.core.script;

import org.mozilla.javascript.ScriptableObject;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-11-15
 * Time: 17:48:12
 * 本脚本在部分tomcat中运行，tomcat会运行程序调研本脚本，避免tomcat调研错误，虚拟部分变量提供给tomcat
 */
public class Catalina extends ScriptableObject {
    final public static String VAR_CATALINA = "catalina";

    @Override
    public String getClassName() {
        return "Object";
    }

    public String getHome() {
        return System.getenv("CATALINA_HOME");
    }

    public String getRoot() {
        return System.getenv("CATALINA_BASE");
    }

    public String getOut() {
        return System.getenv("CATALINA_OUT");
    }

    public String getOpts() {
        return System.getenv("CATALINA_OPTS");
    }

    public String getTempDir() {
        return System.getenv("CATALINA_TMPDIR");
    }
}
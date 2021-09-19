/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.utils;

import com.github.jspxnet.scriptmark.ScriptRunner;
import com.github.jspxnet.scriptmark.core.script.TemplateScriptEngine;


/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-3-8
 * Time: 上午11:57
 * <p>
 * 脚本执行
 */
public final class JScriptUtil {
    private final static ScriptRunner SCRIPT_RUNNER = new TemplateScriptEngine();

    public static String cleanWordTag(String html) {
        SCRIPT_RUNNER.put("html", html);
        try {
            return (String) SCRIPT_RUNNER.eval("cleanWordTag(html)", 0);
        } catch (Exception e) {
            return html;
        }
    }
}
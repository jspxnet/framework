/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark;


import org.mozilla.javascript.ScriptableObject;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-31
 * Time: 12:08:06
 */
public interface SingletonContext {
    ScriptableObject getGlobalScope();
}
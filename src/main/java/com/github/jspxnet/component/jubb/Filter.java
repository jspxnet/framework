/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.component.jubb;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2004-4-21
 * Time: 19:51:14
 */
public interface Filter {
    void setInputString(String s);

    String getInputString();

    String getFilterString();
}
/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.support;

import com.github.jspxnet.txweb.annotation.HttpMethod;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-3-9
 * Time: 22:05:55
 * 空的做为默认的模板支持
 * 一个空动作，需要模板页面的动作
 */
@HttpMethod(caption = "默认模板")
public class DefaultTemplateAction extends ActionSupport {
    public DefaultTemplateAction() {

    }
}
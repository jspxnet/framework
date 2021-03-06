/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.boot.environment;

import com.github.jspxnet.sioc.BeanFactory;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-11-8
 * Time: 16:26:00
 */
public interface SpringContext {
    void setConfigFile(String[] springCofig);

    void setConfigFile(String springCofig);

    BeanFactory getContext();

    void destroy();
}
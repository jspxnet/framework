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

import com.github.jspxnet.txweb.config.ResultConfigBean;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-12-26
 * Time: 19:57:19
 */

public interface Result extends Serializable {
    ResultConfigBean getResultConfig();

    void setResultConfig(ResultConfigBean resultConfig);

    void execute(ActionInvocation actionInvocation) throws Exception;
}
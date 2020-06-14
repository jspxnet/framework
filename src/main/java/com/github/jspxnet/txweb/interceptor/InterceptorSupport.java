/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.interceptor;

import com.github.jspxnet.enums.UserEnumType;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.boot.environment.Environment;


import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.Interceptor;
import com.github.jspxnet.txweb.ActionInvocation;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.bundle.Bundle;
import com.github.jspxnet.txweb.table.Role;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * date: 2006-12-26
 * Time: 18:17:52
 *
 * @author chenYuan (mail:39793751@qq.com)
 */
public abstract class InterceptorSupport implements Interceptor {
    /*
不能使用  actionProxy 中的配置，应为如果是夸命名空间的调用，配置会是更底层的一个命名空间
 */
    @Ref(name = Environment.language, test = true)
    protected Bundle language;

    /*
    不能使用  actionProxy 中的配置，应为如果是夸命名空间的调用，配置会是更底层的一个命名空间
     */
    protected Bundle config;

    @Param(request = false)
    @Ref(name = Environment.config, test = true)
    public void setConfig(Bundle config) {
        this.config = config;
    }


    protected Role createDebugRole() {
        Role role = new Role();
        role.setId(Environment.DEBUG_ROLE_ID);
        role.setName(Environment.DEBUG_ROLE_NAME);
        role.setDescription("要关闭调试模式将配置 jspx.properties 里边permission=true,将开启所有权限");
        role.setOfficeType(YesNoEnumType.YES.getValue());
        role.setUserType(UserEnumType.ChenYuan.getValue());
        role.setUseUpload(YesNoEnumType.YES.getValue());
        role.setUploadSize(2048000); //kb
        role.setUploadImageSize(10240);
        role.setUploadVideoSize(2048000);
        role.setUploadFileTypes(StringUtil.ASTERISK);
        role.setPutUid(Environment.SYSTEM_ID);
        role.setPutName(Environment.SYSTEM_NAME);
        return role;
    }

    @Override
    public abstract String intercept(ActionInvocation actionInvocation) throws Exception;
}
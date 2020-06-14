/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.view;

import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.JspxNetApplication;
import com.github.jspxnet.boot.EnvFactory;

import java.net.InetAddress;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-5-11
 * Time: 18:01:04
 */
@HttpMethod(caption = "系统信息")
public class SystemInfoView extends ActionSupport {
    public SystemInfoView() {

    }

    @Override
    public String execute() throws Exception {
        put("osName", System.getProperty("os.name"));
        put("osVersion", System.getProperty("os.version"));
        //java.vm.name=Java HotSpot(TM) 64-Bit Server VM
        put("jvmName", System.getProperty("java.vm.name"));
        put("jvmVersion", System.getProperty("java.vm.version"));
        put("osArch", System.getProperty("os.arch"));
        put("osArchDataModel", System.getProperty("sun.arch.data.remote"));


        put("graphicsenv", System.getProperty("java.awt.graphicsenv"));
        put("usePlatformFont", StringUtil.toBoolean(System.getProperty("java2d.font.usePlatformFont")));
        put("fileEncoding", System.getProperty("file.encoding"));
        put("totalMemory", Runtime.getRuntime().totalMemory() / 1048576);
        put("useMemory", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576);
        put("freeMemory", Runtime.getRuntime().freeMemory() / 1048576);
        put("serverPort", request.getServerPort());
        put("serverHost", InetAddress.getLocalHost().toString());
        put("protocol", request.getProtocol());
        put("serverDate", new Date());


        put("realPath", getEnv(ActionEnv.Key_RealPath));
        put("coreVersion", Environment.version);
        put("runDay", JspxNetApplication.runDay());
        put(Environment.startRunDate, JspxNetApplication.getRunDate());
        put("coreCheckRun", JspxNetApplication.checkRun());

        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        put(Environment.defaultPath, envTemplate.getString(Environment.defaultPath));
        put(Environment.resPath, envTemplate.getString(Environment.resPath));
        put(Environment.encode, envTemplate.getString(Environment.encode));
        put(Environment.defaultPath, envTemplate.getString(Environment.defaultPath));
        put(Environment.resPath, envTemplate.getString(Environment.resPath));
        put(Environment.encode, envTemplate.getString(Environment.encode));
        return SUCCESS;
    }


}
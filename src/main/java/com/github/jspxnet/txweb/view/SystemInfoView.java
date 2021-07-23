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
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.util.TomcatUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.JspxNetApplication;
import com.github.jspxnet.boot.EnvFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    @Operate(caption = "得到系统信息",post = false,method = "sysinfo")
    public Map<String,Object> getSysInfoMap() throws UnknownHostException {
        Map<String,Object> result = new HashMap<>();
        result.put("osName", System.getProperty("os.name"));
        result.put("osVersion", System.getProperty("os.version"));
        //java.vm.name=Java HotSpot(TM) 64-Bit Server VM
        result.put("jvmName", System.getProperty("java.vm.name"));
        result.put("jvmVersion", System.getProperty("java.vm.version"));
        result.put("osArch", System.getProperty("os.arch"));
        result.put("osArchDataModel", System.getProperty("sun.arch.data.remote"));


        result.put("graphicsenv", System.getProperty("java.awt.graphicsenv"));
        result.put("usePlatformFont", StringUtil.toBoolean(System.getProperty("java2d.font.usePlatformFont")));
        result.put("fileEncoding", System.getProperty("file.encoding"));
        result.put("totalMemory", Runtime.getRuntime().totalMemory() / 1048576);
        result.put("useMemory", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576);
        result.put("freeMemory", Runtime.getRuntime().freeMemory() / 1048576);
        result.put("serverPort", request.getServerPort());
        result.put("serverHost", InetAddress.getLocalHost().toString());
        result.put("protocol", request.getProtocol());
        result.put("serverDate", new Date());


        result.put("realPath", getEnv(ActionEnv.Key_RealPath));
        result.put("coreVersion", Environment.VERSION);
        result.put("runDay", JspxNetApplication.runDay());
        result.put(Environment.startRunDate, JspxNetApplication.getRunDate());
        result.put("coreCheckRun", JspxNetApplication.checkRun());

        result.put("portList", TomcatUtil.getPortList());

        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        result.put(Environment.defaultPath, envTemplate.getString(Environment.defaultPath));
        result.put(Environment.resPath, envTemplate.getString(Environment.resPath));
        result.put(Environment.encode, envTemplate.getString(Environment.encode));
        result.put(Environment.defaultPath, envTemplate.getString(Environment.defaultPath));
        result.put(Environment.resPath, envTemplate.getString(Environment.resPath));
        result.put(Environment.encode, envTemplate.getString(Environment.encode));
        return result;
    }


    @Override
    public String execute() throws Exception {
        super.getEnv().putAll(getSysInfoMap());
        return SUCCESS;
    }


}
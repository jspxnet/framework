/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.boot;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;


/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 12-3-20
 * Time: 下午6:34
 * 这里是应用启动接口,可以使用exe4j 调用这个接口启动应用
 * 配置文件中加入:appBeanId  jspx.test.sioc
 * 这里是exe4j直接调用，不涉及其他aop方式
 *
 */
@Slf4j
public class ApplicationBoot {
    private static String appBeanId = StringUtil.empty;

    public static void main(String[] args) throws Exception {
        //arg[0] 运行路径
        if (ArrayUtil.isEmpty(args)) {
            JspxNetApplication.autoRun();
        } else {
            JspxNetApplication.autoRun(args[0]);
        }
        EnvironmentTemplate envTemplate = com.github.jspxnet.boot.EnvFactory.getEnvironmentTemplate();
        appBeanId = envTemplate.getString(Environment.appBeanId);
        log.info("ioc id:" + appBeanId);
        if (StringUtil.isNull(appBeanId)) {
            log.info("appBeanId ioc id is null,没有配置appBeanId作为启动接口点");
            return;
        }
        Thread.sleep(2000);     //等待数据库启动
        start(args);
        if (args != null) {
            log.info("args=" + ArrayUtil.toString(args, " "));
        }
    }

    public static void start(String[] args) {
        BeanFactory beanFactory = EnvFactory.getBeanFactory();
        beanFactory.getBean(appBeanId);
    }

    public static void restart(String[] args) {
        JspxNetApplication.restart();
        start(args);
    }
}
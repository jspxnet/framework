/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sioc.aop;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Scheduled;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.sioc.BeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-11-7
 * Time: 11:36:10
 * <p>
 * 自动启动容器
 */
@Bean
@Slf4j
public class AutoRunCommand {


    private String[] beanArray;

    private boolean enable = true;

    public AutoRunCommand() {

    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setBeanArray(String[] beanArray) {
        this.beanArray = beanArray;
    }

    public String[] getBeanArray() {
        return beanArray;
    }

    @Scheduled(cron = "* * * * *", once = true)
    public String run() {
        if (!enable) {
            return Environment.none;
        }
        BeanFactory beanFactory = EnvFactory.getBeanFactory();
        if (beanArray != null) {
            for (String beanKey : beanArray) {
                log.info("aop start " + beanKey);
                Object aopBean;
                if (beanKey.contains("@")) {
                    aopBean = beanFactory.getBean(StringUtil.substringBefore(beanKey, "@"), StringUtil.substringAfter(beanKey, "@"));
                } else {
                    aopBean = beanFactory.getBean(beanKey, null);
                }
            }
        }
        return Environment.SUCCESS;
    }


    @Override
    public String toString() {
        return ArrayUtil.toString(beanArray, StringUtil.SEMICOLON);
    }
}
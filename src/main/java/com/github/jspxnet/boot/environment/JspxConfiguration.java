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

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-11-8
 * Time: 16:32:01
 */
public interface JspxConfiguration {
    void setDefaultConfigFile(String defaultConfigFile);

    /**
     * 默认路径
     *
     * @return String
     */
    String getDefaultPath();


    /**
     * @param defaultPath 提供手动方式设置 在win服务方式 不能得到路径
     */
    void setDefaultPath(String defaultPath);

    String getConfigFilePath();

    /**
     * 配置文件
     *
     * @return String
     */
    String getIocConfigFile();

    /**
     * 开始运行日期
     *
     * @return Date
     */
    Date getStartRunDate();

    /**
     * 运行天数
     *
     * @return long
     */
    long getRunDay();

}
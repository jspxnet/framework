/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.config;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-5
 * Time: 15:04:23
 */
public interface Configuration {

    void setFileName(String fileName);

    Map<String, List<DefaultInterceptorBean>> getDefaultInterceptorMap();

    Map<String, String> getExtendMap();

    Map<String, Map<String, ActionConfigBean>> loadConfigMap() throws Exception;

    Map<String, List<ResultConfigBean>> getDefaultResultMap();

    List<ScanConfig> getScanPackageList();
}
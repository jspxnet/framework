/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.validator;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-15
 * Time: 23:43:20
 */
public interface Validator {

    void setEncode(String encode);

    String getEncode();

    String getConfigFile();

    void setConfigFile(String configFile);

    String getId();

    void setId(String formId);

    void setCheckObject(Object checkObject);

    String getXML() throws Exception;

    String getJson() throws Exception;


    Map<String, String> getInformation();
}
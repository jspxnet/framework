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

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-11-7
 * Time: 21:13:50
 */
public interface EnvironmentTemplate extends Serializable {
    Object get(String keys);

    String getString(String keys);

    String getString(String keys, String def);

    int getInt(String keys);

    int getInt(String keys, int def);

    long getLong(String keys, long def);

    boolean getBoolean(String keys);

    void put(String keys, Object value);

    Map<String, Object> getVariableMap();

    void createPathEnv(String defaultPath);

    void createSystemEnv();

    String processTemplate(String templateString) ;

    void createJspxEnv(String fileName);

    Properties getProperties();

    void restorePlaceholder();

    boolean containsName(String key);

    void debugPrint();
}
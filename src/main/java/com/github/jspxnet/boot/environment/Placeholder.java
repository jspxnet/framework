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

import java.util.Map;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-6-4
 * Time: 22:05:52
 */
public interface Placeholder {
    String getCurrentPath();

    void setCurrentPath(String currentPath);

    String getRootDirectory();

    void setRootDirectory(String rootDirectory);

    String processTemplate(Map<String, Object> valueMap, String templateString);

    String processTemplateException(Map<String, Object> valueMap, String templateString) throws Exception;

    String processTemplate(Map<String, Object> valueMap, File fileName, String encode);
}
/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark;

import java.io.Serializable;
import java.io.Writer;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-11
 * Time: 16:59:17
 */
public interface ScriptMark extends Serializable {
    void setCurrentPath(String currentPath);

    void setRootDirectory(String rootDirectory);

    void process(Writer out, Map<String, Object> map) throws Exception;

}
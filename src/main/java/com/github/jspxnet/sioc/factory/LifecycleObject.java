/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sioc.factory;

import com.github.jspxnet.boot.conf.AppolloBootConfig;
import com.github.jspxnet.boot.environment.Environment;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-13
 * Time: 10:59:43
 */

@Data
public class LifecycleObject implements Serializable {

    private Map<String, Object> paramMap = new HashMap<String, Object>();
    private long createDate = System.currentTimeMillis();
    private String create;
    private Object object;
    private String name;
    private boolean isSingleton = true;
    private String className;
    //真实命名空间,配置
    private String namespace = Environment.Global;
    //虚拟命名空间
    private String refNamespace = Environment.Global;



    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap.putAll(paramMap);
    }

    public String getClassName() {
        if (className == null) {
            return null;
        }
        return className.trim();
    }

    public String getRefNamespace() {
        if (!Environment.Global.equals(refNamespace)) {
            return refNamespace;
        }
        return namespace;
    }

}
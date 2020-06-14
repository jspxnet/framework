/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober;

/**
 * Created by IntelliJ IDEA.
 * User: 陈原
 * date: 2007-11-1
 * Time: 15:17:44
 */
public class BackUpBean {
    public BackUpBean() {

    }
    //gzrj.gov.cn

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean isUseNamespace() {
        return useNamespace;
    }

    public void setUseNamespace(boolean useNamespace) {
        this.useNamespace = useNamespace;
    }

    private String className = null;
    private String caption = null;
    private boolean useNamespace = false;

}
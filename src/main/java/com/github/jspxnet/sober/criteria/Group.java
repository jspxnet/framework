/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.criteria;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-9
 * Time: 16:56:46
 */

public class Group implements Serializable {
    public Group() {

    }

    //分组
    private String propertyName;

    @Override
    public String toString() {
        return propertyName;
    }

    protected Group(String propertyName) {
        this.propertyName = propertyName;

    }


}
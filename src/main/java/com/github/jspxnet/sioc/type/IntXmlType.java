/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sioc.type;

import com.github.jspxnet.utils.StringUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-3-22
 * Time: 11:36:51
 */
public class IntXmlType extends TypeSerializer {

    @Override
    public Type getJavaType()
    {
        return int.class;
    }

    @Override
    public String getTypeString() {
        return "int";
    }

    @Override
    public Object getTypeObject() {
        return StringUtil.toInt(value.toString());
    }

    @Override
    public String getXmlString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<int name=\"").append(name).append("\">").append(value).append("</int>\r\n");
        return sb.toString();
    }
}
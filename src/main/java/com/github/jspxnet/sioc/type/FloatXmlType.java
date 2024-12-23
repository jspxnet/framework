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

import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;

import java.lang.reflect.Type;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-3-22
 * Time: 20:55:10
 */
public class FloatXmlType extends TypeSerializer {

    @Override
    public Type getJavaType()
    {
        return float.class;
    }

    @Override
    public String getTypeString() {
        return "float";
    }

    @Override
    public Object getTypeObject() {
        if (StringUtil.isNullOrWhiteSpace(value.toString()))
        {
            return 0f;
        }
        return ObjectUtil.toFloat(value.toString().trim());
    }

    @Override
    public String getXmlString() {
        return "<float name=\"" + name + "\">" + value + "</float>\r\n";
    }
}
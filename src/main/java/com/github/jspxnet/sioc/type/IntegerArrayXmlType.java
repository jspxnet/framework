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

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-4
 * Time: 23:47:16
 */
public class IntegerArrayXmlType extends TypeSerializer {

    @Override
    public Type getJavaType()
    {
        return Integer[].class;
    }

    @Override
    public String getTypeString() {
        return "array";
    }

    @Override
    public Object getTypeObject() {
        String[] stringArray = StringUtil.split((String) value, ",");
        int[] result = new int[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            result[i] = StringUtil.toInt(stringArray[i]);
        }
        return result;
    }

    /**
     * @return 返回XML结果
     */
    @Override
    public String getXmlString() {
        Integer[] theValue = (Integer[]) value;
        if (theValue == null || theValue.length < 1) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<array name=\"").append(name).append("\" class=\"").append("int").append("\">\r\n");
        for (Object o : theValue) {
            sb.append("<value>").append(o).append("</value>\r\n");
        }
        sb.append("</array>\r\n");
        return sb.toString();
    }
}
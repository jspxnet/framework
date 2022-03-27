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
 * date: 2007-4-5
 * Time: 0:05:44
 */
public class FloatArrayXmlType extends ArrayXmlType {
    @Override
    public Type getJavaType()
    {
        return float[].class;
    }
    @Override
    public Object getTypeObject() {
        String[] stringArray = StringUtil.split((String) value, ",");
        long[] result = new long[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            result[i] = StringUtil.toLong(stringArray[i]);
        }
        return result;
    }

    /**
     * @return 返回XML结果
     */
    @Override
    public String getXmlString() {
        if (value instanceof float[]) {
            float[] theValue = (float[]) value;
            if (theValue.length < 1) {
                return StringUtil.empty;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("<array name=\"").append(name).append("\" class=\"").append("long").append("\">\r\n");
            for (float o : theValue) {
                sb.append("<value>").append(o).append("</value>\r\n");
            }
            sb.append("</array>\r\n");
            return sb.toString();
        }
        Float[] theValue = (Float[]) value;
        if (theValue == null || theValue.length < 1) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<array name=\"").append(name).append("\" class=\"").append("long").append("\">\r\n");
        for (Float o : theValue) {
            sb.append("<value>").append(o).append("</value>\r\n");
        }
        sb.append("</array>\r\n");
        return sb.toString();
    }

}
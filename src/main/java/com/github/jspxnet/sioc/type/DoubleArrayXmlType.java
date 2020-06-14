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

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-3-22
 * Time: 22:37:37
 */
public class DoubleArrayXmlType extends ArrayXmlType {
    @Override
    public String getTypeString() {
        return "array";
    }

    @Override
    public Object getTypeObject() {
        String[] stringArray = StringUtil.split((String) value, ",");
        double[] result = new double[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            result[i] = StringUtil.toDouble(stringArray[i]);
        }
        return result;
    }

    /**
     * @return 返回XML结果
     */
    @Override
    public String getXmlString() {
        if (value instanceof double[]) {
            double[] theValue = (double[]) value;
            if (theValue.length < 1) {
                return StringUtil.empty;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("<array name=\"").append(name).append("\" class=\"").append("double").append("\">\r\n");
            for (double o : theValue) {
                sb.append("<value>").append(o).append("</value>\r\n");
            }
            sb.append("</array>\r\n");
            return sb.toString();
        }
        Double[] theValue = (Double[]) value;
        if (theValue == null || theValue.length < 1) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<array name=\"").append(name).append("\" class=\"").append("double").append("\">\r\n");
        for (Double o : theValue) {
            sb.append("<value>").append(o).append("</value>\r\n");
        }
        sb.append("</array>\r\n");
        return sb.toString();
    }

}
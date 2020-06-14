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

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-3-22
 * Time: 22:54:18
 */
public class CharXmlType extends TypeSerializer {
    @Override
    public String getTypeString() {
        return "int";
    }

    @Override
    public Object getTypeObject() {
        if (value == null) {
            return null;
        }
        return value.toString().charAt(0);
    }

    @Override
    public String getXmlString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<char name=\"").append(name).append("\">").append(value).append("</char>\r\n");
        return sb.toString();
    }
}
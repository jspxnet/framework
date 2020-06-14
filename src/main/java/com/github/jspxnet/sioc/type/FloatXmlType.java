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
 * Time: 20:55:10
 */
public class FloatXmlType extends TypeSerializer {
    @Override
    public String getTypeString() {
        return "float";
    }

    @Override
    public Object getTypeObject() {
        return new Float(value.toString().trim());
    }

    @Override
    public String getXmlString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<float name=\"").append(name).append("\">").append(value).append("</float>\r\n");
        return sb.toString();
    }
}
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
 * Time: 11:36:19
 */
public class StringXmlType extends TypeSerializer {
    @Override
    public String getTypeString() {
        return "string";
    }

    @Override
    public Object getTypeObject() {
        if (value == null) {
            return StringUtil.empty;
        }
        return value.toString();
    }

    @Override
    public String getXmlString() {
        if (value == null) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<string name=\"").append(name).append("\">");
        sb.append(value);
        sb.append("&gt;/string&lg;\r\n");
        return sb.toString();
    }

}
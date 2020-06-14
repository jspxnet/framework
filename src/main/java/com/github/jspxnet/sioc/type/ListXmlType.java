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

import com.github.jspxnet.sioc.util.TypeUtil;
import com.github.jspxnet.utils.StringUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-3-22
 * Time: 12:51:31
 */
public class ListXmlType extends TypeSerializer {
    @Override
    public String getTypeString() {
        return "list";
    }

    @Override
    public String getXmlString() throws Exception {
        List list = (List) value;
        if (list == null || list.isEmpty()) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        String typeString = TypeUtil.getTypeString(list.get(0).getClass());
        sb.append("<list name=\"").append(name).append("\" class=\"").append(typeString).append("\">\r\n");
        for (Object o : list) {
            if (TypeUtil.isBaseType(typeString)) {
                sb.append("<value>").append(o).append("</value>\r\n");
            } else {
                sb.append("<value>").append(TypeUtil.getTypeSerializer(typeString, o)).append("</value>\r\n");
            }
        }
        sb.append("</list>");
        return sb.toString();
    }
}
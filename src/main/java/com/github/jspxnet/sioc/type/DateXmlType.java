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

import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;

import java.util.Date;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-3-22
 * Time: 11:36:43
 */
public class DateXmlType extends TypeSerializer {
    @Override
    public String getTypeString() {
        return "date";
    }

    @Override
    public Object getTypeObject() {
        if (value instanceof java.sql.Date) {
            return DateUtil.toJavaDate((java.sql.Date) value);
        }
        if (value instanceof String) {
            try {
                return StringUtil.getDate((String) value);
            } catch (Exception e) {
                return value;
            }
        }
        return value;
    }

    @Override
    public String getXmlString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<date name=\"").append(name).append("\">").append(DateUtil.toString((Date) value, DateUtil.ST_FORMAT)).append("</date>\r\n");
        return sb.toString();
    }
}
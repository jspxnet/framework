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

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-3-22
 * Time: 22:24:00
 */
public class DateArrayXmlType extends ArrayXmlType {
    @Override
    public String getTypeString() {
        return "array";
    }

    @Override
    public Object getTypeObject() throws Exception {
        String[] stringArray = StringUtil.split((String) value, ",");
        Date[] result = new Date[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            result[i] = StringUtil.getDate(stringArray[i]);
        }
        return result;
    }
}
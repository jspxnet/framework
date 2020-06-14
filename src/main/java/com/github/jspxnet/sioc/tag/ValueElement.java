/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sioc.tag;

import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.utils.XMLUtil;

import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.scriptmark.core.TagNode;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-12
 * Time: 0:44:40
 * [value key="xxx" ref="true" class="string"]2[/value]
 */
public class ValueElement extends TagNode {
    public ValueElement() {

    }

    public boolean isRef() {
        return StringUtil.toBoolean(XMLUtil.deleteQuote(getStringAttribute(Sioc.IocRef)));
    }

    public String getKey() {
        return XMLUtil.deleteQuote(getStringAttribute("key"));
    }

    public boolean getSelected() {
        return StringUtil.toBoolean(XMLUtil.deleteQuote(getStringAttribute("selected")));
    }

    public String getClassName() {
        return XMLUtil.deleteQuote(getStringAttribute(Sioc.IocClass));
    }

    public String getValue() {
        return getBody();
    }
}
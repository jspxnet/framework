/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.core.block;

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.core.TemplateElement;
import com.github.jspxnet.scriptmark.exception.ScriptRunException;
import com.github.jspxnet.utils.StringUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-16
 * Time: 12:10:20
 * <pre>
 * {@code
 * <#setting name=value/>
 *  }</pre>
 */
public class SettingBlock extends TagNode {
    public static final String var = "var";

    public String getVarName() {
        String name = getStringAttribute(var);
        if (StringUtil.hasLength(name)) {
            return name;
        }
        String[] attributeNames = getAttributeName();
        if (attributeNames == null) {
            return null;
        }
        for (String s : attributeNames) {
            if (s != null) {
                return s;
            }
        }
        return null;
    }

    public String getValue() {
        String value = getExpressionAttribute(getVarName());
        if (StringUtil.hasLength(value)) {
            return value;
        }
        return getBody();
    }

    public List<TagNode> getValueList() throws ScriptRunException {
        TemplateElement templateEl = new TemplateElement(getValue(), getTemplate().getLastModified(), getTemplate().getConfigurable());
        return templateEl.getRootTree();
    }

}
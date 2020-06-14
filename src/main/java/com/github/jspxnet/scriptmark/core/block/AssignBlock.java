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
import com.github.jspxnet.scriptmark.util.ScriptMarkUtil;

import com.github.jspxnet.utils.StringUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-16
 * Time: 12:13:17
 * name为变量名称
 * or
 * <pre>{@code
 *
 * <#assign var=name>
 * <#list 1..3 as n>
 * ${n} <@myMacro />
 * </#list>
 * </#assign>
 * or
 * <#assign var=botton>[name:"btname";value:"1111"]</#assign>
 * ${botton.name}=btname
 * or
 *
 * <#assign mail="chen@other.com" in=my>
 * <#assign mail="jsmith@other.com" in=you>
 * ${my.mail}, ${you.mail}
 * }</pre>
 */
public class AssignBlock extends TagNode {
    public static final String SCRIPT = "script";
    public static final String VAR_KEY = "var";
    public static final String IN_KEY = "in";
    public static final String TYPE_KEY = "type";  //很多时候需要放入吧执行的程序，可以在这里定义类型为text,txt

    public AssignBlock() {
        repair = true;
    }

    public String getVarName() {
        String name = getStringAttribute(VAR_KEY);
        if (StringUtil.hasLength(name)) {
            return ScriptMarkUtil.deleteQuote(name);
        }
        String[] attributes = getAttributeName();
        if (attributes == null) {
            return null;
        }
        for (String s : attributes) {
            if (!IN_KEY.equalsIgnoreCase(s)) {
                return s;
            }
        }
        return null;
    }

    //得到值
    public String getValue() {
        String value = getExpressionAttribute(getVarName());
        if (StringUtil.hasLength(value)) {
            return ScriptMarkUtil.deleteQuote(value);
        }
        return getBody();
    }


    public List<TagNode> getValueList() throws ScriptRunException {
        return new TemplateElement(getValue(), getTemplate().getLastModified(), getTemplate().getConfigurable()).getRootTree();
    }

    public String getIn() {
        return ScriptMarkUtil.deleteQuote(getExpressionAttribute(IN_KEY));
    }

    public String getType() {
        String sType = ScriptMarkUtil.deleteQuote(getExpressionAttribute(TYPE_KEY));
        if (StringUtil.isNull(sType)) {
            return SCRIPT;
        }
        return sType;
    }

}
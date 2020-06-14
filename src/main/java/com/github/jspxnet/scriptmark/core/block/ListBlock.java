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
import com.github.jspxnet.utils.XMLUtil;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-14
 * Time: 17:22:15
 * 列表循环
 * <pre>{@code
 * <#list v=list>
 * <li>${v.index}-${v}</li>
 * </#list>
 * or
 * <#list var=v list=listV>
 * <li>${v.index}-${v}</li>
 * </#list>
 * }</pre>
 */
public class ListBlock extends TagNode {

    public static final String VAR_KEY = "var";
    public static final String LIST_KEY = "list";
    public static final String EQUALS_KEY = "equals";
    public static final String OPEN_KEY = "open";
    public static final String CLOSE_KEY = "close";
    public static final String SEPARATOR_KEY = "separator";
    public static final String EMPTY_KEY = "empty";

    /**
     *
     * @return 得到变量名称
     */
    public String getVarName() {
        String name = getStringAttribute(VAR_KEY);
        if (StringUtil.hasLength(name)) {
            return name;
        }
        String[] attributeNames = getAttributeName();
        //不为 list= 的就当成变量名
        if (attributeNames != null) {
            for (String s : attributeNames) {
                if (!LIST_KEY.equalsIgnoreCase(s)) {
                    return s;
                }
            }
        }
        return null;
    }

    /**
     *
     * @return 列表名称
     */
    public String getListName() {
        return XMLUtil.deleteQuote(getStringAttribute(getVarName()));
    }

    /**
     *
     * @return 开始的字符串
     */
    public String getOpen() {
        return XMLUtil.deleteQuote(getStringAttribute(OPEN_KEY));
    }

    /**
     *
     * @return 结束的字符串
     */
    public String getClose() {
        return XMLUtil.deleteQuote(getStringAttribute(CLOSE_KEY));
    }

    /**
     *
     * @return 分割
     */
    public String getSeparator() {
        return XMLUtil.deleteQuote(getStringAttribute(SEPARATOR_KEY));
    }

    /**
     *
     * @return 空数据输出
     */
    public String getEmpty() {
        return XMLUtil.deleteQuote(getStringAttribute(EMPTY_KEY));
    }

    /**
     *
     * @return 如果开始值等于结束值是否运行一次
     */
    public boolean getEquals() {
        String eq = XMLUtil.deleteQuote(getStringAttribute(EQUALS_KEY));
        return !("false".equalsIgnoreCase(eq) || "0".equals(eq));
    }

    /**
     *
     * @return 子节点列表
     * @throws ScriptRunException 异常
     */
    @Override
    public List<TagNode> getChildList() throws ScriptRunException
    {
        TemplateElement templateEl = new TemplateElement(getBody(), 0, getTemplate().getConfigurable());
        return templateEl.getRootTree();
    }
}
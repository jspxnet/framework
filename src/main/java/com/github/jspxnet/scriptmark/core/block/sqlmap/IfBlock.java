/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.core.block.sqlmap;

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.core.TemplateElement;
import com.github.jspxnet.scriptmark.core.block.BaseIfBlock;
import com.github.jspxnet.scriptmark.core.block.ElseBlock;
import com.github.jspxnet.scriptmark.util.ScriptMarkUtil;
import com.github.jspxnet.utils.StringUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-14
 * Time: 17:13:43
 * if 语句
 * <pre>
 * {@code
 * <#if where="ifthree">
 * 3333333333
 * 777777777<#if where=true>aaaa<#else where=true>bbb</#else>bbbb</#if>
 * <#else>
 * 444444444444
 * </#else>
 * 55555555555555 <#if where=true>aaaa<#else>bbbbbbb</#else>
 * ccccccc
 * </#if>
 * }
 * </pre>
 */
public class IfBlock extends TagNode implements BaseIfBlock {


    final private static String WHERE = "where";
    final private static String W = "w";

    final private static String ELSE_BEGIN = "else";
    final private static String ELSE_END = "/else";

    final private static String IF_BEGIN = "if";
    final private static String IF_END = "/if";

    public IfBlock() {

    }

    @Override
    public String getWhere() {
        String s = getExpressionAttribute(WHERE);
        if (!StringUtil.hasLength(s)) {
            s = getExpressionAttribute(W);
        }
        if (!StringUtil.hasLength(s)) {
            s = getAttributes();
        }
        if (!StringUtil.hasLength(s)) {
            return null;
        }
        return ScriptMarkUtil.deleteQuote(s.replaceAll(" lt ", "<").replaceAll(" le ", "<=").replaceAll(" gt ", ">").replaceAll(" ge ", ">=")).replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">").replaceAll("&amp;", StringUtil.AND).replaceAll(" and ", "&&").replaceAll(" or ", "||");
    }
    @Override
    public List<TagNode> getTrueNode() {
        try {
            TemplateElement templateElement = new TemplateElement(getBody(true), getTemplate().getLastModified(), getTemplate().getConfigurable());
            return templateElement.getRootTree();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 得到if 成立和不成立的body
     *
     * @param bt if true or false
     * @return 成立和不成立的body
     */
    @Override
    public String getBody(boolean bt) {
        String s = super.getBody();
        StringBuilder sb = new StringBuilder();
        int inElse = 0;
        int inIf = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == beginTag) {
                if ((i + 4 < s.length()) && IF_BEGIN.equalsIgnoreCase(s.substring(i + 1, i + 1 + IF_BEGIN.length()))) {
                    inIf++;
                }

                //inif == 0 &&
                if ((inIf == 0 && i + 6 < s.length()) && ELSE_BEGIN.equalsIgnoreCase(s.substring(i + 1, i + 1 + ELSE_BEGIN.length()))) {
                    inElse++;
                }
            }
            if (bt && inElse % 2 == 0) {
                sb.append(c);
            } else if (!bt && inElse % 2 != 0) {
                sb.append(c);
            }

            if (c == endTag) {
                if (i > IF_END.length() && IF_END.equalsIgnoreCase(s.substring(i - IF_END.length(), i))) {
                    inIf--;
                }
                //inif == 0 &&
                if (inIf == 0 && i > ELSE_END.length() && ELSE_END.equalsIgnoreCase(s.substring(i - ELSE_END.length(), i))) {
                    inElse--;
                }
            }
        }
        return sb.toString();
    }


    /**
     * @return 得到的是节点case
     */
    @Override
    public List<TagNode> getElseBlock() {
        TemplateElement templateEl = new TemplateElement(getBody(false), getTemplate().getLastModified(), getTemplate().getConfigurable());
        Map<String, String> termTagMap = new HashMap<>();
        termTagMap.put(ELSE_BEGIN, ElseBlock.class.getName());
        return templateEl.getBlockTree(getBody(false), termTagMap);
    }
}
/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.core;

import com.github.jspxnet.scriptmark.ScriptmarkEnv;
import com.github.jspxnet.scriptmark.Configurable;
import com.github.jspxnet.scriptmark.TemplateModel;
import com.github.jspxnet.scriptmark.config.TemplateConfigurable;
import com.github.jspxnet.scriptmark.core.block.CommentBlock;
import com.github.jspxnet.scriptmark.exception.ScriptRunException;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.XMLUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-14
 * Time: 15:18:02
 * xml元素解析器
 */

public abstract class TagNode implements Serializable {
    private TemplateModel templateElement;
    private String name = HtmlEngineImpl.NONE_TAG;
    protected int starLength = 0; //开始长度
    private int endLength = 0; //块的长度
    //private int errorLine = 0; //块的长度
    protected char beginTag = '<';
    protected char endTag = '>';
    protected char escapeVariable = '\\';
    protected char macroCallTag = '@';
    private String attribute = null;
    private boolean hasAttribute = false;

    //是否修复的,不会内嵌标签的 就修复,主要是html标签
    protected boolean repair = false;

    public void setTemplate(TemplateModel templateElement) {
        this.templateElement = templateElement;
        if (this.templateElement == null) {
            try {
                throw new Exception("模板对象不能为空,检查action 是否使用正确,TemplateModel is Null check action");
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        Configurable configurable = this.templateElement.getConfigurable();
        if (configurable == null) {
            configurable = TemplateConfigurable.getInstance();
        }
        String syncopate = configurable.getString(ScriptmarkEnv.Syncopate);
        if (syncopate == null || syncopate.length() < 1) {
            syncopate = "<>";
        }
        beginTag = syncopate.charAt(0);
        endTag = syncopate.charAt(1);
        escapeVariable = configurable.getString(ScriptmarkEnv.escapeVariable).charAt(0);
        macroCallTag = configurable.getString(ScriptmarkEnv.MacroCallTag).charAt(0);
    }

    public boolean isRepair() {
        return repair;
    }

    public void setRepair(boolean repair) {
        this.repair = repair;
    }

    /**
     * 得到属性值
     *
     * @param name 名称
     * @return 属性部分src
     */
    public String getExpressionAttribute(String name) {
        return XMLUtil.getExpressionAttribute(getAttributes(), name, escapeVariable);
    }

    /**
     * 得到属性值
     *
     * @param name 名称
     * @return 属性部分src
     */
    public String getStringAttribute(String name) {
        return XMLUtil.getStringAttribute(getAttributes(), name, escapeVariable);
    }

    /**
     * 得到属性部分的代码，提高速度
     *
     * @return xml Attr
     */
    public String getAttributes() {
        if (hasAttribute) {
            return attribute;
        }
        if (attribute != null) {
            return attribute;
        }
        String source = getSource();
        //缓存数据
        if (!com.github.jspxnet.utils.StringUtil.hasLength(source)) {
            hasAttribute = true;
            return attribute = StringUtil.empty;
        }
        boolean begin = false;
        int in = 0;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < source.length()) {
            char c = source.charAt(i);
            if (c == escapeVariable) {
                sb.append(source.charAt(i + 1));
                i = i + 2;
                continue;
            }
            if (c == beginTag) {
                in++;
            }
            if (c == endTag) {
                in--;
            }
            if (begin && in > 0) {
                sb.append(c);
            }
            if (c == ' ') {
                begin = true;
            }
            if (in == 0) {
                break;
            }
            i++;
        }
        if (sb.length() > 2 && sb.toString().endsWith("/")) {
            return sb.substring(0, sb.length() - 1).trim();
        }
        attribute = sb.toString().trim();
        hasAttribute = true;
        return attribute;
    }

    /**
     * 得到所有属性名称
     *
     * @return 属性名称数组
     */
    public String[] getAttributeName() {
        String xml = getAttributes();
        StringBuilder sb = new StringBuilder();
        int yh = 0;
        int yf = 0;
        int yd = 0;
        int ys = 0;
        int start = 0;
        int j = 0;
        while (j < xml.length()) {
            char c = xml.charAt(j);
            char old = ' ';
            if (j > 0) {
                old = xml.charAt(j - 1);
            }
            if (c == '[' && old != '\\') {
                yf++;
            }
            if (c == ']' && old != '\\') {
                yf--;
            }
            if (c == '{' && old != '\\') {
                yh++;
            }
            if (c == '}' && old != '\\') {
                yh--;
            }
            if (c == '\"' && old != '\\') {
                ys++;
            }

            if (c == '\'' && old != '\\') {
                yd++;
            }
            if (yf == 0 && yh == 0 && ys % 2 == 0 && yd % 2 == 0 && ' ' == c) {
                start = j;
            }
            if (start != -1 && yf == 0 && yh == 0 && ys % 2 == 0 && yd % 2 == 0 && '=' == c && '=' != old) {
                String sTmp = xml.substring(start, j).trim();
                if (StringUtil.EQUAL.equals(sTmp)) {
                    j++;
                    continue;
                }
                sb.append(sTmp).append(StringUtil.CRLF);
                start = -1;
            }
            j++;
        }
        return sb.toString().split(StringUtil.CRLF);
    }

    /**
     * 得到真实代码,添加一定的修复能力
     *
     * @return String
     */
    public String getSource() {
        if (starLength >= endLength) {
            return StringUtil.empty;
        }
        return templateElement.getSource(starLength, endLength);
    }

    /**
     * 得到正文内容部分,添加一定的修复能力
     *
     * @return 正文内容
     */
    public String getBody() {
        if (getTagName().equals(CommentBlock.NOTE_TAG_BEGIN)) {
            return StringUtil.empty;
        }
        if (HtmlEngineImpl.NONE_TAG.equals(name)) {
            return getSource();
        }
        return templateElement.getBody(starLength, endLength);
    }

    public List<TagNode> getChildList() throws ScriptRunException {
        TemplateElement templateEl = new TemplateElement(getBody(), templateElement.getLastModified(), templateElement.getConfigurable());
        return templateEl.getRootTree();
    }

    public String getTagName() {
        return name;
    }

    public void setTagName(String name) {
        this.name = name;
    }

    public int getStarLength() {
        return starLength;
    }

    public void setStarLength(int starLength) {
        this.starLength = starLength;
    }

    public int getEndLength() {
        return endLength;
    }

    public void setEndLength(int endLength) {
        this.endLength = endLength;
    }

    public TemplateModel getTemplate() {
        return templateElement;
    }

    public int getLineNumber() {
        //得到当前模板第几行
        int startLen = getStarLength();
        if (getSource().length() < startLen) {
            startLen = getSource().length();
        }
        return StringUtil.countMatches(getSource().substring(0, startLen), StringUtil.CR) + 1;
    }

}
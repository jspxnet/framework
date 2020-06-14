/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.core.block.template;

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.core.TemplateElement;
import com.github.jspxnet.scriptmark.core.block.BaseTryBlock;
import com.github.jspxnet.scriptmark.exception.ScriptRunException;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-16
 * Time: 12:58:31
 * 相当Freemarker 中的 attempt java 中的try catch语句
 * <pre>
 * {@code
 *
 * <#try>
 * 111111111
 * <#try>222<#catch>错误的时候运行---2222</#catch></#finally>一定运行--2222</#finally></#try>
 * <#catch>错误的时候运行</#catch>
 * </#finally>一定运行</#finally>
 * </#try>
 *  }</pre>
 */
public class TryBlock extends TagNode implements BaseTryBlock {
    public TryBlock() {

    }

    @Override
    public List<TagNode> getBodyList() throws Exception {
        TemplateElement templateEl = new TemplateElement(getBody(), 0, getTemplate().getConfigurable());
        return templateEl.getRootTree();
    }

    @Override
    public String getBody() {
        String s = super.getBody();
        StringBuilder sb = new StringBuilder();
        int infina = 0;
        int incatch = 0;
        int intry = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == beginTag) {
                if (i + 5 < s.length()) {
                    String ts = s.substring(i + 1, i + 5);
                    if ("#try".equalsIgnoreCase(ts)) {
                        intry++;
                    }
                }
                if (intry == 0 && i + 7 < s.length()) {
                    String ts = s.substring(i + 1, i + 7);
                    if ("#catch".equalsIgnoreCase(ts)) {
                        incatch++;
                    }
                }
                if (intry == 0 && i + 9 < s.length()) {
                    String ts = s.substring(i + 1, i + 9);
                    if ("#finally".equalsIgnoreCase(ts)) {
                        infina++;
                    }
                }
            }
            if (incatch == 0 && infina == 0) {
                sb.append(c);
            }
            if (c == endTag) {
                if (i > 5) {
                    String ts = s.substring(i - 5, i);
                    if ("/#try".equalsIgnoreCase(ts)) {
                        intry--;
                    }
                }
                if (intry == 0 && i > 7) {
                    String ts = s.substring(i - 7, i);
                    if ("/#catch".equalsIgnoreCase(ts)) {
                        incatch--;
                    }
                }
                if (intry == 0 && i > 9) {
                    String ts = s.substring(i - 9, i);
                    if ("/#finally".equalsIgnoreCase(ts)) {
                        infina--;
                    }
                }
            }

        }
        return sb.toString();
    }
    @Override
    public List<TagNode> getCatchBodyList() throws ScriptRunException {
        TemplateElement templateElement = new TemplateElement(getCatchBody(), getTemplate().getLastModified(), getTemplate().getConfigurable());
        return templateElement.getRootTree();
    }
    @Override
    public String getCatchBody() {
        String s = super.getBody();
        StringBuilder sb = new StringBuilder();
        int infina = 0;
        int incatch = 0;
        int intry = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == beginTag) {
                if (i + 5 < s.length()) {
                    String ts = s.substring(i + 1, i + 5);
                    if ("#try".equalsIgnoreCase(ts)) {
                        intry++;
                    }
                }
                if (intry == 0 && i + 7 < s.length()) {
                    String ts = s.substring(i + 1, i + 7);
                    if ("#catch".equalsIgnoreCase(ts)) {
                        incatch++;
                    }
                }
                if (intry == 0 && i + 9 < s.length()) {
                    String ts = s.substring(i + 1, i + 9);
                    if ("#finally".equalsIgnoreCase(ts)) {
                        infina++;
                    }
                }
            }
            if (incatch > 0 && infina == 0) {
                sb.append(c);
            }
            if (c == endTag) {
                if (i > 5) {
                    String ts = s.substring(i - 5, i);
                    if ("/#try".equalsIgnoreCase(ts)) {
                        intry--;
                    }
                }
                if (intry == 0 && i > 7) {
                    String ts = s.substring(i - 7, i);
                    if ("/#catch".equalsIgnoreCase(ts)) {
                        incatch--;
                    }
                }
                if (intry == 0 && i > 9) {
                    String ts = s.substring(i - 9, i);
                    if ("/#finally".equalsIgnoreCase(ts)) {
                        infina--;
                    }
                }
            }

        }
        return sb.substring(sb.indexOf(">") + 1, sb.lastIndexOf("<"));
    }
    @Override
    public List<TagNode> getFinallyBodyList() throws ScriptRunException {
        TemplateElement templateElement = new TemplateElement(getFinallyBody(), getTemplate().getLastModified(), getTemplate().getConfigurable());
        return templateElement.getRootTree();
    }
    @Override
    public String getFinallyBody() {
        String s = super.getBody();
        StringBuilder sb = new StringBuilder();
        int infina = 0;
        int incatch = 0;
        int intry = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == beginTag) {
                if (i + 5 < s.length()) {
                    String ts = s.substring(i + 1, i + 5);
                    if ("#try".equalsIgnoreCase(ts)) {
                        intry++;
                    }
                }
                if (intry == 0 && i + 7 < s.length()) {
                    String ts = s.substring(i + 1, i + 7);
                    if ("#catch".equalsIgnoreCase(ts)) {
                        incatch++;
                    }
                }
                if (intry == 0 && i + 9 < s.length()) {
                    String ts = s.substring(i + 1, i + 9);
                    if ("#finally".equalsIgnoreCase(ts)) {
                        infina++;
                    }
                }
            }
            if (incatch == 0 && infina > 0) {
                sb.append(c);
            }
            if (c == endTag) {
                if (i > 5) {
                    String ts = s.substring(i - 5, i);
                    if ("/#try".equalsIgnoreCase(ts)) {
                        intry--;
                    }
                }
                if (intry == 0 && i > 7) {
                    String ts = s.substring(i - 7, i);
                    if ("/#catch".equalsIgnoreCase(ts)) {
                        incatch--;
                    }
                }
                if (intry == 0 && i > 9) {
                    String ts = s.substring(i - 9, i);
                    if ("/#finally".equalsIgnoreCase(ts)) {
                        infina--;
                    }
                }
            }
        }
        return sb.substring(sb.indexOf(">") + 1, sb.lastIndexOf("<"));
    }
}
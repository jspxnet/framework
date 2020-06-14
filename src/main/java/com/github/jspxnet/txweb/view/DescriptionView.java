/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.view;

import com.github.jspxnet.lucene.ChineseAnalyzer;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.annotation.HttpMethod;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.HtmlUtil;
import com.github.jspxnet.utils.StringUtil;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2007-11-19
 * Time: 18:28:15
 * 生成择要
 */
@HttpMethod(caption = "生成简介或tag")
public class DescriptionView extends ActionSupport {
    private String content = StringUtil.empty;
    private int length = 200;
    private String type = StringUtil.empty;

    @Ref
    private ChineseAnalyzer chineseAnalyzer;

    public DescriptionView() {

    }

    public int getLength() {
        return length;
    }

    @Param(caption = "限制长度")
    public void setLength(int length) {
        this.length = length;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTags() throws IOException {
        return chineseAnalyzer.getTag(HtmlUtil.deleteHtml(content), " ", 6, true);
    }

    public String getDescription() {
        return HtmlUtil.deleteHtml(StringUtil.replace(content, "\n", StringUtil.empty), length, "..");
    }

    public String getType() {
        return type;
    }

    @Param(caption = "类型(tags)", max = 10)
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String execute() throws Exception {
        PrintWriter pw = response.getWriter();
        if ("tags".equalsIgnoreCase(type)) {
            pw.print(getTags());
        } else {
            pw.print(getDescription());
        }
        pw.flush();
        pw.close();
        return NONE;
    }
}
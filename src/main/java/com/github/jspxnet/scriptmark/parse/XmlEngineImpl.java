/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.parse;

import com.github.jspxnet.scriptmark.config.TemplateConfigurable;
import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.scriptmark.ScriptmarkEnv;
import com.github.jspxnet.scriptmark.Configurable;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.core.TemplateElement;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.XMLUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-14
 * Time: 14:54:46
 * 用来解析XML
 */
public class XmlEngineImpl implements XmlEngine {
    private final Configurable configurable = new TemplateConfigurable();
    private String tag;

    public XmlEngineImpl() {
        //不解析tag
        configurable.getTagMap().clear();
        //////不使用扩展方式
        configurable.put(ScriptmarkEnv.htmlExtType, false);
    }

    @Override
    public void putTag(String tag, String className) {
        configurable.setTag(tag, className);
        this.tag = tag;
    }

    @Override
    public String removeTag(String tag) {
        this.tag = StringUtil.empty;
        return configurable.removeTag(tag);
    }

    @Override
    public List<TagNode> getTagNodes(String str) throws Exception {
        TemplateElement templateElement = new TemplateElement(XMLUtil.deleteExegesis(str), 0, configurable);
        return templateElement.getRootTree();
    }

    @Override
    public TagNode createTagNode(String str) throws Exception {
        TemplateElement templateElement = new TemplateElement(XMLUtil.deleteExegesis(str), 0, configurable);
        List<TagNode> list = templateElement.getRootTree();
        for (TagNode tagNode : list) {
            if (tagNode.getTagName().equalsIgnoreCase(tag)) {
                return tagNode;
            }
        }
        list.clear();
        return null;
    }
}
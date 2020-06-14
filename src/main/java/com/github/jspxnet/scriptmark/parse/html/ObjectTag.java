/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.parse.html;

import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.scriptmark.util.ScriptMarkUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-1-16
 * Time: 上午12:13
 */
public class ObjectTag extends TagNode {
    public ObjectTag() {

    }

    public String getData() {
        return ScriptMarkUtil.deleteQuote(getStringAttribute("data"));
    }

    public String getWidth() {
        return ScriptMarkUtil.deleteQuote(getStringAttribute("width"));
    }

    public String getHeight() {
        return ScriptMarkUtil.deleteQuote(getStringAttribute("height"));
    }

    public List<TagNode> getObjectTags() throws Exception {
        return getObjectTags(getBody());
    }

    public List<TagNode> getObjectTags(String body) throws Exception {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag("object", ObjectTag.class.getName());
        return xmlEngine.getTagNodes(body);
    }

}
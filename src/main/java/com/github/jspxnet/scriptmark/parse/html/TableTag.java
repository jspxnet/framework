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

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2009-4-27
 * Time: 16:01:20
 */
public class TableTag extends TagNode {
    public TableTag() {

    }

    public List<TagNode> getTdTags() throws Exception {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag("tr", TrTag.class.getName());
        return xmlEngine.getTagNodes(getBody());
    }
}
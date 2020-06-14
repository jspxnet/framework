/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sioc.tag;

import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.utils.XMLUtil;

import java.util.List;

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.scriptmark.XmlEngine;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-12
 * Time: 0:42:50
 * <pre>
 * [list name="XXXList" class="integer"]
 * [value]2[/value]
 * [value]2[/value]
 * [/list]
 * </pre>
 */

public class ListElement extends IocTagNode {
    public final static String TAG_NAME = "list";

    public ListElement() {

    }

    public String getClassName() {
        return XMLUtil.deleteQuote(getStringAttribute(Sioc.IocClass));
    }

    public List<TagNode> getValueList() throws Exception {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag("value", ValueElement.class.getName());
        return xmlEngine.getTagNodes(getBody());
    }

}
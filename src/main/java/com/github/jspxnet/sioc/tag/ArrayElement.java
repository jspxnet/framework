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
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.scriptmark.XmlEngine;

import java.util.List;

import com.github.jspxnet.utils.XMLUtil;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-12
 * Time: 16:45:13
 * [array name="" class=""]
 * [value]2[/value]
 * [value class="int"]2[/value]
 * [/array]
 */
public class ArrayElement extends IocTagNode {
    public final static String TAG_NAME = "array";

    public ArrayElement() {

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
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

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.scriptmark.XmlEngine;

import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.sioc.Sioc;

import java.util.List;

import com.github.jspxnet.utils.XMLUtil;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-12
 * Time: 15:06:29
 * [sioc namespace="global"/]
 */
public class SiocElement extends TagNode {
    public final static String TAG_NAME = "sioc";

    public SiocElement() {

    }

    public String getNamespace() {
        String namespace = XMLUtil.deleteQuote(getStringAttribute("name"));
        if (StringUtil.isNull(namespace)) {
            namespace = XMLUtil.deleteQuote(getStringAttribute("namespace"));
        }
        if (StringUtil.isNull(namespace)) {
            namespace = Sioc.global;
        }
        return namespace;
    }

    public String getApplication() {
        return XMLUtil.deleteQuote(getStringAttribute("application"));
    }

    public String getExtends() {
        String extend = XMLUtil.deleteQuote(getStringAttribute("extends"));
        if (StringUtil.isNull(extend)) {
            extend = StringUtil.empty;
        }
        return extend;
    }

    public List<TagNode> getBeanElements() throws Exception {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag(BeanElement.TAG_NAME, BeanElement.class.getName());
        return xmlEngine.getTagNodes(getBody());
    }

    public List<TagNode> getIncludeElements() throws Exception {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag(IncludeElement.TAG_NAME, IncludeElement.class.getName());
        return xmlEngine.getTagNodes(getBody());
    }

    public List<TagNode> getScanElements() throws Exception {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag(ScanElement.TAG_NAME, ScanElement.class.getName());
        return xmlEngine.getTagNodes(getBody());
    }

}
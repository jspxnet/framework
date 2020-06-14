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

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-12
 * Time: 0:53:52
 * <p>
 * [map name="xx"]
 * [value key="one"]1[/value]
 * [value key="two" ref="false"]2[/value]
 * [/map]
 */
public class MapElement extends IocTagNode {
    public final static String TAG_NAME = "map";

    public MapElement() {

    }

    public List<TagNode> getValueList() throws Exception {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag("value", ValueElement.class.getName());
        return xmlEngine.getTagNodes(getBody());
    }

}
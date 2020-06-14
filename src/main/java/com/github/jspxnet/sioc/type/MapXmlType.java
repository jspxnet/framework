/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sioc.type;

import com.github.jspxnet.sioc.util.TypeUtil;
import com.github.jspxnet.sioc.tag.MapElement;
import com.github.jspxnet.sioc.tag.ValueElement;
import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.utils.StringUtil;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-3-22
 * Time: 12:53:57
 */

public class MapXmlType extends TypeSerializer {
    @Override
    public String getTypeString() {
        return "map";
    }

    @Override
    public Object getTypeObject() throws Exception {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag(MapElement.TAG_NAME, MapElement.class.getName());
        MapElement mapElement = (MapElement) xmlEngine.createTagNode((String) value);
        List<TagNode> valueList = mapElement.getValueList();
        Map<Object, Object> result = new LinkedHashMap<Object, Object>();
        for (TagNode node : valueList) {
            ValueElement valueElement = (ValueElement) node;
            result.put(valueElement.getKey(), TypeUtil.getTypeValue(valueElement.getClassName(), valueElement.getValue()));
        }
        return result;
    }

    @Override
    public String getXmlString() {
        Map theMap = (Map) value;
        if (theMap == null || theMap.isEmpty()) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<map name=\"").append(name).append("\">\r\n");
        for (Object o : theMap.keySet()) {
            String keys = (String) o;
            Object object = theMap.get(keys);
            String typeString = TypeUtil.getTypeString(object.getClass());
            sb.append("<value key=\"").append(keys).append("\" class=\"").append(typeString).append("\">").append(object).append("</value>\r\n");
        }
        sb.append("</map>\r\n");
        return sb.toString();
    }
}
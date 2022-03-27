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
import com.github.jspxnet.sioc.tag.ArrayElement;
import com.github.jspxnet.sioc.tag.ValueElement;
import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.utils.StringUtil;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-3-22
 * Time: 11:47:04
 * [array name="" class=""]
 * [value]2[/value]
 * [value class="int"]2[/value]
 * [/array]
 */
public class ArrayXmlType extends TypeSerializer {

    @Override
    public Type getJavaType()
    {
        return Object[].class;
    }

    @Override
    public String getTypeString() {
        return "array";
    }

    /**
     * 返回证真的数据
     *
     * @return Object
     */
    @Override
    public Object getTypeObject() throws Exception {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag("array", ArrayElement.class.getName());
        ArrayElement arrayElement = (ArrayElement) xmlEngine.createTagNode((String) value);
        String typeString = arrayElement.getClassName();
        List<TagNode> valueList = arrayElement.getValueList();
        Object[] result = TypeUtil.createArray(typeString, valueList.size());
        for (int i = 0; i < valueList.size(); i++) {
            ValueElement valueElement = (ValueElement) valueList.get(i);
            result[i] = TypeUtil.getTypeValue(typeString, valueElement.getValue());
        }
        valueList.clear();
        return result;
    }

    /**
     * 返回XML结果
     *
     * @return String
     */
    @Override
    public String getXmlString() {
        Object[] theValue = (Object[]) value;
        if (theValue == null || theValue.length < 1) {
            return StringUtil.empty;
        }
        StringBuilder sb = new StringBuilder();

        sb.append("<array name=\"").append(name).append("\" class=\"").append(TypeUtil.getTypeString(theValue[0].getClass())).append("\">\r\n");
        for (Object o : theValue) {
            sb.append("<value>").append(o).append("</value>\r\n");
        }
        sb.append("</array>\r\n");
        return sb.toString();
    }
}
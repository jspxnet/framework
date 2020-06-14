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

import com.github.jspxnet.sioc.util.TypeUtil;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.io.CharArrayWriter;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-25
 * Time: 18:27:16
 * 必须跳开list,map,array
 */
public class ReadProperty extends DefaultHandler {
    private List<PropertyElement> list = new LinkedList<PropertyElement>();
    private CharArrayWriter contents = new CharArrayWriter();
    private PropertyElement propertyElement = null;
    private final String[] jumpType = new String[]{"array", "list", "map"};

    private boolean jump = false;


    public ReadProperty() {

    }

    @Override
    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes attr) {
        contents.reset();

        if (ArrayUtil.inArray(jumpType, localName, true)) {
            jump = true;
        }
        if (!jump && TypeUtil.isBaseType(localName)) {
            propertyElement = new PropertyElement();
            propertyElement.setTypeName(localName);
            propertyElement.setName(attr.getValue("name"));
            if (!StringUtil.isNull(attr.getValue("value"))) {
                propertyElement.setValue(attr.getValue("value"));
            }
        }

    }

    @Override
    public void endElement(String namespaceURI,
                           String localName,
                           String qName) {
        if (!jump && TypeUtil.isBaseType(localName)) {
            propertyElement.setValue(contents.toString().trim());
            list.add(propertyElement);
        }
        if (ArrayUtil.inArray(jumpType, localName, true)) {
            jump = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        contents.write(ch, start, length);
    }

    public List<PropertyElement> getPropertyElements() {
        return list;
    }
}
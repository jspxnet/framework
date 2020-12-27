/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.utils;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;

import java.io.CharArrayWriter;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2008-11-12
 * Time: 20:30:51
 * 读取基本的XML
 */
public class BaseXML extends DefaultHandler {
    private String result;
    private String attributesResult;
    private String Key;
    private String Att;

    private CharArrayWriter contents = new CharArrayWriter();

    public BaseXML(String key, String Att) {
        this.Key = key;
        this.Att = Att;
    }

    @Override
    public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) {
        contents.reset();
        if (Key != null && Key.equalsIgnoreCase(localName)) {
            attributesResult = attributes.getValue(Att);
        }
    }

    @Override
    public void endElement(String namespaceURI,
                           String localName,
                           String qName) throws SAXException {
        if (Key != null && Key.equalsIgnoreCase(localName)) {
            result = contents.toString();
        }

    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        contents.write(ch, start, length);
    }

    public String getValue() {
        return result;
    }

    public String getAttributes() {
        return attributesResult;
    }
}
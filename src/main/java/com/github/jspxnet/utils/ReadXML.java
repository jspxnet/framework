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
 * date: 2004-12-27
 * Time: 16:30:12
 */
public class ReadXML extends DefaultHandler {
    private String Key = StringUtil.empty;
    private String value = StringUtil.empty;
    private boolean result = false;

    private final CharArrayWriter contents = new CharArrayWriter();

    public ReadXML(String key, String value) {
        this.Key = key;
        this.value = value;
    }

    @Override
    public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes)
            throws SAXException {
        contents.reset();
    }

    @Override
    public void endElement(String namespaceURI,
                           String localName,
                           String qName) throws SAXException {
        if (result) {
            return;
        }
        if (Key.equalsIgnoreCase(localName)) {
            if (contents.toString().equalsIgnoreCase(value)) {
                result = true;
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (result) {
            return;
        }
        contents.write(ch, start, length);
    }

    public boolean getValue() {
        return result;
    }
}
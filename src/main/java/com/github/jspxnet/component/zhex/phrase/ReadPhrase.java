/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.component.zhex.phrase;


import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.List;
import java.util.ArrayList;
import java.io.CharArrayWriter;

import com.github.jspxnet.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: 陈原
 * date: 2007-10-4
 * Time: 15:55:16
 */
public class ReadPhrase extends DefaultHandler {
    //命名空间,空间中的action MAP

    private CharArrayWriter contents = new CharArrayWriter();
    private List<Phrase> phraseList = new ArrayList<Phrase>();
    private Phrase phrase = new Phrase();
    private String q = StringUtil.empty;


    public ReadPhrase(String str) {
        q = str;
    }


    @Override
    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes attr) throws SAXException {
        contents.reset();
    }

    @Override
    public void endElement(String namespaceURI,
                           String localName,
                           String qName) throws SAXException {


        if (phraseList.size() < 10) {

            if ("ID".equalsIgnoreCase(localName)) {
                phrase.setId(contents.toString());
            }
            if ("word".equalsIgnoreCase(localName)) {
                phrase.setWord(contents.toString());
            }
            if ("py".equalsIgnoreCase(localName)) {
                phrase.setPy(contents.toString());
            }
            if ("explain".equalsIgnoreCase(localName)) {
                phrase.setExplain(contents.toString());
            }
            if ("example".equalsIgnoreCase(localName)) {
                phrase.setExample(contents.toString());
            }
            if ("jp".equalsIgnoreCase(localName)) {
                phrase.setJp(contents.toString());
            }

            if (!StringUtil.isNull(phrase.getJp()) && !StringUtil.isNull(phrase.getWord())) {
                if (phrase.getWord().contains(q)) {

                    phraseList.add(phrase);
                    phrase = new Phrase();
                } else if (phrase.getJp().contains(q)) {
                    phraseList.add(phrase);
                    phrase = new Phrase();
                }

            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        contents.write(ch, start, length);
    }

    public List<Phrase> getPhraseList() {
        return phraseList;
    }

}
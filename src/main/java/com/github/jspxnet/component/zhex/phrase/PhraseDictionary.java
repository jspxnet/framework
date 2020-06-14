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

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.XMLUtil;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-3-7
 * Time: 下午3:00
 */
public class PhraseDictionary {
    static public List<Phrase> getPhraseList(String find) throws IOException {
        if (StringUtil.isNull(find) || find.length() < 1) {
            return new ArrayList<>();
        }
        ReadPhrase readPhrase = new ReadPhrase(find);
        InputSource source = new InputSource();
        source.setEncoding(Environment.defaultEncode);
        source.setByteStream(ReadPhrase.class.getResourceAsStream("cyzd.xml"));
        XMLUtil.parseXmlInputSource(readPhrase, source);
        return readPhrase.getPhraseList();
    }
}
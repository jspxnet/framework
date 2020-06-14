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

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import org.xml.sax.SAXException;

import java.util.List;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: 陈原
 * date: 2007-10-4
 * Time: 15:37:59
 * 成语字典
 * "cyzd.jzb!//cyzd.xml";
 */
public class PhraseDictionaryView extends ActionSupport {
    public PhraseDictionaryView() {

    }

    public List<Phrase> getPhraseList(@Param String find) throws IOException, SAXException {
        return getPhrases(find);
    }


    public List<Phrase> getPhrases(@Param String find) throws IOException, SAXException {
        return PhraseDictionary.getPhraseList(find);
    }

}
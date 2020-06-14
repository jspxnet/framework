/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.view;

import com.github.jspxnet.io.AbstractRead;
import com.github.jspxnet.io.AutoReadTextFile;
import com.github.jspxnet.io.ReadHtml;
import com.github.jspxnet.io.StringInputStream;
import com.github.jspxnet.txweb.annotation.HttpMethod;

import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.XMLUtil;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndEntry;

import java.util.List;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-10-4
 * Time: 4:53:40
 * 读取RRS
 * com.github.jspxnet.txweb.view.RRSReadView
 */
@HttpMethod(caption = "读取RRS")
public class RRSReadView extends ActionSupport {
    private SyndFeedInput syndFeedInput = new SyndFeedInput();
    private SyndFeed syndFeed = null;
    private String url = StringUtil.empty;
    private AbstractRead read;

    public String getUrl() {
        return url;
    }

    @Param(caption = "URL")
    public void setUrl(String url) {
        this.url = url;

        if (url.toLowerCase().startsWith("http")) {
            read = new ReadHtml();
            read.setFile(url);
        } else {
            read = new AutoReadTextFile();
            read.setFile(new File(url));
        }
    }

    public SyndFeed getSyndFeed() throws Exception {
        String cont = read.getContent();
        String encode = XMLUtil.getHeadEncode(cont);
        StringInputStream inputStream = new StringInputStream(cont, encode);
        XmlReader reader = new XmlReader(inputStream);
        return syndFeed = syndFeedInput.build(reader);
    }

    @Operate(caption = "得到SyndEntry")
    public List<SyndEntry> getSyndEntrys() throws Exception {
        if (syndFeed == null) {
            syndFeed = getSyndFeed();
        }
        return (List<SyndEntry>) syndFeed.getEntries();
    }
}
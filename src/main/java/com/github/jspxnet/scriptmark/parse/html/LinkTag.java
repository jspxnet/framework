/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.parse.html;

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.util.ScriptMarkUtil;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-1-14
 * Time: 下午3:05
 * [link href="newyearcss.css" rel="stylesheet" type="text/css" /]
 */

public class LinkTag extends TagNode {
    public LinkTag() {

    }

    public String getHref() {
        return ScriptMarkUtil.deleteQuote(getStringAttribute("href"));
    }

    public String getRel() {
        return ScriptMarkUtil.deleteQuote(getStringAttribute("rel"));
    }

    public String getType() {
        return ScriptMarkUtil.deleteQuote(getStringAttribute("type"));
    }
}
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
 * date: 2008-12-23
 * Time: 10:12:10
 */
public class MetaTag extends TagNode {
    public MetaTag() {
        repair = true;
    }

    public String getScheme() {
        return ScriptMarkUtil.deleteQuote(getStringAttribute("scheme"));
    }

    public String getHttpEquiv() {
        return ScriptMarkUtil.deleteQuote(getStringAttribute("http-equiv"));
    }

    public String getName() {
        return ScriptMarkUtil.deleteQuote(getStringAttribute("name"));
    }

    public String getContent() {
        return ScriptMarkUtil.deleteQuote(getStringAttribute("content"));
    }
}
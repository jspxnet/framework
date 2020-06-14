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
 * date: 11-1-19
 * Time: 下午5:56
 */
public class IframeTag extends TagNode {
    public IframeTag() {

    }

    public String getId() {
        return ScriptMarkUtil.deleteQuote(getStringAttribute("id"));
    }

    public String getSrc() {
        return ScriptMarkUtil.deleteQuote(getStringAttribute("src"));
    }

    public String getScrolling() {
        return ScriptMarkUtil.deleteQuote(getStringAttribute("scrolling"));
    }
}
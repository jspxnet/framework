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

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.XMLUtil;

/**
 * Created by IntelliJ IDEA.
 *
  * author: chenYuan
 * date: 12-1-4
 * Time: 下午12:08
 */
public abstract class IocTagNode extends TagNode {

    public final static String KEY_NAME = "name";

    public String getId() {
        String name = getStringAttribute(KEY_NAME);
        if (!StringUtil.hasLength(name)) {
            name = getStringAttribute("id");
        }
        return XMLUtil.deleteQuote(name);
    }
}
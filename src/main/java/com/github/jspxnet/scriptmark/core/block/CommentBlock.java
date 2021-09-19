/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.core.block;

import com.github.jspxnet.scriptmark.core.TagNode;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-29
 * Time: 7:14:19
 * 注释格式
 * <pre>{@code
 * <!--#注释说明#-->
 * }</pre>
 */
public class CommentBlock extends TagNode {
    final static public String NOTE_TAG_BEGIN = "!--#";

    final static public String NOTE_TAG_END = "#--";

    public CommentBlock() {
        super.setTagName(NOTE_TAG_BEGIN);
    }
}
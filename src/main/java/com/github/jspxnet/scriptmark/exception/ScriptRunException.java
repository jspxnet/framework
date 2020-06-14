/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.exception;

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 12-3-2
 * Time: 下午2:17
 */
public class ScriptRunException extends Exception {

    private TagNode tagNode;
    private String message;

    public ScriptRunException(TagNode tagNode, String s) {
        super(s);
        this.tagNode = tagNode;
        message = s;
    }

    public TagNode getTagNode() {
        return tagNode;
    }

    public void setTagNode(TagNode tagNode) {
        this.tagNode = tagNode;
    }

    @Override
    public String getMessage() {
        if (StringUtil.hasLength(message)) {
            return message;
        }
        return super.getMessage();
    }

}
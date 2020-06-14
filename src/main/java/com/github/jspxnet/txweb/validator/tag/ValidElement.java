/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.validator.tag;

import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.XMLUtil;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2009-1-27
 * Time: 22:50:53
 * [validation dataType="isGoodName" url="server/url/check.action" field="name" msgId="nameMsg" needed="true"]
 * [message]会员登录名4-20个英文字母或数字组成(或2-10个中文)。不能使用符号。[/message]
 * [error]错误,长度不正确或者使用了特殊符号[/error]
 * [success]验证通过[/success]
 * [/validation]
 */
public class ValidElement extends TagNode {
    public ValidElement() {

    }

    //调用的类Action中的配置
    public String getField() {
        return XMLUtil.deleteQuote(getStringAttribute("field"));
    }

    public String getNoteId() {
        String msg = getStringAttribute("noteId");
        return XMLUtil.deleteQuote(msg);
    }

    public String getDataType() {
        return XMLUtil.deleteQuote(getStringAttribute("dataType"));
    }

    public String getUrl() {
        String result = XMLUtil.deleteQuote(getStringAttribute("url"));
        if (StringUtil.isNull(result)) {
            return StringUtil.empty;
        }
        return result;
    }

    public boolean isRequired() {
        String need = XMLUtil.deleteQuote(getStringAttribute("required"));
        if (!StringUtil.hasLength(need)) {
            need = getStringAttribute("required");
        }
        return StringUtil.toBoolean(XMLUtil.deleteQuote(need));
    }

    public String getError()  {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag("error", InfoElement.class.getName());
        TagNode node = null;
        try {
            node = xmlEngine.createTagNode(getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (node == null) {
            return StringUtil.empty;
        }
        return node.getBody();
    }

    public String getNote() throws Exception {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag("note", InfoElement.class.getName());
        TagNode node = xmlEngine.createTagNode(getBody());
        if (node == null) {
            return StringUtil.empty;
        }
        return node.getBody();
    }
}

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

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.txweb.validator.DataTypeValidator;
import com.github.jspxnet.utils.XMLUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2009-1-27
 * Time: 22:55:04
 * [validator formId="adduserForm"]
 * [/validator]
 */
public class ValidatorElement extends TagNode {

    public ValidatorElement() {

    }

    //调用的类Action中的配置
    public String getId() {
        return XMLUtil.deleteQuote(getStringAttribute("id"));
    }

    public List<TagNode> getValidElements() throws Exception {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag(DataTypeValidator.TAG_VALIDATION, ValidElement.class.getName());
        return xmlEngine.getTagNodes(getBody());
    }
}
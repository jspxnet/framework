/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.support;

import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.enums.SafetyEnumType;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.txweb.validator.DataTypeValidator;
import com.github.jspxnet.txweb.validator.Validator;
import com.github.jspxnet.utils.FileUtil;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-5-19
 * Time: 22:29:15
 */
@HttpMethod(caption = "数据验证")
public class ValidatorAction extends ActionSupport {
    final private Validator processor = new DataTypeValidator();
    private String type = "json";

    public ValidatorAction() {

    }

    @Param(request = false)
    public void setConfigFile(String configFile) {
        processor.setConfigFile(configFile);
    }

    public String getConfigFile() {
        return processor.getConfigFile();
    }

    @Param(max = 220, level = SafetyEnumType.MIDDLE)
    public void setId(String formId) {
        if (formId.contains("/")) {
            formId = FileUtil.getFileNamePart(formId);
        }
        processor.setId(formId);
    }

    public String getFormId() {
        return processor.getId();
    }

    public String getXML() throws Exception {
        return processor.getXML();
    }

    public String getJson() throws Exception {
        return processor.getJson();
    }

    public Validator getProcessor() {
        return processor;
    }

    public String getType() {
        return type;
    }

    @Param(max = 20, level = SafetyEnumType.MIDDLE)
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String execute() throws Exception {
        processor.setEncode(Dispatcher.getEncode());
        if (WebOutEnumType.JSON.getValue()==WebOutEnumType.find(type).getValue()) {
            TXWebUtil.print(getJson(), WebOutEnumType.JSON.getValue(), response);
        } else {
            TXWebUtil.print("<?xml version=\"1.0\" encoding=\"" + processor.getEncode() + "\"?>\r\n" + getXML(), WebOutEnumType.XML.getValue(), response);
        }
        return NONE;
    }
}
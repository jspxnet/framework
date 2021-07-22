/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.bundle.action;

import com.github.jspxnet.boot.res.LanguageRes;
import lombok.extern.slf4j.Slf4j;


import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.view.ConfigView;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-9-24
 * Time: 15:15:50
 * com.github.jspxnet.txweb.bundle.action.EditConfigAction
 */
@Slf4j
@HttpMethod(caption = "配置")
public class EditConfigAction extends ConfigView {

    private int encrypt = 0;

    public EditConfigAction() {

    }

    public void setEncrypt(int encrypt) {
        this.encrypt = encrypt;
    }

    //校验infoType为检查后保存到FieldInfo中的信息类型
    //dataTypeValidator为sioc配置  formId 为检验配置的ID,
    //提交表单中 post 不为空才运行校验
    //@Validate(id = "@formId")
    //提交表单中 post 不为空才运行 execute 方法
    //作用  if (hasFieldInfo())  save();
    @Operate(caption = "保存")
    public void save() throws Exception {
        boolean isSave = false;
        StringBuilder sb = new StringBuilder();
        String[] noSaveArray = {"post", "submit", "formId"};
        java.util.Enumeration<java.lang.String> enumeration = request.getParameterNames();
        try {
            while (enumeration.hasMoreElements()) {
                String key = enumeration.nextElement();
                if (ArrayUtil.inArray(noSaveArray, key, true)) {
                    continue;
                }
                String value = getString(key);
                if (value != null) {
                    isSave = config.save(key, value, encrypt);
                    sb.append(key).append("=").append(value).append(StringUtil.CRLF);
                    if (!isSave) {
                        addFieldInfo(key, language.getLang(LanguageRes.saveFailure) + ":" + key + "=" + value);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("保存配置发生异常", e);
        }
        if (isSave) {
            setActionLogTitle(language.getLang(LanguageRes.modifyConfig));
            setActionLogContent("namespace=" + config.getNamespace() + " dataType=" + config.getDataType() + " " + sb.toString());
            addActionMessage(language.getLang(LanguageRes.saveSuccess));
        }
        config.flush();
   }


}
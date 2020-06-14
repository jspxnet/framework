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
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.ArrayUtil;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-5-24
 * Time: 13:51:44
 */
@Slf4j
@HttpMethod(caption = "语言")
public class EditLanguageAction extends ActionSupport {
    public EditLanguageAction() {

    }


    //提交表单中 post 不为空才运行 execute 方法
    //作用  if (hasFieldInfo())  save();
    @Operate(caption = "保存")
    public void save() throws Exception {
        boolean isSave = false;
        String[] noSaveArray = {"post", "submit", "formId"};
        java.util.Enumeration<java.lang.String> enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            if (ArrayUtil.inArray(noSaveArray, key, true)) {
                continue;
            }
            String value = getString(key);
            if (value != null) {
                try {
                    isSave = language.save(key, value);
                } catch (Exception e) {
                    log.error(key + "=" + value, e);
                }
                if (!isSave) {
                    addFieldInfo(key, "保存失败:" + key + "=" + value);
                    break;
                }
            }
        }
        if (isSave) {
            setActionLogTitle("修改语言");
            setActionLogContent("namespace=" + language.getNamespace() + " dataType=" + language.getDataType());
            addActionMessage(language.getLang(LanguageRes.saveSuccess));
        }
        language.flush();
    }

}
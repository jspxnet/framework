/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.action;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.table.MemberBundle;
import com.github.jspxnet.txweb.view.UserBundleView;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.HtmlUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 12-3-28
 * Time: 下午5:28
 * 保存用户自己的偏好设置
 */
@Slf4j
@HttpMethod(caption = "个人配置")
public class UserBundleAction extends UserBundleView {

    public UserBundleAction() {

    }

    //提交表单中 post 不为空才运行 execute 方法
    //作用  if (hasFieldInfo())  save();
    @Operate(caption = "保存")
    public void write() throws Exception {

        if (isGuest()) {

            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needLogin));
            setActionResult(MESSAGE);
            return;
        }

        IUserSession userSession = getUserSession();
        int i = 0;
        boolean isSave = false;
        String[] noSaveArray = {"post", "submit", "formId", "method", "save"};
        java.util.Enumeration<java.lang.String> enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            if (ArrayUtil.inArray(noSaveArray, key, true)) {
                continue;
            }
            String value = getString(key);
            if (StringUtil.hasLength(value)) {
                try {
                    MemberBundle memberBundle = new MemberBundle();
                    memberBundle.setIdx(key);
                    memberBundle.setContext(HtmlUtil.deleteHtml(value));
                    memberBundle.setPutName(userSession.getName());
                    memberBundle.setPutUid(userSession.getUid());
                    memberBundle.setIp(getRemoteAddr());
                    isSave = userBundleDAO.save(memberBundle);
                    i++;
                    if (i > 100) {
                        addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.notAllowedOperation));
                        break;
                    }
                } catch (Exception e) {
                    log.error(key + "=" + value, e);
                }
                if (!isSave) {
                    addFieldInfo(key, language.getLang(LanguageRes.saveFailure) + ":" + key + "=" + value);
                    break;
                }
            }
        }

        if (isSave) {
            setActionLogTitle(language.getLang(LanguageRes.modifyUserConfig));
            setActionLogContent("namespace=" + userBundleDAO.getNamespace() + " 用户名=" + userSession.getName() + " uid=" + userSession.getUid());
            addActionMessage(language.getLang(LanguageRes.saveSuccess));
        }
        userBundleDAO.evict(MemberBundle.class);
    }

}
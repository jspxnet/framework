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
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.view.TemplateView;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.component.zhex.spell.ChineseUtil;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: yuan
 * date: 12-11-30
 * Time: 下午11:53
 */
@Deprecated
@HttpMethod(caption = "默认页面管理")
public class TemplateAction extends TemplateView {

    public TemplateAction() {

    }


    @Operate(caption = "保存")
    public void save() throws Exception {
        Object obj = getBean(templateDAO.getClassType());
        IUserSession userSession = getUserSession();
        if (userSession != null) {
            BeanUtil.setSimpleProperty(obj, "putName", userSession.getName());
            BeanUtil.setSimpleProperty(obj, "putUid", userSession.getUid());
        }

        BeanUtil.setSimpleProperty(obj, "ip", getRemoteAddr());
        Method method = ClassUtil.getSetMethod(templateDAO.getClassType(), "namespace");
        if (method != null) {
            BeanUtil.setSimpleProperty(obj, method.getName(), templateDAO.getNamespace());
        }
        Method spellingMethod = ClassUtil.getSetMethod(templateDAO.getClassType(), "spelling");
        if (spellingMethod != null) {
            if (ClassUtil.isDeclaredMethod(templateDAO.getClassType(), "getTitle")) {
                String name = (String) BeanUtil.getProperty(obj, "title");
                BeanUtil.setSimpleProperty(obj, spellingMethod.getName(), ChineseUtil.getFullSpell(name, ""));
            } else if (ClassUtil.isDeclaredMethod(templateDAO.getClassType(), "getName")) {
                String name = (String) BeanUtil.getProperty(obj, "name");
                BeanUtil.setSimpleProperty(obj, spellingMethod.getName(), ChineseUtil.getFullSpell(name, ""));
            }
        }
        if (templateDAO.save(obj) > 0) {
            addActionMessage(language.getLang(LanguageRes.saveSuccess));
        } else {
            addActionMessage(language.getLang(LanguageRes.saveFailure));
        }
    }

    @Operate(caption = "编辑")
    public void update() throws Exception {

        Object obj = getBean(templateDAO.getClassType());
        IUserSession userSession = getUserSession();
        if (userSession != null) {
            BeanUtil.setSimpleProperty(obj, "putName", userSession.getName());
            BeanUtil.setSimpleProperty(obj, "putUid", userSession.getUid());
        }
        Method method = ClassUtil.getSetMethod(templateDAO.getClassType(), "namespace");
        if (method != null) {
            BeanUtil.setSimpleProperty(obj, method.getName(), templateDAO.getNamespace());
        }
        Method spellingMethod = ClassUtil.getSetMethod(templateDAO.getClassType(), "spelling");
        if (spellingMethod != null) {
            if (ClassUtil.isDeclaredMethod(templateDAO.getClassType(), "getTitle")) {
                String name = (String) BeanUtil.getProperty(obj, "title");
                BeanUtil.setSimpleProperty(obj, spellingMethod.getName(), ChineseUtil.getFullSpell(name, ""));
            } else if (ClassUtil.isDeclaredMethod(templateDAO.getClassType(), "getName")) {
                String name = (String) BeanUtil.getProperty(obj, "name");
                BeanUtil.setSimpleProperty(obj, spellingMethod.getName(), ChineseUtil.getFullSpell(name, ""));
            }
        }
        BeanUtil.setSimpleProperty(obj, "ip", getRemoteAddr());
        if (templateDAO.update(obj) > 0) {
            addActionMessage(language.getLang(LanguageRes.updateSuccess));
        } else {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.updateFailure));
        }
    }


    @Operate(caption = "删除")
    public void delete(@Param(caption = "id列表") String[] ids) throws Exception {
        if (ArrayUtil.isEmpty(ids)) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needSelect));
            return;
        }
        if (templateDAO.delete(ids)) {
            addActionMessage(language.getLang(LanguageRes.deleteSuccess));
        } else {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.deleteFailure));
        }
    }

    @Operate(caption = "审核")
    public void auditing(@Param(caption = "id列表") String[] ids, @Param(caption = "审核") int auditingType) throws Exception {
        if (ArrayUtil.isEmpty(ids)) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needSelect));
            return;
        }

        for (Serializable id : ids) {
            Object obj = templateDAO.get(templateDAO.getClassType(), id);
            if (obj == null) {
                continue;
            }
            Method method = ClassUtil.getSetMethod(templateDAO.getClassType(), "auditingType");
            if (method == null) {
                continue;
            }
            BeanUtil.setSimpleProperty(obj, "auditingType", auditingType);
            BeanUtil.setSimpleProperty(obj, "auditingDate", new Date());
            if (templateDAO.update(obj, new String[]{"auditingType", "auditingDate"}) > 0) {
                addActionMessage(language.getLang(LanguageRes.operationSuccess));
            } else {
                addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.operationFailure));
            }
        }
    }

    @Operate(caption = "提前")
    public void sortDate(@Param(caption = "id列表") String[] ids) throws Exception {
        setActionResult(ROC);
        if (ArrayUtil.isEmpty(ids)) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needSelect));
            return;
        }
        boolean del = templateDAO.updateSortDate(ids);
        if (del) {
            addActionMessage(language.getLang(LanguageRes.operationSuccess));
        } else {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.operationFailure));
        }
    }

    @Operate(caption = "排序")
    public void sortType(@Param(caption = "id列表") String[] ids, @Param(caption = "排序") int sortType) throws Exception {

        if (ArrayUtil.isEmpty(ids)) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needSelect));
            return;
        }
        if (templateDAO.updateSortType(ids, sortType)) {
            addActionMessage(language.getLang(LanguageRes.operationSuccess));
        } else {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.operationFailure));
        }
    }

    @Operate(caption = "固顶")
    public void top(@Param(caption = "id列表") String[] ids) throws Exception {

        if (ArrayUtil.isEmpty(ids)) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needSelect));
            return;
        }
        if (templateDAO.updateSortType(ids, 2)) {
            addActionMessage(language.getLang(LanguageRes.operationSuccess));
        } else {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.operationFailure));
        }
    }

    @Operate(caption = "取消固顶")
    public void clearSortType(@Param(caption = "id列表") String[] ids) throws Exception {
        setActionResult(ROC);
        if (ArrayUtil.isEmpty(ids)) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needSelect));
            return;
        }
        if (templateDAO.updateSortType(ids, 0)) {
            addActionMessage(language.getLang(LanguageRes.operationSuccess));
        } else {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.operationFailure));
        }
    }

    @Override
    public String execute() throws Exception {
        if (isMethodInvoked()) {
            templateDAO.evict(templateDAO.getClassType());
        }
        return super.execute();
    }
}
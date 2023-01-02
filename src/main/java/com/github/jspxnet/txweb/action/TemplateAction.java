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
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.view.TemplateView;
import com.github.jspxnet.utils.*;
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
@HttpMethod(caption = "默认页面管理")
public class TemplateAction extends TemplateView {

    public TemplateAction() {

    }


    @Operate(caption = "保存")
    public RocResponse<?>  save() throws Exception {
        Object obj = getBean(templateDAO.getClassType());

        IUserSession userSession = getUserSession();
        if (userSession != null) {
            BeanUtil.setSimpleProperty(obj, "putName", userSession.getName());
            BeanUtil.setSimpleProperty(obj, "putUid", userSession.getUid());
        }
        TableModels tableModels = templateDAO.getSoberTable(templateDAO.getClassType());
        if (tableModels.containsField("ip"))
        {
            BeanUtil.setSimpleProperty(obj, "ip", getRemoteAddr());
        }

        if (tableModels.containsField("namespace"))
        {
            BeanUtil.setSimpleProperty(obj, "namespace", templateDAO.getNamespace());
        }

        if (tableModels.containsField("spelling"))
        {
            if (ClassUtil.isDeclaredMethod(templateDAO.getClassType(), "getTitle")) {
                String name = (String) BeanUtil.getProperty(obj, "title");
                BeanUtil.setSimpleProperty(obj, "spelling", ChineseUtil.getFullSpell(name, ""));
            } else if (ClassUtil.isDeclaredMethod(templateDAO.getClassType(), "getName")) {
                String name = (String) BeanUtil.getProperty(obj, "name");
                BeanUtil.setSimpleProperty(obj, "spelling", ChineseUtil.getFullSpell(name, ""));
            }
        }

        if (tableModels.containsField("ip"))
        {
            BeanUtil.setSimpleProperty(obj, "ip", getRemoteAddr());
        }

        String idName = tableModels.getPrimary();
        if (StringUtil.isNull(idName))
        {
            idName = "id";
        }


        Object id = BeanUtil.getProperty(obj,idName);

        if (ObjectUtil.isEmpty(id) || ObjectUtil.toInt(id)<=0)
        {
            if (templateDAO.save(obj) > 0) {
                return RocResponse.success(obj,language.getLang(LanguageRes.saveSuccess));
            } else {
                return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.saveFailure));
            }
        } else
        {
            if (templateDAO.update(obj) > 0) {
                return RocResponse.success(obj,language.getLang(LanguageRes.updateSuccess));
            } else {
                return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.updateFailure));
            }
        }
    }

    @Operate(caption = "删除")
    public RocResponse<String[]> delete(@Param(caption = "id列表",required = true) String[] ids) throws Exception {
        if (templateDAO.delete(ids)) {
            return RocResponse.success(ids,language.getLang(LanguageRes.deleteSuccess));
        } else {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.deleteFailure));
        }
    }

    @Operate(caption = "审核")
    public RocResponse<String[]> auditing(@Param(caption = "id列表",required = true) String[] ids, @Param(caption = "审核",required = true) int auditingType) throws Exception {
        String[] result = null;
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
                result = ArrayUtil.add(result,id);
                addActionMessage(language.getLang(LanguageRes.operationSuccess));
            } else {
                addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.operationFailure));
            }
        }
        if (hasFieldInfo())
        {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),getFailureMessage());
        } else
        {
            return RocResponse.success(result,getFailureMessage());
        }
    }

    @Operate(caption = "提前")
    public RocResponse<String[]> sortDate(@Param(caption = "id列表",required = true) String[] ids) throws Exception {
        boolean del = templateDAO.updateSortDate(ids);
        if (del) {
            return RocResponse.success(ids,language.getLang(LanguageRes.operationSuccess));
        } else {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.operationFailure));
        }
    }

    @Operate(caption = "排序")
    public RocResponse<String[]> sortType(@Param(caption = "id列表",required = true) String[] ids, @Param(caption = "排序",required = true) int sortType) throws Exception {
        if (templateDAO.updateSortType(ids, sortType)) {
            return RocResponse.success(ids,language.getLang(LanguageRes.operationSuccess));
        } else {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.operationFailure));
        }
    }

    @Operate(caption = "固顶")
    public RocResponse<String[]> top(@Param(caption = "id列表") String[] ids) throws Exception {
        if (ArrayUtil.isEmpty(ids)) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.needSelect));
        }
        if (templateDAO.updateSortType(ids, 2)) {
            return RocResponse.success(ids,language.getLang(LanguageRes.operationSuccess));
        } else {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.operationFailure));
        }
    }

    @Operate(caption = "取消固顶")
    public RocResponse<String[]> clearSortType(@Param(caption = "id列表") String[] ids) throws Exception {
        if (ArrayUtil.isEmpty(ids)) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.needSelect));
        }
        if (templateDAO.updateSortType(ids, 0)) {
            return RocResponse.success(ids,language.getLang(LanguageRes.operationSuccess));
        } else {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.operationFailure));
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
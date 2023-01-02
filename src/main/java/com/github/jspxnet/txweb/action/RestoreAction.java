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
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sober.Criteria;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: 陈原
 * date: 2007-11-1
 * Time: 14:40:49
 */
@HttpMethod(caption = "载入数据")
public class RestoreAction extends ActionSupport {

    ///////////////载入IOC DAO 对象 begin
    @Ref
    private GenericDAO genericDAO;
    ///////////////载入IOC DAO 对象 end

    private String loadXml = StringUtil.empty;

    public String getLoadXml() {
        return loadXml;
    }

    public void setLoadXml(String loadXml) {
        this.loadXml = loadXml;
    }

    private String backClass;

    public String getBackClass() {
        return backClass;
    }

    @Param(caption = "类名")
    public void setBackClass(String backClass) {
        this.backClass = backClass;
    }

    private String namespace = StringUtil.empty;

    public String getNamespace() {
        return namespace;
    }


    @Param(caption = "命名空间")
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    private Map<String, String> classNames = new HashMap<String, String>();

    public Map<String, String> getClassNames() {
        return classNames;
    }

    public void setClassNames(Map<String, String> classNames) {
        this.classNames = classNames;
    }

    @Operate(caption = "导入数据")
    public void save() throws Exception {
        if (StringUtil.isNull(loadXml)) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.notDataFind));
            return;
        }
        List<?> list = (List<?>) ObjectUtil.getForXml(loadXml);
        if (list == null || list.isEmpty()) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.notDataFind));
            return;
        }
        HttpServletRequest request = getRequest();
        if (StringUtil.isNull(namespace) || "auto".equalsIgnoreCase(namespace)) {
            String licenseVersion = getEnv(Environment.versionType);
            namespace = Environment.versionEnterprise.equalsIgnoreCase(licenseVersion) ? request.getServerName() : null;
            if (request.getServerName().startsWith("www.")) {
                namespace = null;
            }
        }

        Object o = list.get(0);
        Criteria criteria = genericDAO.createCriteria(o.getClass());
        Method method = ClassUtil.getSetMethod(o.getClass(), "namespace");
        if (method != null) {
            criteria = criteria.add(Expression.like("namespace", namespace + "%"));
        }
        criteria.delete(false);
        if (genericDAO.save(list) > 0) {
            addActionMessage(language.getLang(LanguageRes.operationSuccess));
        }
        genericDAO.evict(o.getClass());
        list.clear();
    }

    @Operate(caption = "备份数据")
    public void backup() throws Exception {
        HttpServletRequest request = getRequest();
        if (StringUtil.isNull(namespace) || "auto".equalsIgnoreCase(namespace)) {
            String licenseVersion = getEnv(Environment.versionType);
            namespace = Environment.versionEnterprise.equalsIgnoreCase(licenseVersion) ? request.getServerName() : null;
            if (request.getServerName().startsWith("www.")) {
                namespace = null;
            }
        }

        Class<?> cla = ClassUtil.loadClass(backClass);
        Criteria criteria = genericDAO.createCriteria(cla);
        if (!StringUtil.isNull(namespace) && ClassUtil.isDeclaredMethod(cla, "setNamespace")) {
            criteria = criteria.add(Expression.like("namespace", namespace + "%"));
        }
        criteria = criteria.setCurrentPage(1).setTotalCount(10000);
        setResult(criteria.list(false));
        setActionResult(XSTREAM);
    }
}
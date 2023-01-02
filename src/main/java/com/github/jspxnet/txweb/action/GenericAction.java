package com.github.jspxnet.txweb.action;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.component.zhex.spell.ChineseUtil;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.view.GenericView;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/4/5 22:36
 * description: jspbox
 **/
@HttpMethod(caption = "通用ACTION")
public class GenericAction  extends GenericView {

    @Operate(caption = "保存")
    public void save() throws Exception {

        Class<?> cls = ClassUtil.loadClass(className);
        Object obj = getBean(cls);
        IUserSession userSession = getUserSession();
        if (userSession != null) {
            BeanUtil.setSimpleProperty(obj, "putName", userSession.getName());
            BeanUtil.setSimpleProperty(obj, "putUid", userSession.getUid());
        }

        BeanUtil.setSimpleProperty(obj, "ip", getRemoteAddr());
        Method method = ClassUtil.getSetMethod(cls, "namespace");
        if (method != null) {
            BeanUtil.setSimpleProperty(obj, method.getName(), getRootNamespace());
        }
        Method spellingMethod = ClassUtil.getSetMethod(cls, "spelling");
        if (spellingMethod != null) {
            if (ClassUtil.isDeclaredMethod(cls, "getTitle")) {
                String name = (String) BeanUtil.getProperty(obj, "title");
                BeanUtil.setSimpleProperty(obj, spellingMethod.getName(), ChineseUtil.getFullSpell(name, ""));
            } else if (ClassUtil.isDeclaredMethod(cls, "getName")) {
                String name = (String) BeanUtil.getProperty(obj, "name");
                BeanUtil.setSimpleProperty(obj, spellingMethod.getName(), ChineseUtil.getFullSpell(name, ""));
            }
        }
        if (genericDAO.save(obj) > 0) {
            addActionMessage(language.getLang(LanguageRes.saveSuccess));
        } else {
            addActionMessage(language.getLang(LanguageRes.saveFailure));
        }
    }

    @Operate(caption = "编辑")
    public void update() throws Exception {

        Class<?> cls = ClassUtil.loadClass(className);
        Object obj = getBean(cls);
        IUserSession userSession = getUserSession();
        if (userSession != null) {
            BeanUtil.setSimpleProperty(obj, "putName", userSession.getName());
            BeanUtil.setSimpleProperty(obj, "putUid", userSession.getUid());
        }
        Method method = ClassUtil.getSetMethod(cls, "namespace");
        if (method != null) {
            BeanUtil.setSimpleProperty(obj, method.getName(), getRootNamespace());
        }
        Method spellingMethod = ClassUtil.getSetMethod(cls, "spelling");
        if (spellingMethod != null) {
            if (ClassUtil.isDeclaredMethod(cls, "getTitle")) {
                String name = (String) BeanUtil.getProperty(obj, "title");
                BeanUtil.setSimpleProperty(obj, spellingMethod.getName(), ChineseUtil.getFullSpell(name, ""));
            } else if (ClassUtil.isDeclaredMethod(cls, "getName")) {
                String name = (String) BeanUtil.getProperty(obj, "name");
                BeanUtil.setSimpleProperty(obj, spellingMethod.getName(), ChineseUtil.getFullSpell(name, ""));
            }
        }
        BeanUtil.setSimpleProperty(obj, "ip", getRemoteAddr());
        if (genericDAO.update(obj) > 0) {
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
        Class<?> cls = ClassUtil.loadClass(className);
        if (genericDAO.delete(cls,ids,false)>=0) {
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
        Class<?> cls = ClassUtil.loadClass(className);
        for (Serializable id : ids) {
            Object obj = genericDAO.get(cls, id);
            if (obj == null) {
                continue;
            }
            Method method = ClassUtil.getSetMethod(cls, "auditingType");
            if (method == null) {
                continue;
            }
            BeanUtil.setSimpleProperty(obj, "auditingType", auditingType);
            BeanUtil.setSimpleProperty(obj, "auditingDate", new Date());
            if (genericDAO.update(obj, new String[]{"auditingType", "auditingDate"}) > 0) {
                addActionMessage(language.getLang(LanguageRes.operationSuccess));
            } else {
                addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.operationFailure));
            }
        }
    }

    @Override
    public String execute() throws Exception {
        if (isMethodInvoked()) {
            Class<?> cls = ClassUtil.loadClass(className);
            genericDAO.evict(cls);
        }
        return super.execute();
    }
}


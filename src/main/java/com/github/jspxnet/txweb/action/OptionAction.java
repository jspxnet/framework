/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.action;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.component.zhex.spell.ChineseUtil;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.model.param.OptionBundleParam;
import com.github.jspxnet.txweb.table.OptionBundle;
import com.github.jspxnet.txweb.view.OptionListView;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by yuan on 14-2-15.
 * 字典
 */
@HttpMethod(caption = "字典管理")
public class OptionAction extends OptionListView {


    @Operate(caption = "保存")
    public void save(@Param("参数对象") OptionBundleParam param) throws Exception {
        OptionBundle optionBundle = BeanUtil.copy(param,OptionBundle.class);
        IUserSession userSession = getUserSession();
        if (userSession != null) {
            optionBundle.setPutName(userSession.getName());
            optionBundle.setPutUid(userSession.getUid());
        }
        optionBundle.setIp(getRemoteAddr());
        optionBundle.setNamespace(optionDAO.getNamespace());
        optionBundle.setSpelling(ChineseUtil.getFullSpell(optionBundle.getName(), ""));
        if (StringUtil.isNull(optionBundle.getNamespace())) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.namespaceError));
            return;
        }
        optionBundle.setId(0);
        if (optionDAO.save(optionBundle) > 0) {
            addActionMessage(language.getLang(LanguageRes.saveSuccess));
        } else {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.saveFailure));
        }
    }

    @Operate(caption = "编辑")
    public void edit(@Param("参数对象") OptionBundleParam param) throws Exception {
        OptionBundle optionBundle = BeanUtil.copy(param,OptionBundle.class);
        IUserSession userSession = getUserSession();
        if (userSession != null) {
            optionBundle.setPutName(userSession.getName());
            optionBundle.setPutUid(userSession.getUid());

        }
        optionBundle.setIp(getRemoteAddr());
        optionBundle.setNamespace(optionDAO.getNamespace());
        optionBundle.setSpelling(ChineseUtil.getFullSpell(optionBundle.getName(), ""));
        if (StringUtil.isNull(optionBundle.getNamespace())) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.namespaceError));
            return;
        }
        if (optionDAO.update(optionBundle) > 0) {
            addActionMessage(language.getLang(LanguageRes.updateSuccess));
        } else {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.updateFailure));
        }
    }

    @Operate(caption = "默选")
    public void selected(@Param(caption = "ID", min = 1,required = true,message = "不允许为空") Long id) throws Exception {
        if (optionDAO.updateSelected(id)) {
            addActionMessage(language.getLang(LanguageRes.operationSuccess));
        } else {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.operationFailure));
        }
    }

    @Operate(caption = "删除")
    public void delete(@Param(caption = "ID列表", min = 1,required = true,message = "不允许为空") Long[] ids) {
        if (optionDAO.delete(ids)) {
            addActionMessage(language.getLang(LanguageRes.deleteSuccess));
        } else {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.deleteFailure));
        }
    }

    @Operate(caption = "提前")
    public void sortDate(@Param(caption = "ID列表", min = 1,required = true,message = "不允许为空") Long[] ids)  {

        if (ArrayUtil.isEmpty(ids)) {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.needSelect));
            return;
        }
        boolean del = optionDAO.updateSortDate(ids);
        if (del) {
            addActionMessage(language.getLang(LanguageRes.operationSuccess));
        } else {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.operationFailure));
        }
    }

    @Operate(caption = "排序")
    public void sortType(@Param(caption = "ID列表", min = 1,required = true,message = "不允许为空") Long[] ids) {
        boolean del = optionDAO.updateSortType(ids, getInt("sortType", 0));
        if (del) {
            addActionMessage(language.getLang(LanguageRes.operationSuccess));
        } else {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.operationFailure));
        }
    }

    @Operate(caption = "固顶")
    public void top(@Param(caption = "ID列表", min = 1,required = true,message = "不允许为空") Long[] ids)  {
        boolean del = optionDAO.updateSortType(ids, 2);
        if (del) {
            addActionMessage(language.getLang(LanguageRes.operationSuccess));
        } else {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.operationFailure));

        }
    }

    @Operate(caption = "取消固顶")
    public void clearSortType(@Param(caption = "ID列表", min = 1,required = true,message = "不允许为空") Long[] ids)  {
        if (optionDAO.updateSortType(ids, 0)) {
            addActionMessage(language.getLang(LanguageRes.operationSuccess));
        } else {
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.operationFailure));
        }
    }

    @Operate(caption = "导入数据库")
    public void loadStore() throws Exception {
        setActionResult(ROC);
        addActionMessage(language.getLang(LanguageRes.importData) + "," + optionDAO.storeDatabase());
    }

    @Override
    public String execute()  {
        if (isMethodInvoked()) {
            optionDAO.evict(OptionBundle.class);
        }
        return super.execute();
    }
}
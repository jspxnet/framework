/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.action;

import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.component.zhex.spell.ChineseUtil;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.model.param.OptionBundleParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.table.OptionBundle;
import com.github.jspxnet.txweb.view.OptionView;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.StringUtil;


/**
 * Created by yuan on 14-2-15.
 * 字典
 */
@HttpMethod(caption = "字典管理", actionName = "*", namespace = "/option/bundle")
@Bean(singleton = true)
public class OptionAction extends OptionView {

    @Operate(caption = "保存",method = "save")
    public RocResponse<Integer> save(@Param("参数对象") OptionBundleParam params) throws Exception {
        OptionBundle optionBundle = BeanUtil.copy(params,OptionBundle.class);
        IUserSession userSession = getUserSession();
        if (userSession != null) {
            optionBundle.setPutName(userSession.getName());
            optionBundle.setPutUid(userSession.getUid());
        }
        if (StringUtil.isNull(optionBundle.getNamespace())) {
            return RocResponse.error(ErrorEnumType.PARAMETERS.getValue(),language.getLang(LanguageRes.namespaceError));
        }

        optionBundle.setIp(getRemoteAddr());
        optionBundle.setNamespace(params.getNamespace());
        optionBundle.setSpelling(ChineseUtil.getFullSpell(optionBundle.getName(), ""));
        optionBundle.setId(0);
        if (optionDAO.save(optionBundle) > 0) {
            return RocResponse.success(1,language.getLang(LanguageRes.saveSuccess));
        } else {
            return RocResponse.error(ErrorEnumType.DATABASE.getValue(),language.getLang(LanguageRes.saveFailure));
        }
    }

    @Operate(caption = "编辑",method = "edit")
    public RocResponse<Integer> edit(@Param("参数对象") OptionBundleParam params) throws Exception {
        OptionBundle optionBundle = BeanUtil.copy(params,OptionBundle.class);
        IUserSession userSession = getUserSession();
        if (userSession != null) {
            optionBundle.setPutName(userSession.getName());
            optionBundle.setPutUid(userSession.getUid());

        }
        optionBundle.setIp(getRemoteAddr());
        optionBundle.setNamespace(params.getNamespace());
        optionBundle.setSpelling(ChineseUtil.getFullSpell(optionBundle.getName(), ""));
        if (StringUtil.isNull(optionBundle.getNamespace())) {
            return RocResponse.error(ErrorEnumType.PARAMETERS.getValue(),language.getLang(LanguageRes.namespaceError));
        }
        if (optionDAO.update(optionBundle) > 0) {
            return RocResponse.success(1,language.getLang(LanguageRes.updateSuccess));
        } else {
            return RocResponse.error(ErrorEnumType.DATABASE.getValue(),language.getLang(LanguageRes.saveFailure));
        }
    }

    @Operate(caption = "默选",method = "selected")
    public RocResponse<Integer> selected(@Param(caption = "ID", min = 1,required = true,message = "不允许为空") Long id) throws Exception {
        if (optionDAO.updateSelected(id)) {
            return RocResponse.success(1,language.getLang(LanguageRes.updateSuccess));
        } else {
            return RocResponse.error(ErrorEnumType.DATABASE.getValue(),language.getLang(LanguageRes.saveFailure));
        }
    }

    @Operate(caption = "删除",method = "delete")
    public RocResponse<Integer>  delete(@Param(caption = "ID列表", min = 1,required = true,message = "不允许为空") Long[] ids) {
        if (optionDAO.delete(ids)) {
            return RocResponse.success(1,language.getLang(LanguageRes.deleteSuccess));
        } else {
            return RocResponse.error(ErrorEnumType.DATABASE.getValue(),language.getLang(LanguageRes.deleteFailure));
        }
    }

    @Operate(caption = "提前",method = "sort/date")
    public RocResponse<Integer> sortDate(@Param(caption = "ID列表", min = 1,required = true,message = "不允许为空") Long[] ids)  {
        if (ArrayUtil.isEmpty(ids)) {
            return RocResponse.error(ErrorEnumType.PARAMETERS.getValue(),language.getLang(LanguageRes.needSelect));
        }
        if (optionDAO.updateSortDate(ids)) {
            return RocResponse.success(1,language.getLang(LanguageRes.operationSuccess));
        } else {
            return RocResponse.error(ErrorEnumType.DATABASE.getValue(),language.getLang(LanguageRes.operationFailure));
        }
    }

    @Operate(caption = "排序",method = "sort/type")
    public RocResponse<Integer>  sortType(@Param(caption = "ID列表", min = 1,required = true,message = "不允许为空") Long[] ids,
                                          @Param(caption = "排序标识", min = 1,required = true,message = "不允许为空")int sortType) {
        boolean del = optionDAO.updateSortType(ids,sortType);
        if (del) {
            return RocResponse.success(1,language.getLang(LanguageRes.operationSuccess));
        } else {
            return RocResponse.error(ErrorEnumType.DATABASE.getValue(),language.getLang(LanguageRes.operationFailure));
        }
    }

    @Operate(caption = "固顶",method = "sort/top")
    public RocResponse<Integer>  sortTop(@Param(caption = "ID列表", min = 1,required = true,message = "不允许为空") Long[] ids)  {
        boolean del = optionDAO.updateSortType(ids, 2);
        if (del) {
            return RocResponse.success(1,language.getLang(LanguageRes.operationSuccess));
        } else {
            return RocResponse.error(ErrorEnumType.DATABASE.getValue(),language.getLang(LanguageRes.operationFailure));
        }
    }

    @Operate(caption = "取消固顶",method = "sort/clear")
    public RocResponse<Integer>  clearSortType(@Param(caption = "ID列表", min = 1,required = true,message = "不允许为空") Long[] ids)  {
        if (optionDAO.updateSortType(ids, 0)) {
            return RocResponse.success(1,language.getLang(LanguageRes.operationSuccess));
        } else {
            return RocResponse.error(ErrorEnumType.DATABASE.getValue(),language.getLang(LanguageRes.operationFailure));
        }
    }

    @Operate(caption = "导入数据库",method = "load/store")
    public RocResponse<Integer> loadStore() throws Exception {
        
        return RocResponse.success(optionDAO.storeDatabase(),language.getLang(LanguageRes.importData));
    }

    @Override
    public String execute() throws Exception {
        if (isMethodInvoked()) {
            optionDAO.evict(OptionBundle.class);
        }
        return super.execute();
    }
}
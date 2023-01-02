package com.github.jspxnet.txweb.devcenter.action;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.util.TypeUtil;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.devcenter.view.TableFieldView;
import com.github.jspxnet.txweb.model.param.component.SoberColumnParam;
import com.github.jspxnet.txweb.model.param.component.TableColumnParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;

import java.lang.reflect.Type;
import java.util.List;


@HttpMethod(caption = "基础控件", actionName = "*", namespace = Environment.DEV_CENTER+"/column")
@Bean(singleton = true)
public class TableColumnAction extends TableFieldView {
    @Operate(caption = "保存字段", method = "delete")
    public RocResponse<?> delete(@Param(caption = "参数",  required = true) SoberColumnParam param) throws Exception {
        if (isGuest()) {
            return RocResponse.error(ErrorEnumType.NEED_LOGIN);
        }
        Class<?> cls = null;
        try {
            cls = ClassUtil.loadClass(param.getClassName());
        } catch (Exception e)
        {
            return RocResponse.error(ErrorEnumType.PARAMETERS);
        }
        SoberColumn soberColumn = BeanUtil.copy(param, SoberColumn.class);
        if (genericDAO.dropColumn(cls,soberColumn))
        {
            genericDAO.evictTableModels(cls);
            return RocResponse.success(param.getTableName(),language.getLang(LanguageRes.deleteSuccess));
        }

        return RocResponse.error(ErrorEnumType.DATABASE.getValue(), language.getLang(LanguageRes.deleteFailure));
    }

    @Operate(caption = "保存字段", method = "save")
    public RocResponse<?> save(@Param(caption = "参数",  required = true) SoberColumnParam param) throws Exception {
        if (isGuest()) {
            return RocResponse.error(ErrorEnumType.NEED_LOGIN);
        }

        Class<?> cls = null;
        try {
            cls = ClassUtil.loadClass(param.getClassName());
        } catch (Exception e)
        {
            return RocResponse.error(ErrorEnumType.PARAMETERS);
        }


        Type type = TypeUtil.getJavaType(param.getTypeString());

        SoberColumn soberColumn = BeanUtil.copy(param, SoberColumn.class);
        soberColumn.setClassType((Class<?>)type);
        /*
         * 1.先判断是否  存在,存在就是修改
         * 2.如果不存在就是添加
         */

        TableModels soberTable = genericDAO.getSoberTable(cls);
        boolean x =  false;
        if (soberTable.containsField(soberColumn .getName()))
        {
            //编辑
            x = genericDAO.modifyColumn(cls,soberColumn);

        } else
        {
            //添加
            x = genericDAO.addColumn(cls,soberColumn);
        }

        if (x)
        {
            genericDAO.evictTableModels(cls);
            return RocResponse.success(param.getTableName(),language.getLang(LanguageRes.saveSuccess));
        }
        return RocResponse.error(ErrorEnumType.DATABASE.getValue(), language.getLang(LanguageRes.saveFailure));
    }


}

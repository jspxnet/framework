package com.github.jspxnet.txweb.devcenter.action;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.devcenter.view.TableMetaView;
import com.github.jspxnet.txweb.enums.SafetyEnumType;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.table.meta.ControlBase;
import com.github.jspxnet.txweb.table.meta.TableMeta;
import com.github.jspxnet.utils.StringUtil;

//devcenter/meta/save/viewscript
//meta/fieldlist
@HttpMethod(caption = "表单管理", actionName = "*", namespace = Environment.DEV_CENTER+"/meta")
@Bean(singleton = true)
public class TableMetaAction extends TableMetaView {

    @Operate(caption = "删除控件", method = "delete")
    public RocResponse<Integer> delete(@Param(caption = "日志参数", required = true) long[] ids)  {
        if (isGuest()) {
            return RocResponse.error(ErrorEnumType.NEED_LOGIN);
        }
        int i = 0;
        for (long id:ids)
        {
            i = genericDAO.delete(ControlBase.class,id,true) + i;
        }
        return RocResponse.success(i);
    }




    /**
     * id为0是保存,整合vform, 有tableName,用tableName,如果没有用 formConfig.modelName
     * @param tableName 表名
     * @param viewScript 界面脚本
     * @return 是否保存成功
     * @throws Exception 异常
     */
    @Operate(caption = "vform保存界面", method = "save/vform/json")
    public RocResponse<?> saveFormScript(@Param(caption = "数据库表名", message = "表名不能为空") String tableName,
                               @Param(caption = "view脚本", level= SafetyEnumType.NONE,  required = true) String viewScript) throws Exception {
        if (isGuest()) {
            return RocResponse.error(ErrorEnumType.NEED_LOGIN);
        }
        if (!StringUtil.isJsonObject(viewScript))
        {
            return RocResponse.error(ErrorEnumType.WARN);
        }
        JSONObject json  = new JSONObject(viewScript);
        if (StringUtil.isNull(tableName))
        {
            JSONObject formConfig = json.getJSONObject("formConfig");
            tableName = formConfig.getString("modelName");
        }

        if (StringUtil.isNull(tableName))
        {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),"tableName不能为空,数据对象名称");
        }

        TableMeta tableMeta = genericDAO.get(TableMeta.class,"tableName",tableName,false);
        if (tableMeta==null)
        {
            //默认构建一个空的
            tableMeta = create(tableName);
        }
        tableMeta.setViewScript(json.toString());
        if (tableMeta.getId()==0)
        {
            int x = genericDAO.save(tableMeta,true);
            if (x>=0)
            {
                return RocResponse.success(tableMeta.getId(),language.getLang(LanguageRes.saveSuccess));
            }
            return RocResponse.error(ErrorEnumType.DATABASE.getValue(), language.getLang(LanguageRes.saveFailure));
        } else
        {
            int x = genericDAO.update(tableMeta,new String[]{"viewScript"});
            if (x>=0)
            {
                return RocResponse.success(tableMeta.getId(),language.getLang(LanguageRes.updateSuccess));
            }
            return RocResponse.error(ErrorEnumType.DATABASE.getValue(), language.getLang(LanguageRes.updateFailure));
        }
    }

    @Override
    public String execute() throws Exception {
        // clear缓存
        if (isMethodInvoked()) {
            genericDAO.evict(TableMeta.class);
        }
        return super.execute();
    }
}
